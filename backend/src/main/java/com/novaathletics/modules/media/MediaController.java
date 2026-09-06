package com.novaathletics.modules.media;
import com.novaathletics.common.pagination.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/v1/admin/media")
public class MediaController {
  @PostMapping("/presign") public ApiResponse<Map<String,String>> presign(@RequestBody Map<String,String> body){ return new ApiResponse<>(Map.of("url","https://minio.local/presigned","key","uploads/"+body.getOrDefault("fileName","file.jpg"))); }
}
