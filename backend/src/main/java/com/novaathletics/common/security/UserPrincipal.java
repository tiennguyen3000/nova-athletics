package com.novaathletics.common.security;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.*;
public class UserPrincipal {
  private final Long userId; private final String email; private final Long customerId; private final Long employeeId;
  private final List<String> roles; private final List<String> permissions;
  public UserPrincipal(Long userId,String email,Long customerId,Long employeeId,List<String> roles,List<String> perms){
    this.userId=userId; this.email=email; this.customerId=customerId; this.employeeId=employeeId; this.roles=roles; this.permissions=perms;
  }
  public Long getUserId(){return userId;} public String getEmail(){return email;} public Long getCustomerId(){return customerId;} public Long getEmployeeId(){return employeeId;}
  public List<String> getRoles(){return roles;} public List<String> getPermissions(){return permissions;}
  public Collection<GrantedAuthority> authorities(){
    List<GrantedAuthority> a=new ArrayList<>();
    for(String r:roles) a.add(new SimpleGrantedAuthority("ROLE_"+r));
    for(String p:permissions) a.add(new SimpleGrantedAuthority(p));
    return a;
  }
}
