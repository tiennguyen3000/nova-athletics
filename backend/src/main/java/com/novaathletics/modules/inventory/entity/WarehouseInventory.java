package com.novaathletics.modules.inventory.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="warehouse_inventory") @IdClass(WarehouseInventoryId.class) public class WarehouseInventory {
  @Id @Column(name="variant_id") private Long variantId;
  @Id @Column(name="warehouse_id") private Long warehouseId;
  @Column(name="quantity_on_hand", nullable=false) private Integer quantityOnHand=0;
  @Column(name="quantity_reserved", nullable=false) private Integer quantityReserved=0;
  @Version private Integer version=0;
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getVariantId(){return variantId;} public void setVariantId(Long v){variantId=v;}
  public Long getWarehouseId(){return warehouseId;} public void setWarehouseId(Long v){warehouseId=v;}
  public Integer getQuantityOnHand(){return quantityOnHand;} public void setQuantityOnHand(Integer v){quantityOnHand=v;}
  public Integer getQuantityReserved(){return quantityReserved;} public void setQuantityReserved(Integer v){quantityReserved=v;}
  public Integer getAvailable(){ return quantityOnHand - quantityReserved; }
  public Integer getVersion(){return version;} public void setVersion(Integer v){version=v;}
}
