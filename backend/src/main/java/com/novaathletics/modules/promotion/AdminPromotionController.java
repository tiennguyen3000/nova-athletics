package com.novaathletics.modules.promotion;
import com.novaathletics.common.pagination.ApiResponse;
import com.novaathletics.modules.promotion.entity.Coupon;
import com.novaathletics.modules.promotion.repository.CouponRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/admin/promotions")
public class AdminPromotionController {
  private final CouponRepository repo;
  public AdminPromotionController(CouponRepository r){this.repo=r;}
  @GetMapping @PreAuthorize("hasAuthority('PROMOTION_MANAGE')") public ApiResponse<List<Coupon>> list(){ return new ApiResponse<>(repo.findAll()); }
  @PostMapping @PreAuthorize("hasAuthority('PROMOTION_MANAGE')") public ApiResponse<Coupon> create(@RequestBody Coupon c){ return new ApiResponse<>(repo.save(c)); }
  @PutMapping("/{id}") @PreAuthorize("hasAuthority('PROMOTION_MANAGE')") public ApiResponse<Coupon> update(@PathVariable Long id,@RequestBody Coupon c){ var e=repo.findById(id).orElseThrow(); e.setName(c.getName()); e.setDiscountValue(c.getDiscountValue()); return new ApiResponse<>(repo.save(e)); }
}
