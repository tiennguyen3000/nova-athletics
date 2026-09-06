package com.novaathletics.modules.wishlist.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="wishlist_items") public class WishlistItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="wishlist_id", nullable=false) private Long wishlistId;
  @Column(name="product_id", nullable=false) private Long productId;
  @Column(name="variant_id") private Long variantId;
  @Column(name="added_at") private Instant addedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getWishlistId(){return wishlistId;} public void setWishlistId(Long v){wishlistId=v;}
  public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
  public Long getVariantId(){return variantId;} public void setVariantId(Long v){variantId=v;}
  public Instant getAddedAt(){return addedAt;}
}
