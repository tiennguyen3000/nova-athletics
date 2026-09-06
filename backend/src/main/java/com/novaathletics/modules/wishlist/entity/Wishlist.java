package com.novaathletics.modules.wishlist.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="wishlists") public class Wishlist {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="customer_id", nullable=false, unique=true) private Long customerId;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getCustomerId(){return customerId;} public void setCustomerId(Long v){customerId=v;}
}
