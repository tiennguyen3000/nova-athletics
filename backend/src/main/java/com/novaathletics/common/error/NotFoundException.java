package com.novaathletics.common.error;
import org.springframework.http.HttpStatus;
public class NotFoundException extends BusinessException {
  public NotFoundException(String code,String msg){ super(code,msg,HttpStatus.NOT_FOUND); }
}
