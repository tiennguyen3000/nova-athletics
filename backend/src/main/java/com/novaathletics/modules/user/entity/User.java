package com.novaathletics.modules.user.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="users")
public class User {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(nullable=false, unique=true) private String email;
  @Column(name="password_hash", nullable=false) private String passwordHash;
  @Column(nullable=false) private String status="ACTIVE";
  @Column(name="email_verified", nullable=false) private Boolean emailVerified=false;
  @Column(name="last_login_at") private Instant lastLoginAt;
  @Column(name="deleted_at") private Instant deletedAt;
  @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt=Instant.now();
  @Column(name="updated_at", nullable=false) private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public String getEmail(){return email;} public void setEmail(String v){email=v;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public Boolean getEmailVerified(){return emailVerified;} public void setEmailVerified(Boolean v){emailVerified=v;}
  public Instant getLastLoginAt(){return lastLoginAt;} public void setLastLoginAt(Instant v){lastLoginAt=v;}
  public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
