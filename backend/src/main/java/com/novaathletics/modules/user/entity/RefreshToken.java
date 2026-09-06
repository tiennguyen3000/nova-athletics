package com.novaathletics.modules.user.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="refresh_tokens") public class RefreshToken {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="user_id", nullable=false) private Long userId;
  @Column(name="token_hash", nullable=false) private String tokenHash;
  @Column(name="expires_at", nullable=false) private Instant expiresAt;
  @Column(nullable=false) private Boolean revoked=false;
  @Column(name="replaced_by") private Long replacedBy;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getUserId(){return userId;} public void setUserId(Long v){userId=v;}
  public String getTokenHash(){return tokenHash;} public void setTokenHash(String v){tokenHash=v;}
  public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant v){expiresAt=v;}
  public Boolean getRevoked(){return revoked;} public void setRevoked(Boolean v){revoked=v;}
  public Long getReplacedBy(){return replacedBy;} public void setReplacedBy(Long v){replacedBy=v;}
}
