package com.novaathletics.modules.notification.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="notifications") public class Notification {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="user_id", nullable=false) private Long userId;
  @Column(nullable=false) private String type;
  @Column(nullable=false) private String title;
  @Column(columnDefinition="TEXT") private String body;
  @Column(columnDefinition="TEXT") private String data;
  @Column(name="is_read") private Boolean isRead=false;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  public void setUserId(Long v){userId=v;} public Long getUserId(){return userId;}
  public void setType(String v){type=v;} public String getType(){return type;}
  public void setTitle(String v){title=v;} public String getTitle(){return title;}
  public void setBody(String v){body=v;} public void setData(String v){data=v;}
  public void setIsRead(Boolean v){isRead=v;} public Boolean getIsRead(){return isRead;}
  public Long getId(){return id;} public Instant getCreatedAt(){return createdAt;}
}
