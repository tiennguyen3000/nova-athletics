package com.novaathletics.common.error;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;
public record ApiError(String code, String message, @JsonInclude(JsonInclude.Include.NON_NULL) Object details, String traceId, Instant timestamp){
  public static ApiError of(String code,String msg,Object details,String traceId){ return new ApiError(code,msg,details,traceId,Instant.now()); }
}
