package com.novaathletics.modules.audit.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="audit_logs") public class AuditLog {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="user_id") private Long userId;
  @Column(nullable=false) private String action;
  @Column(name="entity_type", nullable=false) private String entityType;
  @Column(name="entity_id") private String entityId;
  @Column(name="old_value", columnDefinition="TEXT") private String oldValue;
  @Column(name="new_value", columnDefinition="TEXT") private String newValue;
  @Column(name="ip_address") private String ipAddress;
  @Column(name="user_agent") private String userAgent;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  public void setUserId(Long v){userId=v;} public void setAction(String v){action=v;} public void setEntityType(String v){entityType=v;} public void setEntityId(String v){entityId=v;} public void setOldValue(String v){oldValue=v;} public void setNewValue(String v){newValue=v;} public void setIpAddress(String v){ipAddress=v;} public void setUserAgent(String v){userAgent=v;}
  public Long getId(){return id;} public String getAction(){return action;} public Instant getCreatedAt(){return createdAt;}
}
