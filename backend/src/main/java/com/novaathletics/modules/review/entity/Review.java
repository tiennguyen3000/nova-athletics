package com.novaathletics.modules.review.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="reviews") public class Review {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="product_id", nullable=false) private Long productId;
  @Column(name="customer_id", nullable=false) private Long customerId;
  @Column(name="order_id") private Long orderId;
  @Column(nullable=false) private Integer rating;
  private String title; @Column(columnDefinition="TEXT") private String content;
  private String status="PENDING";
  @Column(name="is_verified_purchase") private Boolean isVerifiedPurchase=false;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
  public Long getCustomerId(){return customerId;} public void setCustomerId(Long v){customerId=v;}
  public Long getOrderId(){return orderId;} public void setOrderId(Long v){orderId=v;}
  public Integer getRating(){return rating;} public void setRating(Integer v){rating=v;}
  public String getTitle(){return title;} public void setTitle(String v){title=v;}
  public String getContent(){return content;} public void setContent(String v){content=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public Boolean getIsVerifiedPurchase(){return isVerifiedPurchase;} public void setIsVerifiedPurchase(Boolean v){isVerifiedPurchase=v;}
  public Instant getCreatedAt(){return createdAt;}
}
