package com.novaathletics.modules.common;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="idempotency_keys") public class IdempotencyKey {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="idem_key", nullable=false, unique=true) private String idemKey;
  @Column(nullable=false) private String method;
  @Column(nullable=false) private String path;
  @Column(name="response_status") private Integer responseStatus;
  @Column(name="response_body", columnDefinition="TEXT") private String responseBody;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="expires_at") private Instant expiresAt;
  public Long getId(){return id;} public String getKey(){return idemKey;} public void setKey(String v){idemKey=v;}
  public String getMethod(){return method;} public void setMethod(String v){method=v;}
  public String getPath(){return path;} public void setPath(String v){path=v;}
  public Integer getResponseStatus(){return responseStatus;} public void setResponseStatus(Integer v){responseStatus=v;}
  public String getResponseBody(){return responseBody;} public void setResponseBody(String v){responseBody=v;}
  public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant v){expiresAt=v;}
}
