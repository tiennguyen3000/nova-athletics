package com.novaathletics.modules.product.entity;
import jakarta.persistence.*;
import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="product_variants") public class ProductVariant {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="product_id", nullable=false) private Long productId;
  @Column(nullable=false, unique=true) private String sku;
  private String size; private String color; @Column(name="color_hex") private String colorHex;
  @Column(name="price_override") private BigDecimal priceOverride;
  @Column(name="weight_grams") private Integer weightGrams;
  @Column(name="is_active") private Boolean isActive=true;
  @Column(name="deleted_at") private Instant deletedAt;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
  public String getSku(){return sku;} public void setSku(String v){sku=v;}
  public String getSize(){return size;} public void setSize(String v){size=v;}
  public String getColor(){return color;} public void setColor(String v){color=v;}
  public String getColorHex(){return colorHex;} public void setColorHex(String v){colorHex=v;}
  public BigDecimal getPriceOverride(){return priceOverride;} public void setPriceOverride(BigDecimal v){priceOverride=v;}
  public Integer getWeightGrams(){return weightGrams;} public void setWeightGrams(Integer v){weightGrams=v;}
  public Boolean getIsActive(){return isActive;} public void setIsActive(Boolean v){isActive=v;}
}
