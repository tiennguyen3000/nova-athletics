package com.novaathletics.modules.analytics;
import com.novaathletics.common.pagination.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/admin/reports")
public class AnalyticsController {
  @GetMapping("/revenue") @PreAuthorize("hasAuthority('REPORT_READ')") public ApiResponse<Map<String,Object>> revenue(){ return new ApiResponse<>(Map.of("total",0)); }
  @GetMapping("/top-products") @PreAuthorize("hasAuthority('REPORT_READ')") public ApiResponse<List<Map<String,Object>>> top(){ return new ApiResponse<>(List.of()); }
  @GetMapping("/inventory") @PreAuthorize("hasAuthority('REPORT_READ')") public ApiResponse<List<Map<String,Object>>> inv(){ return new ApiResponse<>(List.of()); }
  @GetMapping("/orders") @PreAuthorize("hasAuthority('REPORT_READ')") public ApiResponse<Map<String,Object>> orders(){ return new ApiResponse<>(Map.of("total",0)); }
}
