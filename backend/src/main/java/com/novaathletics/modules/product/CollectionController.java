package com.novaathletics.modules.product;
import com.novaathletics.common.pagination.ApiResponse;
import com.novaathletics.modules.product.repository.CollectionRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/collections")
public class CollectionController {
  private final CollectionRepository repo;
  public CollectionController(CollectionRepository r){this.repo=r;}
  @GetMapping public ApiResponse<List<com.novaathletics.modules.product.entity.Collection>> list(){ return new ApiResponse<>(repo.findAll()); }
  @GetMapping("/{slug}") public ApiResponse<com.novaathletics.modules.product.entity.Collection> bySlug(@PathVariable String slug){ return new ApiResponse<>(repo.findBySlug(slug).orElseThrow(()->new com.novaathletics.common.error.NotFoundException("COLLECTION_NOT_FOUND","Collection not found"))); }
}
