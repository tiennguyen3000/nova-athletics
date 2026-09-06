package com.novaathletics.modules.inventory.repository;
import com.novaathletics.modules.inventory.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction,Long>{}
