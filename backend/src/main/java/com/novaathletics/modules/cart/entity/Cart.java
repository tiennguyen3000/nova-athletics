package com.novaathletics.modules.cart.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="carts") public class Cart {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="customer_id") private Long customerId;
  @Column(name="guest_id") private String guestId;
  private String status="ACTIVE";
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getCustomerId(){return customerId;} public void setCustomerId(Long v){customerId=v;}
  public String getGuestId(){return guestId;} public void setGuestId(String v){guestId=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
