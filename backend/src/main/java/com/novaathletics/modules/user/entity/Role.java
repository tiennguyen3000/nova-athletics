package com.novaathletics.modules.user.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="roles") public class Role {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String name;
  private String description;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getName(){return name;} public void setName(String v){name=v;}
  public String getDescription(){return description;} public void setDescription(String v){description=v;}
}
