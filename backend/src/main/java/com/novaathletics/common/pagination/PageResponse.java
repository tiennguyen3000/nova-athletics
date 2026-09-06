package com.novaathletics.common.pagination;
import java.util.List;
public record PageResponse<T>(List<T> data, Meta meta){
  public record Meta(int page,int size,long totalElements,int totalPages){}
  public static <T> PageResponse<T> of(List<T> data,int page,int size,long total,int pages){ return new PageResponse<>(data,new Meta(page,size,total,pages)); }
}
