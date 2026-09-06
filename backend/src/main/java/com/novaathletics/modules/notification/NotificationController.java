package com.novaathletics.modules.notification;
import com.novaathletics.common.pagination.*;
import com.novaathletics.common.security.SecurityUtils;
import com.novaathletics.modules.notification.repository.NotificationRepository;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/account/notifications")
public class NotificationController {
  private final NotificationRepository repo;
  public NotificationController(NotificationRepository r){this.repo=r;}
  @GetMapping public PageResponse<com.novaathletics.modules.notification.entity.Notification> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size){ Long uid=SecurityUtils.current().orElseThrow().getUserId(); Pageable p=PageRequest.of(page,Math.min(size,100),Sort.by(Sort.Direction.DESC,"createdAt")); var pg=repo.findByUserId(uid,p); return PageResponse.of(pg.getContent(),pg.getNumber(),pg.getSize(),pg.getTotalElements(),pg.getTotalPages()); }
}
