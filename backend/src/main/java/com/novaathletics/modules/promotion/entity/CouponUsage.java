package com.novaathletics.modules.promotion.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="coupon_usages") public class CouponUsage {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="coupon_id", nullable=false) private Long couponId;
  @Column(name="customer_id", nullable=false) private Long customerId;
  @Column(name="order_id", nullable=false) private Long orderId;
  @Column(name="discount_applied", nullable=false) private BigDecimal discountApplied;
  @Column(name="used_at") private Instant usedAt=Instant.now();
  public void setCouponId(Long v){couponId=v;} public void setCustomerId(Long v){customerId=v;} public void setOrderId(Long v){orderId=v;} public void setDiscountApplied(BigDecimal v){discountApplied=v;}
  public Long getCouponId(){return couponId;} public Long getOrderId(){return orderId;}
}
