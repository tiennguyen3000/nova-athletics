package com.novaathletics.modules.product.dto;
import java.math.BigDecimal;
import java.util.List;
public record ProductListResponse(Long id,String name,String slug,String subtitle,String brand,String gender,String sport,BigDecimal basePrice,BigDecimal salePrice,String currency,Boolean isFeatured,String primaryImage,List<CatDto> categories,List<CatDto> collections,List<String> availableSizes,List<ColorDto> availableColors,Boolean inStock){
  public record CatDto(Long id,String name,String slug){}
  public record ColorDto(String color,String hex){}
}
