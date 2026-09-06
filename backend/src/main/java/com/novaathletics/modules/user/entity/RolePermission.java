package com.novaathletics.modules.user.entity;
import jakarta.persistence.*;
@Entity @Table(name="role_permissions")
public class RolePermission {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="role_id", nullable=false) private Long roleId;
  @Column(name="permission_id", nullable=false) private Long permissionId;
  public void setRoleId(Long v){roleId=v;} public void setPermissionId(Long v){permissionId=v;}
}
