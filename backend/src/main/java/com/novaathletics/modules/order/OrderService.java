package com.novaathletics.modules.order;
import com.novaathletics.common.error.*;
import com.novaathletics.modules.cart.entity.CartItem;
import com.novaathletics.modules.cart.repository.*;
import com.novaathletics.modules.customer.repository.AddressRepository;
import com.novaathletics.modules.inventory.InventoryService;
import com.novaathletics.modules.order.entity.*;
import com.novaathletics.modules.order.repository.*;
import com.novaathletics.modules.product.entity.ProductVariant;
import com.novaathletics.modules.product.repository.*;
import com.novaathletics.modules.promotion.entity.Coupon;
import com.novaathletics.modules.promotion.repository.*;
import com.novaathletics.modules.common.IdempotencyRepository;
import com.novaathletics.modules.common.IdempotencyKey;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
@Service
public class OrderService {
  private final OrderRepository orderRepo; private final OrderItemRepository itemRepo; private final PaymentRepository paymentRepo;
  private final CartRepository cartRepo; private final CartItemRepository cartItemRepo;
  private final ProductVariantRepository variantRepo; private final ProductRepository productRepo; private final ProductImageRepository imageRepo;
  private final AddressRepository addressRepo; private final CouponRepository couponRepo; private final CouponUsageRepository couponUsageRepo;
  private final InventoryService inventoryService; private final IdempotencyRepository idemRepo;

  private static final Map<String,Set<String>> ALLOWED=Map.of(
    "PENDING", Set.of("CONFIRMED","CANCELLED"),
    "CONFIRMED", Set.of("PROCESSING","CANCELLED"),
    "PROCESSING", Set.of("PACKED"),
    "PACKED", Set.of("SHIPPED"),
    "SHIPPED", Set.of("DELIVERED"),
    "DELIVERED", Set.of("REFUNDED"),
    "CANCELLED", Set.of("REFUNDED")
  );

  public OrderService(OrderRepository or, OrderItemRepository ir, PaymentRepository pr, CartRepository cr, CartItemRepository cir, ProductVariantRepository vr, ProductRepository prodR, ProductImageRepository imR, AddressRepository ar, CouponRepository coupR, CouponUsageRepository coupUr, InventoryService invS, IdempotencyRepository idemR){
    this.orderRepo=or; this.itemRepo=ir; this.paymentRepo=pr; this.cartRepo=cr; this.cartItemRepo=cir; this.variantRepo=vr; this.productRepo=prodR; this.imageRepo=imR; this.addressRepo=ar; this.couponRepo=coupR; this.couponUsageRepo=coupUr; this.inventoryService=invS; this.idemRepo=idemR;
  }

  @Transactional
  public Map<String,Object> checkout(Long customerId, Long shippingAddressId, Long billingAddressId, String couponCode, String paymentMethod, String idempotencyKey){
    if(idempotencyKey!=null){
      var existing=idemRepo.findByIdemKey(idempotencyKey);
      if(existing.isPresent() && existing.get().getResponseBody()!=null){
        throw new BusinessException("IDEMPOTENCY_CONFLICT","Duplicate request",org.springframework.http.HttpStatus.CONFLICT);
      }
    }
    CartItem[] cartItems;
    var cart=cartRepo.findByCustomerIdAndStatus(customerId,"ACTIVE").orElseThrow(()->new BusinessException("CART_EMPTY","Cart empty",org.springframework.http.HttpStatus.BAD_REQUEST));
    var items=cartItemRepo.findByCartId(cart.getId());
    if(items.isEmpty()) throw new BusinessException("CART_EMPTY","Cart empty",org.springframework.http.HttpStatus.BAD_REQUEST);

    var shippingAddr=addressRepo.findById(shippingAddressId).orElseThrow(()->new NotFoundException("ADDRESS_NOT_FOUND","Address not found"));
    if(!shippingAddr.getCustomerId().equals(customerId)) throw new BusinessException("FORBIDDEN","Address not owned",org.springframework.http.HttpStatus.FORBIDDEN);

    // price server side
    BigDecimal subtotal=BigDecimal.ZERO;
    List<CartItem> validItems=new ArrayList<>();
    for(CartItem ci: items){
      ProductVariant v=variantRepo.findById(ci.getVariantId()).orElseThrow(()->new NotFoundException("VARIANT_NOT_FOUND","Variant not found"));
      var prod=productRepo.findById(v.getProductId()).orElseThrow();
      BigDecimal unit=v.getPriceOverride()!=null?v.getPriceOverride():(prod.getSalePrice()!=null?prod.getSalePrice():prod.getBasePrice());
      subtotal=subtotal.add(unit.multiply(BigDecimal.valueOf(ci.getQuantity())));
      validItems.add(ci);
    }

    // coupon
    BigDecimal discount=BigDecimal.ZERO; Coupon coupon=null;
    if(couponCode!=null && !couponCode.isBlank()){
      coupon=couponRepo.findByCode(couponCode).orElseThrow(()->new BusinessException("COUPON_INVALID","Invalid coupon",org.springframework.http.HttpStatus.BAD_REQUEST));
      if(!Boolean.TRUE.equals(coupon.getIsActive())) throw new BusinessException("COUPON_INVALID","Inactive",org.springframework.http.HttpStatus.BAD_REQUEST);
      if(coupon.getStartsAt()!=null && coupon.getStartsAt().isAfter(Instant.now())) throw new BusinessException("COUPON_INVALID","Not started",org.springframework.http.HttpStatus.BAD_REQUEST);
      if(coupon.getEndsAt()!=null && coupon.getEndsAt().isBefore(Instant.now())) throw new BusinessException("COUPON_EXPIRED","Expired",org.springframework.http.HttpStatus.BAD_REQUEST);
      if(coupon.getUsageLimit()!=null && coupon.getUsageCount()>=coupon.getUsageLimit()) throw new BusinessException("COUPON_INVALID","Usage limit",org.springframework.http.HttpStatus.BAD_REQUEST);
      if(coupon.getMinOrderAmount()!=null && subtotal.compareTo(coupon.getMinOrderAmount())<0) throw new BusinessException("COUPON_INVALID","Min amount not met",org.springframework.http.HttpStatus.BAD_REQUEST);
      if(coupon.getPerCustomerLimit()!=null && couponUsageRepo.countByCouponIdAndCustomerId(coupon.getId(), customerId)>=coupon.getPerCustomerLimit()) throw new BusinessException("COUPON_INVALID","Per customer limit",org.springframework.http.HttpStatus.BAD_REQUEST);
      if("PERCENTAGE".equals(coupon.getDiscountType())){
        discount=subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100));
        if(coupon.getMaxDiscountAmount()!=null && discount.compareTo(coupon.getMaxDiscountAmount())>0) discount=coupon.getMaxDiscountAmount();
      } else if("FIXED_AMOUNT".equals(coupon.getDiscountType())){
        discount=coupon.getDiscountValue();
      }
      if(discount.compareTo(subtotal)>0) discount=subtotal;
    }

    BigDecimal shippingFee=BigDecimal.valueOf(30000);
    BigDecimal grand=subtotal.subtract(discount).add(shippingFee);
    if(grand.compareTo(BigDecimal.ZERO)<0) grand=BigDecimal.ZERO;

    // create order
    Order order=new Order();
    order.setOrderNumber("NVA-"+System.currentTimeMillis()+"-"+(int)(Math.random()*10000));
    order.setCustomerId(customerId);
    order.setStatus("PENDING");
    order.setSubtotal(subtotal); order.setDiscountTotal(discount); order.setShippingFee(shippingFee); order.setGrandTotal(grand);
    order.setCouponCode(couponCode);
    order.setShippingAddress("{\"id\":"+shippingAddressId+"}");
    if(billingAddressId!=null) order.setBillingAddress("{\"id\":"+billingAddressId+"}");
    order = orderRepo.save(order);
    orderRepo.flush();
    if(order.getId()==null) throw new IllegalStateException("order id not generated after flush");

    // order items snapshot
    for(CartItem ci: validItems){
      ProductVariant v=variantRepo.findById(ci.getVariantId()).orElseThrow();
      var prod=productRepo.findById(v.getProductId()).orElseThrow();
      BigDecimal unit=v.getPriceOverride()!=null?v.getPriceOverride():(prod.getSalePrice()!=null?prod.getSalePrice():prod.getBasePrice());
      OrderItem oi=new OrderItem();
      oi.setOrderId(order.getId()); oi.setProductId(v.getProductId()); oi.setVariantId(v.getId());
      oi.setProductName(prod.getName()); oi.setSku(v.getSku());
      oi.setVariantLabel((v.getColor()!=null?v.getColor():"")+" / "+(v.getSize()!=null?v.getSize():""));
      var imgs=imageRepo.findByProductIdOrderBySortOrderAsc(prod.getId());
      oi.setImageUrl(imgs.isEmpty()?null:imgs.get(0).getUrl());
      oi.setUnitPrice(unit); oi.setQuantity(ci.getQuantity()); oi.setLineTotal(unit.multiply(BigDecimal.valueOf(ci.getQuantity())));
      itemRepo.save(oi);
    }

    // reserve inventory - pessimistic
    for(CartItem ci: validItems){
      inventoryService.reserve(ci.getVariantId(), ci.getQuantity(), order.getId(), cart.getId());
    }

    // payment pending
    Payment pay=new Payment();
    pay.setOrderId(order.getId()); pay.setPaymentNumber("PAY-"+System.currentTimeMillis()+"-"+(int)(Math.random()*1000)+"-"+order.getId());
    pay.setProvider(paymentMethod!=null?paymentMethod:"MOCK"); pay.setStatus("PENDING"); pay.setAmount(grand); pay.setIdempotencyKey(idempotencyKey);
    pay = paymentRepo.save(pay);
    paymentRepo.flush();

    // mock auto pay success
    if("MOCK".equals(pay.getProvider()) || paymentMethod==null){
      pay.setStatus("PAID"); pay.setPaidAt(Instant.now()); pay.setProviderRef("MOCK-"+UUID.randomUUID());
      pay = paymentRepo.save(pay);
      order.setStatus("CONFIRMED"); order = orderRepo.save(order);
      inventoryService.confirmReservation(order.getId());
    }

    // coupon usage
    if(coupon!=null){
      var usage=new com.novaathletics.modules.promotion.entity.CouponUsage();
      usage.setCouponId(coupon.getId()); usage.setCustomerId(customerId); usage.setOrderId(order.getId()); usage.setDiscountApplied(discount);
      couponUsageRepo.save(usage);
      coupon.setUsageCount(coupon.getUsageCount()+1); couponRepo.save(coupon);
    }

    // clear cart
    cartItemRepo.findByCartId(cart.getId()).forEach(cartItemRepo::delete);

    // idempotency store
    if(idempotencyKey!=null){
      IdempotencyKey k=new IdempotencyKey(); k.setKey(idempotencyKey); k.setMethod("POST"); k.setPath("/api/v1/checkout"); k.setResponseStatus(201); k.setResponseBody("{\"orderId\":"+order.getId()+"}"); k.setExpiresAt(Instant.now().plusSeconds(86400));
      idemRepo.save(k);
    }

    Map<String,Object> res=new HashMap<>();
    res.put("order", order); res.put("payment", pay);
    return res;
  }

  public void transition(Long orderId, String target, Long actorCustomerId){
    Order o=orderRepo.findById(orderId).orElseThrow(()->new NotFoundException("ORDER_NOT_FOUND","Order not found"));
    String cur=o.getStatus();
    Set<String> allowed=ALLOWED.getOrDefault(cur, Set.of());
    if(!allowed.contains(target)) throw new BusinessException("ORDER_INVALID_TRANSITION","Invalid transition "+cur+"->"+target, org.springframework.http.HttpStatus.CONFLICT);
    o.setStatus(target); orderRepo.save(o);
    if("CANCELLED".equals(target)){
      // release or return
      if("PENDING".equals(cur)) inventoryService.releaseReservation(orderId);
      else {
        // for CONFIRMED etc, we would RETURN - simplify as release + adjust
        inventoryService.releaseReservation(orderId);
      }
    }
  }

  public Order getForCustomer(Long orderId, Long customerId){
    Order o=orderRepo.findById(orderId).orElseThrow(()->new NotFoundException("ORDER_NOT_FOUND","Order not found"));
    if(!o.getCustomerId().equals(customerId)) throw new BusinessException("FORBIDDEN","Not your order", org.springframework.http.HttpStatus.FORBIDDEN);
    return o;
  }
}
