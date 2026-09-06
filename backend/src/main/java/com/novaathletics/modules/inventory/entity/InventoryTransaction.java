package com.novaathletics.modules.inventory.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="inventory_transactions") public class InventoryTransaction {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="variant_id", nullable=false) private Long variantId;
  @Column(name="warehouse_id", nullable=false) private Long warehouseId;
  @Column(nullable=false) private String type;
  @Column(nullable=false) private Integer quantity;
  @Column(name="reference_type") private String referenceType;
  @Column(name="reference_id") private Long referenceId;
  private String note;
  @Column(name="created_by") private Long createdBy;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  public Long getId(){return id;} public void setVariantId(Long v){variantId=v;} public Long getVariantId(){return variantId;}
  public void setWarehouseId(Long v){warehouseId=v;} public Long getWarehouseId(){return warehouseId;}
  public void setType(String v){type=v;} public String getType(){return type;}
  public void setQuantity(Integer v){quantity=v;} public Integer getQuantity(){return quantity;}
  public void setReferenceType(String v){referenceType=v;} public void setReferenceId(Long v){referenceId=v;}
  public void setNote(String v){note=v;} public void setCreatedBy(Long v){createdBy=v;}
}
