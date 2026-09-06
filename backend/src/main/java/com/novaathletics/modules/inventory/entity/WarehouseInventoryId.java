package com.novaathletics.modules.inventory.entity;
import java.io.Serializable; import java.util.Objects;
public class WarehouseInventoryId implements Serializable {
  private Long variantId; private Long warehouseId;
  public WarehouseInventoryId(){} public WarehouseInventoryId(Long v,Long w){variantId=v;warehouseId=w;}
  public Long getVariantId(){return variantId;} public Long getWarehouseId(){return warehouseId;}
  @Override public boolean equals(Object o){ if(this==o) return true; if(!(o instanceof WarehouseInventoryId)) return false; WarehouseInventoryId that=(WarehouseInventoryId)o; return Objects.equals(variantId,that.variantId)&&Objects.equals(warehouseId,that.warehouseId); }
  @Override public int hashCode(){ return Objects.hash(variantId,warehouseId); }
}
