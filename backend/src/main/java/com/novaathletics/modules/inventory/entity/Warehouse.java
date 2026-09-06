package com.novaathletics.modules.inventory.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="warehouses") public class Warehouse {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String code;
  @Column(nullable=false) private String name;
  private String address; private String city;
  @Column(name="is_active") private Boolean isActive=true;
  @Column(name="deleted_at") private Instant deletedAt;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getCode(){return code;} public void setCode(String v){code=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public String getAddress(){return address;} public void setAddress(String v){address=v;}
  public String getCity(){return city;} public void setCity(String v){city=v;}
  public Boolean getIsActive(){return isActive;} public void setIsActive(Boolean v){isActive=v;}
}
