package com.novaathletics.modules.wishlist;
import com.novaathletics.common.pagination.ApiResponse;
import com.novaathletics.common.security.SecurityUtils;
import com.novaathletics.modules.wishlist.entity.WishlistItem;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/wishlist")
public class WishlistController {
  private final WishlistService svc;
  public WishlistController(WishlistService s){this.svc=s;}
  private Long cid(){ return SecurityUtils.current().orElseThrow(()->new com.novaathletics.common.error.BusinessException("UNAUTHORIZED","Unauthorized",org.springframework.http.HttpStatus.UNAUTHORIZED)).getCustomerId(); }
  @GetMapping public ApiResponse<List<WishlistItem>> list(){ Long c=cid(); if(c==null) return new ApiResponse<>(List.of()); return new ApiResponse<>(svc.list(c)); }
  @PostMapping("/items") public ApiResponse<WishlistItem> add(@RequestBody Map<String,Object> body){
    Long pid=Long.valueOf(body.get("productId").toString()); Long vid=body.get("variantId")!=null?Long.valueOf(body.get("variantId").toString()):null;
    return new ApiResponse<>(svc.add(cid(),pid,vid));
  }
  @DeleteMapping("/items/{id}") public ApiResponse<Map<String,String>> del(@PathVariable Long id){ svc.remove(cid(),id); return new ApiResponse<>(Map.of("message","removed")); }
}
