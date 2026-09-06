package com.novaathletics.modules.auth;
import com.novaathletics.common.error.*;
import com.novaathletics.common.security.*;
import com.novaathletics.modules.customer.entity.Customer;
import com.novaathletics.modules.customer.repository.CustomerRepository;
import com.novaathletics.modules.user.entity.*;
import com.novaathletics.modules.user.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

@Service
public class AuthService {
  private final UserRepository userRepo; private final RoleRepository roleRepo;
  private final CustomerRepository customerRepo; private final RefreshTokenRepository refreshRepo;
  private final PasswordEncoder encoder; private final JwtProvider jwt;
  private final org.springframework.jdbc.core.JdbcTemplate jdbc;

  public AuthService(UserRepository ur, RoleRepository rr, CustomerRepository cr, RefreshTokenRepository rtr, PasswordEncoder enc, JwtProvider jwt, org.springframework.jdbc.core.JdbcTemplate jdbc){
    this.userRepo=ur; this.roleRepo=rr; this.customerRepo=cr; this.refreshRepo=rtr; this.encoder=enc; this.jwt=jwt; this.jdbc=jdbc;
  }

  @Transactional
  public com.novaathletics.modules.auth.dto.AuthResponse register(com.novaathletics.modules.auth.dto.RegisterRequest req){
    if(userRepo.existsByEmail(req.email())) throw new BusinessException("EMAIL_ALREADY_EXISTS","Email already exists",org.springframework.http.HttpStatus.CONFLICT);
    User u=new User(); u.setEmail(req.email()); u.setPasswordHash(encoder.encode(req.password())); u.setStatus("ACTIVE");
    userRepo.save(u);
    Customer c=new Customer(); c.setUserId(u.getId()); c.setFirstName(req.firstName()); c.setLastName(req.lastName()); c.setPhone(req.phone());
    customerRepo.save(c);
    Role role=roleRepo.findByName("CUSTOMER").orElseThrow();
    jdbc.update("INSERT INTO user_roles(user_id,role_id) VALUES(?,?)", u.getId(), role.getId());
    UserPrincipal p=toPrincipal(u,c.getId());
    String access=jwt.generateAccess(p); String refreshRaw=jwt.generateRefresh();
    saveRefresh(u.getId(), refreshRaw);
    return new com.novaathletics.modules.auth.dto.AuthResponse(u.getId(),u.getEmail(),c.getId(),access,refreshRaw,jwt.getAccessTtl());
  }

  public com.novaathletics.modules.auth.dto.AuthResponse login(com.novaathletics.modules.auth.dto.LoginRequest req){
    User u=userRepo.findByEmail(req.email()).orElseThrow(()->new BusinessException("INVALID_CREDENTIALS","Invalid credentials",org.springframework.http.HttpStatus.UNAUTHORIZED));
    if(!encoder.matches(req.password(), u.getPasswordHash())) throw new BusinessException("INVALID_CREDENTIALS","Invalid credentials",org.springframework.http.HttpStatus.UNAUTHORIZED);
    if("LOCKED".equals(u.getStatus())||"DISABLED".equals(u.getStatus())) throw new BusinessException("ACCOUNT_LOCKED","Account locked",org.springframework.http.HttpStatus.FORBIDDEN);
    u.setLastLoginAt(Instant.now()); userRepo.save(u);
    Customer c=customerRepo.findByUserId(u.getId()).orElse(null);
    UserPrincipal p=toPrincipal(u, c!=null?c.getId():null);
    String access=jwt.generateAccess(p); String refreshRaw=jwt.generateRefresh();
    saveRefresh(u.getId(), refreshRaw);
    return new com.novaathletics.modules.auth.dto.AuthResponse(u.getId(),u.getEmail(),c!=null?c.getId():null,access,refreshRaw,jwt.getAccessTtl());
  }

  public com.novaathletics.modules.auth.dto.AuthResponse refresh(String raw){
    if(raw==null||raw.isBlank()) throw new BusinessException("INVALID_REFRESH_TOKEN","Invalid refresh",org.springframework.http.HttpStatus.UNAUTHORIZED);
    String hash=sha(raw);
    RefreshToken rt=refreshRepo.findByTokenHash(hash).orElseThrow(()->new BusinessException("INVALID_REFRESH_TOKEN","Invalid refresh",org.springframework.http.HttpStatus.UNAUTHORIZED));
    if(Boolean.TRUE.equals(rt.getRevoked())) throw new BusinessException("INVALID_REFRESH_TOKEN","Revoked",org.springframework.http.HttpStatus.UNAUTHORIZED);
    if(rt.getExpiresAt().isBefore(Instant.now())) throw new BusinessException("REFRESH_TOKEN_EXPIRED","Expired",org.springframework.http.HttpStatus.UNAUTHORIZED);
    rt.setRevoked(true); refreshRepo.save(rt);
    User u=userRepo.findById(rt.getUserId()).orElseThrow();
    Customer c=customerRepo.findByUserId(u.getId()).orElse(null);
    UserPrincipal p=toPrincipal(u,c!=null?c.getId():null);
    String access=jwt.generateAccess(p); String newRaw=jwt.generateRefresh();
    saveRefresh(u.getId(), newRaw);
    return new com.novaathletics.modules.auth.dto.AuthResponse(u.getId(),u.getEmail(),c!=null?c.getId():null,access,newRaw,jwt.getAccessTtl());
  }

  public void logout(String raw){
    if(raw==null) return;
    refreshRepo.findByTokenHash(sha(raw)).ifPresent(rt->{rt.setRevoked(true); refreshRepo.save(rt);});
  }

  private void saveRefresh(Long uid,String raw){
    RefreshToken rt=new RefreshToken(); rt.setUserId(uid); rt.setTokenHash(sha(raw)); rt.setExpiresAt(Instant.now().plusSeconds(jwt.getRefreshTtl()));
    refreshRepo.save(rt);
  }
  private UserPrincipal toPrincipal(User u, Long cid){
    List<String> roles=jdbc.queryForList("SELECT r.name FROM roles r JOIN user_roles ur ON ur.role_id=r.id WHERE ur.user_id=?", String.class, u.getId());
    List<String> perms=jdbc.queryForList("SELECT p.name FROM permissions p JOIN role_permissions rp ON rp.permission_id=p.id JOIN user_roles ur ON ur.role_id=rp.role_id WHERE ur.user_id=?", String.class, u.getId());
    // employee fallback
    Long eid=null; try{ eid=jdbc.queryForObject("SELECT id FROM employees WHERE user_id=?", Long.class, u.getId()); }catch(Exception ignored){}
    return new UserPrincipal(u.getId(),u.getEmail(),cid,eid,roles,perms);
  }
  private String sha(String s){
    try{ MessageDigest md=MessageDigest.getInstance("SHA-256"); byte[] h=md.digest(s.getBytes(StandardCharsets.UTF_8)); return Base64.getEncoder().encodeToString(h);}catch(Exception e){throw new RuntimeException(e);}
  }
}
