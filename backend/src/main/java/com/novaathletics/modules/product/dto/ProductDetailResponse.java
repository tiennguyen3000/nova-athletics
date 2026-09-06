package com.novaathletics.modules.product.dto;
import java.math.BigDecimal; import java.util.List;
public record ProductDetailResponse(Long id,String name,String slug,String subtitle,String description,String brand,String gender,String sport,BigDecimal basePrice,BigDecimal salePrice,String currency,Boolean isFeatured,List<ImageDto> images,List<VariantDto> variants,List<AttrDto> attributes,List<String> tags,List<CatDto> categories){
  public record ImageDto(String url,String altText,Integer sortOrder,Boolean isPrimary){}
  public record VariantDto(Long id,String sku,String size,String color,String colorHex,BigDecimal price,Integer available,Boolean isActive){}
  public record AttrDto(String name,String value){}
  public record CatDto(Long id,String name,String slug){}
}
