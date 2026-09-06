package com.novaathletics.modules.review;
import com.novaathletics.common.pagination.*;
import com.novaathletics.modules.review.repository.ReviewRepository;
import com.novaathletics.modules.review.entity.Review;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/v1/admin/reviews")
public class AdminReviewController {
  private final ReviewRepository repo;
  public AdminReviewController(ReviewRepository r){this.repo=r;}
  @GetMapping @PreAuthorize("hasAuthority('REVIEW_MANAGE')") public PageResponse<Review> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size){ Pageable p=PageRequest.of(page,Math.min(size,100)); var pg=repo.findAll(p); return PageResponse.of(pg.getContent(),pg.getNumber(),pg.getSize(),pg.getTotalElements(),pg.getTotalPages()); }
  @PatchMapping("/{id}/status") @PreAuthorize("hasAuthority('REVIEW_MANAGE')") public ApiResponse<Review> status(@PathVariable Long id,@RequestBody Map<String,String> body){ var r=repo.findById(id).orElseThrow(); r.setStatus(body.get("status")); return new ApiResponse<>(repo.save(r)); }
}
