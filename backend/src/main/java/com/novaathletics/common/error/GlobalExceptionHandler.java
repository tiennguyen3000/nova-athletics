package com.novaathletics.common.error;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBiz(BusinessException ex, HttpServletRequest req){
    String trace = traceId();
    return ResponseEntity.status(ex.getStatus()).body(new ErrorResponse(ApiError.of(ex.getCode(),ex.getMessage(),null,trace)));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleVal(MethodArgumentNotValidException ex){
    Map<String,String> fields=new LinkedHashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(fe->fields.put(fe.getField(),fe.getDefaultMessage()));
    return ResponseEntity.badRequest().body(new ErrorResponse(ApiError.of("VALIDATION_ERROR","Validation failed",Map.of("fieldErrors",fields),traceId())));
  }
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleDenied(AccessDeniedException ex){ return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ApiError.of("FORBIDDEN","Forbidden",null,traceId()))); }
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAny(Exception ex){ ex.printStackTrace(); return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ApiError.of("INTERNAL_ERROR","Internal error",null,traceId()))); }
  private String traceId(){ String t=MDC.get("traceId"); return t!=null?t:UUID.randomUUID().toString(); }
}
