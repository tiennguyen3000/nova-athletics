package com.novaathletics.common.audit;
import com.novaathletics.common.security.SecurityUtils;
import com.novaathletics.modules.audit.entity.AuditLog;
import com.novaathletics.modules.audit.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
@Component @Aspect
public class AuditAspect {
  private final AuditLogRepository repo;
  public AuditAspect(AuditLogRepository r){this.repo=r;}
  @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.PatchMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
  public Object audit(ProceedingJoinPoint pjp) throws Throwable {
    Object ret=pjp.proceed();
    try{
      var req=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())!=null?((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest():null;
      String path=req!=null?req.getRequestURI():"";
      if(path.startsWith("/api/v1/admin")){
        AuditLog log=new AuditLog();
        log.setUserId(SecurityUtils.current().map(u->u.getUserId()).orElse(null));
        log.setAction(pjp.getSignature().getName());
        log.setEntityType(path);
        log.setEntityId("");
        if(req!=null){ log.setIpAddress(req.getRemoteAddr()); log.setUserAgent(req.getHeader("User-Agent")); }
        repo.save(log);
      }
    }catch(Exception ignored){}
    return ret;
  }
}
