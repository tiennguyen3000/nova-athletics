package com.novaathletics.modules.inventory.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="inventory_reservations") public class InventoryReservation {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="variant_id", nullable=false) private Long variantId;
  @Column(name="warehouse_id", nullable=false) private Long warehouseId;
  @Column(name="order_id") private Long orderId;
  @Column(name="cart_id") private Long cartId;
  @Column(nullable=false) private Integer quantity;
  @Column(nullable=false) private String status="ACTIVE";
  @Column(name="expires_at", nullable=false) private Instant expiresAt;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getVariantId(){return variantId;} public void setVariantId(Long v){variantId=v;}
  public Long getWarehouseId(){return warehouseId;} public void setWarehouseId(Long v){warehouseId=v;}
  public Long getOrderId(){return orderId;} public void setOrderId(Long v){orderId=v;}
  public Long getCartId(){return cartId;} public void setCartId(Long v){cartId=v;}
  public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){quantity=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant v){expiresAt=v;}
}
