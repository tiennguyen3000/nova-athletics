package com.novaathletics.modules.order;
import com.novaathletics.common.pagination.*;
import com.novaathletics.modules.order.entity.Order;
import com.novaathletics.modules.order.repository.*;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {
  private final OrderRepository orderRepo; private final OrderService orderService;
  public AdminOrderController(OrderRepository or, OrderService os){this.orderRepo=or; this.orderService=os;}
  @GetMapping @PreAuthorize("hasAuthority('ORDER_READ')") public PageResponse<Order> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size){ Pageable p=PageRequest.of(page,Math.min(size,100),Sort.by(Sort.Direction.DESC,"createdAt")); var pg=orderRepo.findAll(p); return PageResponse.of(pg.getContent(),pg.getNumber(),pg.getSize(),pg.getTotalElements(),pg.getTotalPages()); }
  @GetMapping("/{id}") @PreAuthorize("hasAuthority('ORDER_READ')") public ApiResponse<Order> one(@PathVariable Long id){ return new ApiResponse<>(orderRepo.findById(id).orElseThrow(()->new com.novaathletics.common.error.NotFoundException("ORDER_NOT_FOUND","Not found"))); }
  @PatchMapping("/{id}/status") @PreAuthorize("hasAuthority('ORDER_UPDATE')") public ApiResponse<Order> status(@PathVariable Long id,@RequestBody Map<String,String> body){ orderService.transition(id, body.get("status"), null); return new ApiResponse<>(orderRepo.findById(id).orElseThrow()); }
  @PostMapping("/{id}/cancel") @PreAuthorize("hasAuthority('ORDER_CANCEL')") public ApiResponse<Order> cancel(@PathVariable Long id){ orderService.transition(id,"CANCELLED",null); return new ApiResponse<>(orderRepo.findById(id).orElseThrow()); }
  @PostMapping("/{id}/refund") @PreAuthorize("hasAuthority('ORDER_REFUND')") public ApiResponse<Order> refund(@PathVariable Long id){ orderService.transition(id,"REFUNDED",null); return new ApiResponse<>(orderRepo.findById(id).orElseThrow()); }
}
