package com.novaathletics.modules.product.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="collections") public class Collection {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false) private String name;
  @Column(nullable=false) private String slug;
  @Column(columnDefinition="TEXT") private String description;
  @Column(name="image_url") private String imageUrl;
  @Column(name="is_active") private Boolean isActive=true;
  @Column(name="deleted_at") private Instant deletedAt;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public String getSlug(){return slug;} public void setSlug(String v){slug=v;}
  public String getDescription(){return description;} public void setDescription(String v){description=v;}
  public Boolean getIsActive(){return isActive;} public void setIsActive(Boolean v){isActive=v;}
}
