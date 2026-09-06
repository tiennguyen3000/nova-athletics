package com.novaathletics.modules.cart.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="cart_items") public class CartItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="cart_id", nullable=false) private Long cartId;
  @Column(name="variant_id", nullable=false) private Long variantId;
  @Column(nullable=false) private Integer quantity;
  @Column(name="added_at") private Instant addedAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getCartId(){return cartId;} public void setCartId(Long v){cartId=v;}
  public Long getVariantId(){return variantId;} public void setVariantId(Long v){variantId=v;}
  public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
  public Instant getAddedAt(){return addedAt;}
}
