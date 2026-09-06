package com.novaathletics.modules.product;
import com.novaathletics.common.pagination.*;
import com.novaathletics.modules.product.entity.Product;
import com.novaathletics.modules.product.repository.ProductRepository;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/v1/admin/products")
public class AdminProductController {
  private final ProductRepository repo;
  public AdminProductController(ProductRepository r){this.repo=r;}
  @GetMapping @PreAuthorize("hasAuthority('PRODUCT_READ')") public PageResponse<Product> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="24") int size){ Pageable p=PageRequest.of(page,Math.min(size,100)); var pg=repo.findAll(p); return PageResponse.of(pg.getContent(),pg.getNumber(),pg.getSize(),pg.getTotalElements(),pg.getTotalPages()); }
  @PostMapping @PreAuthorize("hasAuthority('PRODUCT_CREATE')") public ApiResponse<Product> create(@RequestBody Product prod){ prod.setId(null); return new ApiResponse<>(repo.save(prod)); }
  @GetMapping("/{id}") @PreAuthorize("hasAuthority('PRODUCT_READ')") public ApiResponse<Product> one(@PathVariable Long id){ return new ApiResponse<>(repo.findById(id).orElseThrow(()->new com.novaathletics.common.error.NotFoundException("PRODUCT_NOT_FOUND","Not found"))); }
  @PutMapping("/{id}") @PreAuthorize("hasAuthority('PRODUCT_UPDATE')") public ApiResponse<Product> update(@PathVariable Long id,@RequestBody Product prod){ var e=repo.findById(id).orElseThrow(()->new com.novaathletics.common.error.NotFoundException("PRODUCT_NOT_FOUND","Not found")); e.setName(prod.getName()); e.setSlug(prod.getSlug()); e.setBasePrice(prod.getBasePrice()); e.setSalePrice(prod.getSalePrice()); e.setStatus(prod.getStatus()); return new ApiResponse<>(repo.save(e)); }
  @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('PRODUCT_DELETE')") public ApiResponse<Map<String,String>> del(@PathVariable Long id){ var e=repo.findById(id).orElseThrow(()->new com.novaathletics.common.error.NotFoundException("PRODUCT_NOT_FOUND","Not found")); e.setDeletedAt(java.time.Instant.now()); repo.save(e); return new ApiResponse<>(Map.of("message","deleted")); }
}
