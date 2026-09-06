package com.novaathletics.modules.product.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="product_images") public class ProductImage {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="product_id", nullable=false) private Long productId;
  @Column(name="variant_id") private Long variantId;
  @Column(name="media_id") private Long mediaId;
  @Column(nullable=false) private String url;
  @Column(name="alt_text") private String altText;
  @Column(name="sort_order") private Integer sortOrder=0;
  @Column(name="is_primary") private Boolean isPrimary=false;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
  public Long getVariantId(){return variantId;} public void setVariantId(Long v){variantId=v;}
  public String getUrl(){return url;} public void setUrl(String v){url=v;}
  public String getAltText(){return altText;} public void setAltText(String v){altText=v;}
  public Integer getSortOrder(){return sortOrder;} public void setSortOrder(Integer v){sortOrder=v;}
  public Boolean getIsPrimary(){return isPrimary;} public void setIsPrimary(Boolean v){isPrimary=v;}
}
