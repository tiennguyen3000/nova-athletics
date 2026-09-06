package com.novaathletics.modules.order.entity;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="order_items") public class OrderItem {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="order_id", nullable=false) private Long orderId;
  @Column(name="product_id", nullable=false) private Long productId;
  @Column(name="variant_id", nullable=false) private Long variantId;
  @Column(name="product_name", nullable=false) private String productName;
  @Column(nullable=false) private String sku;
  @Column(name="variant_label") private String variantLabel;
  @Column(name="image_url") private String imageUrl;
  @Column(name="unit_price", nullable=false) private BigDecimal unitPrice;
  @Column(nullable=false) private Integer quantity;
  @Column(name="line_total", nullable=false) private BigDecimal lineTotal;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getOrderId(){return orderId;} public void setOrderId(Long v){orderId=v;}
  public Long getProductId(){return productId;} public void setProductId(Long v){productId=v;}
  public Long getVariantId(){return variantId;} public void setVariantId(Long v){variantId=v;}
  public String getProductName(){return productName;} public void setProductName(String v){productName=v;}
  public String getSku(){return sku;} public void setSku(String v){sku=v;}
  public String getVariantLabel(){return variantLabel;} public void setVariantLabel(String v){variantLabel=v;}
  public String getImageUrl(){return imageUrl;} public void setImageUrl(String v){imageUrl=v;}
  public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;}
  public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
  public BigDecimal getLineTotal(){return lineTotal;} public void setLineTotal(BigDecimal v){lineTotal=v;}
}
