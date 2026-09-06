package com.novaathletics.modules.review;
import com.novaathletics.common.pagination.*;
import com.novaathletics.common.security.SecurityUtils;
import com.novaathletics.modules.review.entity.Review;
import com.novaathletics.modules.review.repository.ReviewRepository;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
public class ReviewController {
  private final ReviewRepository repo;
  public ReviewController(ReviewRepository r){this.repo=r;}
  @GetMapping("/api/v1/products/{id}/reviews")
  public PageResponse<Review> list(@PathVariable Long id, @RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size){
    Pageable p=PageRequest.of(page,Math.min(size,100),Sort.by(Sort.Direction.DESC,"createdAt"));
    var pg=repo.findByProductId(id,p);
    return PageResponse.of(pg.getContent(),pg.getNumber(),pg.getSize(),pg.getTotalElements(),pg.getTotalPages());
  }
  @PostMapping("/api/v1/products/{id}/reviews")
  public ApiResponse<Review> create(@PathVariable Long id,@RequestBody Map<String,Object> body){
    Long cid=SecurityUtils.current().orElseThrow(()->new com.novaathletics.common.error.BusinessException("UNAUTHORIZED","Unauthorized",org.springframework.http.HttpStatus.UNAUTHORIZED)).getCustomerId();
    if(cid==null) throw new com.novaathletics.common.error.BusinessException("FORBIDDEN","Customer required",org.springframework.http.HttpStatus.FORBIDDEN);
    Review r=new Review(); r.setProductId(id); r.setCustomerId(cid); r.setRating(Integer.parseInt(body.get("rating").toString())); r.setTitle((String)body.get("title")); r.setContent((String)body.get("content"));
    if(body.get("orderId")!=null) r.setOrderId(Long.valueOf(body.get("orderId").toString()));
    r.setStatus("PENDING");
    return new ApiResponse<>(repo.save(r));
  }
}
