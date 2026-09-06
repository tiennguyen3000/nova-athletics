package com.novaathletics.modules.product;
import com.novaathletics.common.pagination.*;
import com.novaathletics.modules.product.dto.*;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/products")
public class ProductController {
  private final ProductService svc;
  public ProductController(ProductService s){this.svc=s;}
  @GetMapping
  public PageResponse<ProductListResponse> list(@RequestParam(required=false) String q, @RequestParam(required=false) String category, @RequestParam(required=false) String collection, @RequestParam(required=false) String gender, @RequestParam(required=false) String sport, @RequestParam(required=false) String color, @RequestParam(required=false) String size, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="24") int sizeP, @RequestParam(defaultValue="createdAt,desc") String sort){
    sizeP=Math.min(sizeP,100);
    String[] parts=sort.split(","); Sort s=Sort.by(parts.length>1&&parts[1].equalsIgnoreCase("asc")? Sort.Direction.ASC: Sort.Direction.DESC, parts[0]);
    Pageable pageable=PageRequest.of(page,sizeP,s);
    var pg=svc.list(q,category,collection,gender,sport,color,size,pageable);
    return PageResponse.of(pg.getContent(), pg.getNumber(), pg.getSize(), pg.getTotalElements(), pg.getTotalPages());
  }
  @GetMapping("/{slug}")
  public ApiResponse<ProductDetailResponse> detail(@PathVariable String slug){ return new ApiResponse<>(svc.detail(slug)); }
}
