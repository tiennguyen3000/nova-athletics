package com.novaathletics.modules.auth;
import com.novaathletics.common.pagination.ApiResponse;
import com.novaathletics.common.security.SecurityUtils;
import com.novaathletics.modules.auth.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
  private final AuthService svc;
  public AuthController(AuthService s){this.svc=s;}
  @PostMapping("/register") public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest r){ return ResponseEntity.status(201).body(new ApiResponse<>(svc.register(r))); }
  @PostMapping("/login") public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest r){ return ResponseEntity.ok(new ApiResponse<>(svc.login(r))); }
  @PostMapping("/refresh") public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody(required=false) RefreshRequest b, @CookieValue(value="refreshToken", required=false) String cookie){
    String raw=b!=null&&b.refreshToken()!=null?b.refreshToken():cookie;
    return ResponseEntity.ok(new ApiResponse<>(svc.refresh(raw)));
  }
  @PostMapping("/logout") public ResponseEntity<ApiResponse<Map<String,String>>> logout(@RequestBody(required=false) RefreshRequest b, @CookieValue(value="refreshToken", required=false) String cookie){
    String raw=b!=null&&b.refreshToken()!=null?b.refreshToken():cookie;
    svc.logout(raw); return ResponseEntity.ok(new ApiResponse<>(Map.of("message","Logged out")));
  }
  @GetMapping("/me") public ResponseEntity<ApiResponse<Object>> me(){
    var p=SecurityUtils.current().orElseThrow(()->new com.novaathletics.common.error.BusinessException("UNAUTHORIZED","Unauthorized",org.springframework.http.HttpStatus.UNAUTHORIZED));
    return ResponseEntity.ok(new ApiResponse<>(Map.of("userId",p.getUserId(),"email",p.getEmail(),"roles",p.getRoles(),"permissions",p.getPermissions(),"customerId",p.getCustomerId()!=null?p.getCustomerId():"")));
  }
  @PostMapping("/forgot-password") public ResponseEntity<ApiResponse<Map<String,String>>> forgot(@RequestBody Map<String,String> body){ return ResponseEntity.ok(new ApiResponse<>(Map.of("message","If email exists, reset link sent"))); }
  @PostMapping("/reset-password") public ResponseEntity<ApiResponse<Map<String,String>>> reset(@RequestBody Map<String,String> body){ return ResponseEntity.ok(new ApiResponse<>(Map.of("message","Password reset"))); }
}
