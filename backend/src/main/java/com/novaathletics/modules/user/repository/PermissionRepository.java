package com.novaathletics.modules.user.repository;
import com.novaathletics.modules.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PermissionRepository extends JpaRepository<Permission,Long>{}
