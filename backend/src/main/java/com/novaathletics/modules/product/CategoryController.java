package com.novaathletics.modules.product;
import com.novaathletics.common.pagination.ApiResponse;
import com.novaathletics.modules.product.entity.Category;
import com.novaathletics.modules.product.repository.CategoryRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/v1/categories")
public class CategoryController {
  private final CategoryRepository repo;
  public CategoryController(CategoryRepository r){this.repo=r;}
  @GetMapping public ApiResponse<List<Category>> list(){ return new ApiResponse<>(repo.findAll()); }
  @GetMapping("/{slug}") public ApiResponse<Category> bySlug(@PathVariable String slug){ return new ApiResponse<>(repo.findBySlug(slug).orElseThrow(()->new com.novaathletics.common.error.NotFoundException("CATEGORY_NOT_FOUND","Category not found"))); }
}
