package com.novaathletics.modules.notification.repository;
import com.novaathletics.modules.notification.entity.Notification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NotificationRepository extends JpaRepository<Notification,Long>{ Page<Notification> findByUserId(Long uid, Pageable p); }
