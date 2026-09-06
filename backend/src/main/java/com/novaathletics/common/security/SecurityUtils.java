package com.novaathletics.common.security;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
public final class SecurityUtils {
  private SecurityUtils(){}
  public static Optional<UserPrincipal> current(){
    var a=SecurityContextHolder.getContext().getAuthentication();
    if(a!=null && a.getPrincipal() instanceof UserPrincipal p) return Optional.of(p);
    return Optional.empty();
  }
  public static Long currentUserId(){ return current().map(UserPrincipal::getUserId).orElse(null); }
  public static boolean hasPermission(String perm){ return current().map(p->p.getPermissions().contains(perm)).orElse(false); }
}
