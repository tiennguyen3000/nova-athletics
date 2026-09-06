package com.novaathletics.modules.product.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="products") public class Product {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String name;
  @Column(nullable=false) private String slug;
  private String subtitle; @Column(columnDefinition="TEXT") private String description;
  private String brand="NOVA"; private String gender; private String sport;
  @Column(name="base_price", nullable=false) private BigDecimal basePrice;
  @Column(name="sale_price") private BigDecimal salePrice;
  private String currency="VND"; private String status="ACTIVE";
  @Column(name="is_featured") private Boolean isFeatured=false;
  @Column(name="deleted_at") private Instant deletedAt;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public String getSlug(){return slug;} public void setSlug(String v){slug=v;}
  public String getSubtitle(){return subtitle;} public void setSubtitle(String v){subtitle=v;}
  public String getDescription(){return description;} public void setDescription(String v){description=v;}
  public String getBrand(){return brand;} public void setBrand(String v){brand=v;}
  public String getGender(){return gender;} public void setGender(String v){gender=v;}
  public String getSport(){return sport;} public void setSport(String v){sport=v;}
  public BigDecimal getBasePrice(){return basePrice;} public void setBasePrice(BigDecimal v){basePrice=v;}
  public BigDecimal getSalePrice(){return salePrice;} public void setSalePrice(BigDecimal v){salePrice=v;}
  public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public Boolean getIsFeatured(){return isFeatured;} public void setIsFeatured(Boolean v){isFeatured=v;}
  public Instant getDeletedAt(){return deletedAt;} public void setDeletedAt(Instant v){deletedAt=v;}
  public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
