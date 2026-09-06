package com.novaathletics.modules.cart;
import com.novaathletics.common.pagination.ApiResponse;
import com.novaathletics.common.security.SecurityUtils;
import com.novaathletics.modules.cart.dto.CartResponse;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/v1/cart")
public class CartController {
  private final CartService svc;
  public CartController(CartService s){this.svc=s;}
  private Long cid(){ return SecurityUtils.current().map(p->p.getCustomerId()).orElse(null); }
  @GetMapping public ApiResponse<CartResponse> get(@RequestHeader(value="X-Guest-Id", required=false) String guest){ return new ApiResponse<>(svc.view(cid(),guest)); }
  @PostMapping("/items") public ApiResponse<CartResponse> add(@RequestHeader(value="X-Guest-Id", required=false) String guest, @RequestBody Map<String,Object> body){
    Long vid=Long.valueOf(body.get("variantId").toString()); int qty=Integer.parseInt(body.get("quantity").toString());
    return new ApiResponse<>(svc.add(cid(),guest,vid,qty));
  }
  @PatchMapping("/items/{id}") public ApiResponse<CartResponse> update(@RequestHeader(value="X-Guest-Id", required=false) String guest, @PathVariable Long id, @RequestBody Map<String,Object> body){
    int qty=Integer.parseInt(body.get("quantity").toString());
    return new ApiResponse<>(svc.update(cid(),guest,id,qty));
  }
  @DeleteMapping("/items/{id}") public ApiResponse<CartResponse> del(@RequestHeader(value="X-Guest-Id", required=false) String guest, @PathVariable Long id){ return new ApiResponse<>(svc.remove(cid(),guest,id)); }
  @DeleteMapping public ApiResponse<Map<String,String>> clear(@RequestHeader(value="X-Guest-Id", required=false) String guest){ svc.clear(cid(),guest); return new ApiResponse<>(Map.of("message","cleared")); }
}
