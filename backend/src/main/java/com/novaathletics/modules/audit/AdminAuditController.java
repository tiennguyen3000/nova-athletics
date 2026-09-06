package com.novaathletics.modules.audit;
import com.novaathletics.common.pagination.*;
import com.novaathletics.modules.audit.entity.AuditLog;
import com.novaathletics.modules.audit.repository.AuditLogRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/admin/audit-logs")
public class AdminAuditController {
  private final AuditLogRepository repo;
  public AdminAuditController(AuditLogRepository r){this.repo=r;}
  @GetMapping @PreAuthorize("hasAuthority('AUDIT_READ')") public PageResponse<AuditLog> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size){ Pageable p=PageRequest.of(page,Math.min(size,100),Sort.by(Sort.Direction.DESC,"createdAt")); var pg=repo.findAll(p); return PageResponse.of(pg.getContent(),pg.getNumber(),pg.getSize(),pg.getTotalElements(),pg.getTotalPages()); }
}
