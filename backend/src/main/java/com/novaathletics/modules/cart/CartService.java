package com.novaathletics.modules.cart;
import com.novaathletics.common.error.*;
import com.novaathletics.modules.cart.dto.CartResponse;
import com.novaathletics.modules.cart.entity.*;
import com.novaathletics.modules.cart.repository.*;
import com.novaathletics.modules.inventory.repository.WarehouseInventoryRepository;
import com.novaathletics.modules.product.entity.ProductVariant;
import com.novaathletics.modules.product.repository.ProductVariantRepository;
import com.novaathletics.modules.product.repository.ProductRepository;
import com.novaathletics.modules.product.repository.ProductImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;
@Service
public class CartService {
  private final CartRepository cartRepo; private final CartItemRepository itemRepo;
  private final ProductVariantRepository variantRepo; private final ProductRepository productRepo;
  private final ProductImageRepository imageRepo; private final WarehouseInventoryRepository invRepo;
  public CartService(CartRepository cr, CartItemRepository ir, ProductVariantRepository vr, ProductRepository pr, ProductImageRepository imr, WarehouseInventoryRepository wir){
    this.cartRepo=cr; this.itemRepo=ir; this.variantRepo=vr; this.productRepo=pr; this.imageRepo=imr; this.invRepo=wir;
  }
  public Cart getOrCreate(Long customerId,String guestId){
    if(customerId!=null){
      return cartRepo.findByCustomerIdAndStatus(customerId,"ACTIVE").orElseGet(()->{ Cart c=new Cart(); c.setCustomerId(customerId); return cartRepo.save(c); });
    }
    if(guestId!=null){
      return cartRepo.findByGuestIdAndStatus(guestId,"ACTIVE").orElseGet(()->{ Cart c=new Cart(); c.setGuestId(guestId); return cartRepo.save(c); });
    }
    Cart c=new Cart(); return cartRepo.save(c);
  }
  public CartResponse view(Long customerId,String guestId){
    Cart cart=getOrCreate(customerId,guestId);
    return toResponse(cart);
  }
  @Transactional
  public CartResponse add(Long customerId,String guestId,Long variantId,int qty){
    if(qty<1||qty>10) throw new BusinessException("VALIDATION_ERROR","Quantity 1..10",org.springframework.http.HttpStatus.BAD_REQUEST);
    ProductVariant v=variantRepo.findById(variantId).orElseThrow(()->new NotFoundException("VARIANT_NOT_FOUND","Variant not found"));
    if(!Boolean.TRUE.equals(v.getIsActive())) throw new BusinessException("OUT_OF_STOCK","Variant inactive",org.springframework.http.HttpStatus.CONFLICT);
    int avail=invRepo.findByVariantId(variantId).stream().mapToInt(inv->inv.getAvailable()).sum();
    if(avail<qty) throw new BusinessException("OUT_OF_STOCK","Out of stock",org.springframework.http.HttpStatus.CONFLICT);
    Cart cart=getOrCreate(customerId,guestId);
    var existing=itemRepo.findByCartIdAndVariantId(cart.getId(),variantId);
    if(existing.isPresent()){
      int newQty=existing.get().getQuantity()+qty;
      if(newQty>10) throw new BusinessException("VALIDATION_ERROR","Max 10",org.springframework.http.HttpStatus.BAD_REQUEST);
      if(avail<newQty) throw new BusinessException("OUT_OF_STOCK","Out of stock",org.springframework.http.HttpStatus.CONFLICT);
      existing.get().setQuantity(newQty); itemRepo.save(existing.get());
    } else {
      CartItem ci=new CartItem(); ci.setCartId(cart.getId()); ci.setVariantId(variantId); ci.setQuantity(qty); itemRepo.save(ci);
    }
    return toResponse(cart);
  }
  @Transactional
  public CartResponse update(Long customerId,String guestId,Long itemId,int qty){
    Cart cart=getOrCreate(customerId,guestId);
    CartItem ci=itemRepo.findById(itemId).orElseThrow(()->new NotFoundException("CART_ITEM_NOT_FOUND","Item not found"));
    if(!ci.getCartId().equals(cart.getId())) throw new BusinessException("FORBIDDEN","Forbidden",org.springframework.http.HttpStatus.FORBIDDEN);
    if(qty<1||qty>10) throw new BusinessException("VALIDATION_ERROR","Quantity 1..10",org.springframework.http.HttpStatus.BAD_REQUEST);
    int avail=invRepo.findByVariantId(ci.getVariantId()).stream().mapToInt(inv->inv.getAvailable()).sum();
    if(avail<qty) throw new BusinessException("OUT_OF_STOCK","Out of stock",org.springframework.http.HttpStatus.CONFLICT);
    ci.setQuantity(qty); itemRepo.save(ci);
    return toResponse(cart);
  }
  @Transactional
  public CartResponse remove(Long customerId,String guestId,Long itemId){
    Cart cart=getOrCreate(customerId,guestId);
    CartItem ci=itemRepo.findById(itemId).orElseThrow(()->new NotFoundException("CART_ITEM_NOT_FOUND","Item not found"));
    if(!ci.getCartId().equals(cart.getId())) throw new BusinessException("FORBIDDEN","Forbidden",org.springframework.http.HttpStatus.FORBIDDEN);
    itemRepo.delete(ci);
    return toResponse(cart);
  }
  @Transactional
  public void clear(Long customerId,String guestId){
    Cart cart=getOrCreate(customerId,guestId);
    itemRepo.findByCartId(cart.getId()).forEach(itemRepo::delete);
  }
  @Transactional
  public void mergeGuestToCustomer(String guestId, Long customerId){
    if(guestId==null||customerId==null) return;
    var guest=cartRepo.findByGuestIdAndStatus(guestId,"ACTIVE").orElse(null);
    if(guest==null) return;
    Cart cust=getOrCreate(customerId,null);
    for(CartItem gi: itemRepo.findByCartId(guest.getId())){
      var existing=itemRepo.findByCartIdAndVariantId(cust.getId(), gi.getVariantId());
      if(existing.isPresent()){
        int newQty=Math.min(10, existing.get().getQuantity()+gi.getQuantity());
        existing.get().setQuantity(newQty); itemRepo.save(existing.get());
      } else {
        CartItem ni=new CartItem(); ni.setCartId(cust.getId()); ni.setVariantId(gi.getVariantId()); ni.setQuantity(gi.getQuantity()); itemRepo.save(ni);
      }
    }
    guest.setStatus("CONVERTED"); cartRepo.save(guest);
  }
  private CartResponse toResponse(Cart cart){
    var items=itemRepo.findByCartId(cart.getId());
    List<CartResponse.Item> dto=new ArrayList<>();
    BigDecimal sub=BigDecimal.ZERO;
    for(CartItem ci: items){
      ProductVariant v=variantRepo.findById(ci.getVariantId()).orElse(null);
      if(v==null) continue;
      var prod=productRepo.findById(v.getProductId()).orElse(null);
      String pname=prod!=null?prod.getName():v.getSku();
      BigDecimal price=v.getPriceOverride()!=null?v.getPriceOverride():(prod!=null && prod.getSalePrice()!=null?prod.getSalePrice():(prod!=null?prod.getBasePrice():BigDecimal.ZERO));
      var imgs=imageRepo.findByProductIdOrderBySortOrderAsc(v.getProductId());
      String img=imgs.isEmpty()?null:imgs.get(0).getUrl();
      CartResponse.Variant varDto=new CartResponse.Variant(v.getId(),v.getSku(),pname,v.getSize(),v.getColor(),price,img);
      BigDecimal line=price.multiply(BigDecimal.valueOf(ci.getQuantity()));
      sub=sub.add(line);
      dto.add(new CartResponse.Item(ci.getId(),varDto,ci.getQuantity(),line));
    }
    return new CartResponse(cart.getId(),dto,sub,dto.size());
  }
}
