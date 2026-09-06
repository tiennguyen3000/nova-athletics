package com.novaathletics.common.error;
import org.springframework.http.HttpStatus;
public class BusinessException extends RuntimeException {
  private final String code; private final HttpStatus status;
  public BusinessException(String code,String msg,HttpStatus s){ super(msg); this.code=code; this.status=s; }
  public String getCode(){return code;} public HttpStatus getStatus(){return status;}
}
