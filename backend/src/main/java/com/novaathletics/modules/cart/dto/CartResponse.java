package com.novaathletics.modules.cart.dto;
import java.math.BigDecimal; import java.util.List;
public record CartResponse(Long id, List<Item> items, BigDecimal subtotal, int itemCount){
  public record Item(Long id, Variant variant, Integer quantity, BigDecimal lineTotal){}
  public record Variant(Long id,String sku,String productName,String size,String color,BigDecimal price,String imageUrl){}
}
