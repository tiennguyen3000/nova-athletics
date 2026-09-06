package com.novaathletics.modules.promotion.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="coupons") public class Coupon {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String code;
  @Column(nullable=false) private String name;
  @Column(columnDefinition="TEXT") private String description;
  @Column(name="discount_type", nullable=false) private String discountType;
  @Column(name="discount_value", nullable=false) private BigDecimal discountValue;
  @Column(name="min_order_amount") private BigDecimal minOrderAmount;
  @Column(name="max_discount_amount") private BigDecimal maxDiscountAmount;
  @Column(name="usage_limit") private Integer usageLimit;
  @Column(name="usage_count") private Integer usageCount=0;
  @Column(name="per_customer_limit") private Integer perCustomerLimit;
  @Column(name="starts_at") private Instant startsAt;
  @Column(name="ends_at") private Instant endsAt;
  @Column(name="is_active") private Boolean isActive=true;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getCode(){return code;} public void setCode(String v){code=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public String getDescription(){return description;} public void setDescription(String v){description=v;}
  public String getDiscountType(){return discountType;} public void setDiscountType(String v){discountType=v;}
  public BigDecimal getDiscountValue(){return discountValue;} public void setDiscountValue(BigDecimal v){discountValue=v;}
  public BigDecimal getMinOrderAmount(){return minOrderAmount;} public void setMinOrderAmount(BigDecimal v){minOrderAmount=v;}
  public BigDecimal getMaxDiscountAmount(){return maxDiscountAmount;} public void setMaxDiscountAmount(BigDecimal v){maxDiscountAmount=v;}
  public Integer getUsageLimit(){return usageLimit;} public void setUsageLimit(Integer v){usageLimit=v;}
  public Integer getUsageCount(){return usageCount;} public void setUsageCount(Integer v){usageCount=v;}
  public Integer getPerCustomerLimit(){return perCustomerLimit;} public void setPerCustomerLimit(Integer v){perCustomerLimit=v;}
  public Instant getStartsAt(){return startsAt;} public void setStartsAt(Instant v){startsAt=v;}
  public Instant getEndsAt(){return endsAt;} public void setEndsAt(Instant v){endsAt=v;}
  public Boolean getIsActive(){return isActive;} public void setIsActive(Boolean v){isActive=v;}
}
