package com.novaathletics.modules.order.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="payments") public class Payment {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="order_id", nullable=false, unique=true) private Long orderId;
  @Column(name="payment_number", nullable=false, unique=true) private String paymentNumber;
  private String provider="MOCK"; private String method;
  @Column(nullable=false) private String status="PENDING";
  @Column(nullable=false) private BigDecimal amount;
  private String currency="VND";
  @Column(name="idempotency_key") private String idempotencyKey;
  @Column(name="provider_ref") private String providerRef;
  @Column(name="paid_at") private Instant paidAt;
  @Column(name="failed_reason") private String failedReason;
  @Version private Integer version=0;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getOrderId(){return orderId;} public void setOrderId(Long v){orderId=v;}
  public String getPaymentNumber(){return paymentNumber;} public void setPaymentNumber(String v){paymentNumber=v;}
  public String getProvider(){return provider;} public void setProvider(String v){provider=v;}
  public String getMethod(){return method;} public void setMethod(String v){method=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
  public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
  public String getIdempotencyKey(){return idempotencyKey;} public void setIdempotencyKey(String v){idempotencyKey=v;}
  public String getProviderRef(){return providerRef;} public void setProviderRef(String v){providerRef=v;}
  public Instant getPaidAt(){return paidAt;} public void setPaidAt(Instant v){paidAt=v;}
  public String getFailedReason(){return failedReason;} public void setFailedReason(String v){failedReason=v;}
  public Integer getVersion(){return version;}
}
