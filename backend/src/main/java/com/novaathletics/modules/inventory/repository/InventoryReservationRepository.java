package com.novaathletics.modules.inventory.repository;
import com.novaathletics.modules.inventory.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant; import java.util.List;
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation,Long>{
  List<InventoryReservation> findByStatusAndExpiresAtBefore(String status, Instant now);
  List<InventoryReservation> findByOrderId(Long orderId);
}
