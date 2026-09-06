package com.novaathletics.modules.order;
import com.novaathletics.common.pagination.*;
import com.novaathletics.common.security.SecurityUtils;
import com.novaathletics.modules.order.entity.*;
import com.novaathletics.modules.order.repository.*;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1")
public class OrderController {
  private final OrderService orderService; private final OrderRepository orderRepo; private final OrderItemRepository itemRepo; private final PaymentRepository paymentRepo;
  public OrderController(OrderService os, OrderRepository or, OrderItemRepository ir, PaymentRepository pr){this.orderService=os; this.orderRepo=or; this.itemRepo=ir; this.paymentRepo=pr;}
  private Long cid(){ return SecurityUtils.current().orElseThrow(()->new com.novaathletics.common.error.BusinessException("UNAUTHORIZED","Unauthorized",org.springframework.http.HttpStatus.UNAUTHORIZED)).getCustomerId(); }
  @PostMapping("/checkout/validate") public ApiResponse<Map<String,Object>> validate(@RequestBody Map<String,Object> body){ return new ApiResponse<>(Map.of("valid",true)); }
  @PostMapping("/checkout") public ApiResponse<Map<String,Object>> checkout(@RequestHeader(value="Idempotency-Key", required=false) String idem, @RequestBody Map<String,Object> body){
    Long c=cid(); if(c==null) throw new com.novaathletics.common.error.BusinessException("FORBIDDEN","Customer required",org.springframework.http.HttpStatus.FORBIDDEN);
    Long ship=body.get("shippingAddressId")!=null?Long.valueOf(body.get("shippingAddressId").toString()):null;
    Long bill=body.get("billingAddressId")!=null?Long.valueOf(body.get("billingAddressId").toString()):null;
    String coupon=(String)body.get("couponCode"); String pay=(String)body.get("paymentMethod");
    String key=idem!=null?idem:(body.get("idempotencyKey")!=null?body.get("idempotencyKey").toString():null);
    return new ApiResponse<>(orderService.checkout(c, ship, bill, coupon, pay, key));
  }
  @GetMapping("/orders") public PageResponse<Order> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size, @RequestParam(required=false) String status){
    Long c=cid(); Pageable p=PageRequest.of(page, Math.min(size,100), Sort.by(Sort.Direction.DESC,"createdAt"));
    var pg=orderRepo.findByCustomerId(c, p);
    return PageResponse.of(pg.getContent(), pg.getNumber(), pg.getSize(), pg.getTotalElements(), pg.getTotalPages());
  }
  @GetMapping("/orders/{id}") public ApiResponse<Map<String,Object>> detail(@PathVariable Long id){
    Long c=cid(); Order o=orderService.getForCustomer(id,c);
    Map<String,Object> m=new HashMap<>(); m.put("order",o); m.put("items",itemRepo.findByOrderId(id)); m.put("payment",paymentRepo.findByOrderId(id).orElse(null));
    return new ApiResponse<>(m);
  }
  @PostMapping("/orders/{id}/cancel") public ApiResponse<Map<String,Object>> cancel(@PathVariable Long id, @RequestBody(required=false) Map<String,String> body){
    Long c=cid(); Order o=orderService.getForCustomer(id,c);
    orderService.transition(id,"CANCELLED",c);
    return new ApiResponse<>(Map.of("status","CANCELLED"));
  }
}
