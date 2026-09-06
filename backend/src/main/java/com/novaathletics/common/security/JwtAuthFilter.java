package com.novaathletics.common.security;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtProvider jwt;
  public JwtAuthFilter(JwtProvider jwt){this.jwt=jwt;}
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String h=req.getHeader("Authorization");
    if(h!=null && h.startsWith("Bearer ")){
      try{
        var jws=jwt.parse(h.substring(7)); Claims c=jws.getPayload();
        Long uid=Long.valueOf(c.getSubject());
        String email=c.get("email",String.class);
        List<String> roles=c.get("roles",List.class)!=null?c.get("roles",List.class):List.of();
        List<String> perms=c.get("permissions",List.class)!=null?c.get("permissions",List.class):List.of();
        Long cid=c.get("customerId",Long.class); Long eid=c.get("employeeId",Long.class);
        UserPrincipal p=new UserPrincipal(uid,email,cid,eid,roles,perms);
        var auth=new UsernamePasswordAuthenticationToken(p,null,p.authorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
      }catch(Exception ignored){}
    }
    chain.doFilter(req,res);
  }
}
