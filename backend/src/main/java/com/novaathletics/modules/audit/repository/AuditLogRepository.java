package com.novaathletics.modules.audit.repository;
import com.novaathletics.modules.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.domain.*;
public interface AuditLogRepository extends JpaRepository<AuditLog,Long>{ Page<AuditLog> findByEntityType(String t, Pageable p); }
