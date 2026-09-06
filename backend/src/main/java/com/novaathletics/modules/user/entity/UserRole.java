package com.novaathletics.modules.user.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="user_roles")
public class UserRole {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="user_id", nullable=false) private Long userId;
  @Column(name="role_id", nullable=false) private Long roleId;
  @Column(name="granted_at") private Instant grantedAt=Instant.now();
  @Column(name="granted_by") private Long grantedBy;
  public Long getId(){return id;}
  public void setUserId(Long v){userId=v;} public void setRoleId(Long v){roleId=v;}
}
