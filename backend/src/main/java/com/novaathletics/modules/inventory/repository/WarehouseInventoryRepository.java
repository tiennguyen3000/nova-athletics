package com.novaathletics.modules.inventory.repository;
import com.novaathletics.modules.inventory.entity.WarehouseInventory;
import com.novaathletics.modules.inventory.entity.WarehouseInventoryId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory,WarehouseInventoryId> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT w FROM WarehouseInventory w WHERE w.variantId=:vid AND w.warehouseId=:wid")
  Optional<WarehouseInventory> findForUpdate(@Param("vid") Long vid,@Param("wid") Long wid);
  List<WarehouseInventory> findByVariantId(Long vid);
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT w FROM WarehouseInventory w WHERE w.variantId=:vid")
  List<WarehouseInventory> findByVariantForUpdate(@Param("vid") Long vid);
}
