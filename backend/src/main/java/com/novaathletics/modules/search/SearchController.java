package com.novaathletics.modules.search;
import com.novaathletics.common.pagination.PageResponse;
import com.novaathletics.modules.product.ProductService;
import com.novaathletics.modules.product.dto.ProductListResponse;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/search")
public class SearchController {
  private final ProductService svc;
  public SearchController(ProductService s){this.svc=s;}
  @GetMapping
  public PageResponse<ProductListResponse> search(@RequestParam String q, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="24") int size, @RequestParam(defaultValue="createdAt,desc") String sort){
    size=Math.min(size,100);
    String[] parts=sort.split(","); Sort s=Sort.by(parts.length>1&&parts[1].equalsIgnoreCase("asc")? Sort.Direction.ASC: Sort.Direction.DESC, parts[0]);
    Pageable pageable=PageRequest.of(page,size,s);
    var pg=svc.list(q,null,null,null,null,null,null,pageable);
    return PageResponse.of(pg.getContent(), pg.getNumber(), pg.getSize(), pg.getTotalElements(), pg.getTotalPages());
  }
}
