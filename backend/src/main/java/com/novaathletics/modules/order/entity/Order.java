package com.novaathletics.modules.order.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="orders") public class Order {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="order_number", nullable=false, unique=true) private String orderNumber;
  @Column(name="customer_id", nullable=false) private Long customerId;
  @Column(nullable=false) private String status="PENDING";
  @Column(nullable=false) private BigDecimal subtotal;
  @Column(name="discount_total") private BigDecimal discountTotal=BigDecimal.ZERO;
  @Column(name="shipping_fee") private BigDecimal shippingFee=BigDecimal.ZERO;
  @Column(name="tax_total") private BigDecimal taxTotal=BigDecimal.ZERO;
  @Column(name="grand_total", nullable=false) private BigDecimal grandTotal;
  private String currency="VND";
  @Column(name="coupon_code") private String couponCode;
  @Column(name="shipping_address", columnDefinition="TEXT") private String shippingAddress;
  @Column(name="billing_address", columnDefinition="TEXT") private String billingAddress;
  private String notes; @Column(name="cancelled_reason") private String cancelledReason;
  @Version private Integer version=0;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getOrderNumber(){return orderNumber;} public void setOrderNumber(String v){orderNumber=v;}
  public Long getCustomerId(){return customerId;} public void setCustomerId(Long v){customerId=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal v){subtotal=v;}
  public BigDecimal getDiscountTotal(){return discountTotal;} public void setDiscountTotal(BigDecimal v){discountTotal=v;}
  public BigDecimal getShippingFee(){return shippingFee;} public void setShippingFee(BigDecimal v){shippingFee=v;}
  public BigDecimal getTaxTotal(){return taxTotal;} public void setTaxTotal(BigDecimal v){taxTotal=v;}
  public BigDecimal getGrandTotal(){return grandTotal;} public void setGrandTotal(BigDecimal v){grandTotal=v;}
  public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
  public String getCouponCode(){return couponCode;} public void setCouponCode(String v){couponCode=v;}
  public String getShippingAddress(){return shippingAddress;} public void setShippingAddress(String v){shippingAddress=v;}
  public String getBillingAddress(){return billingAddress;} public void setBillingAddress(String v){billingAddress=v;}
  public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
  public String getCancelledReason(){return cancelledReason;} public void setCancelledReason(String v){cancelledReason=v;}
  public Integer getVersion(){return version;} public void setVersion(Integer v){version=v;}
  public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
