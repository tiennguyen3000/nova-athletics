package com.novaathletics.common.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Component
public class JwtProvider {
  private final SecretKey key; private final long accessTtl; private final long refreshTtl;
  public JwtProvider(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.access-ttl}") long at, @Value("${app.jwt.refresh-ttl}") long rt){
    this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); this.accessTtl=at; this.refreshTtl=rt;
  }
  public String generateAccess(UserPrincipal p){
    long now=System.currentTimeMillis();
    return Jwts.builder().subject(String.valueOf(p.getUserId())).claim("email",p.getEmail())
      .claim("roles",p.getRoles()).claim("permissions",p.getPermissions())
      .claim("customerId",p.getCustomerId()).claim("employeeId",p.getEmployeeId())
      .issuedAt(new Date(now)).expiration(new Date(now+accessTtl*1000)).id(UUID.randomUUID().toString())
      .signWith(key).compact();
  }
  public String generateRefresh(){ return UUID.randomUUID().toString().replace("-","")+UUID.randomUUID().toString().replace("-",""); }
  public Jws<Claims> parse(String token){ return Jwts.parser().verifyWith(key).build().parseSignedClaims(token); }
  public long getAccessTtl(){return accessTtl;} public long getRefreshTtl(){return refreshTtl;}
}
