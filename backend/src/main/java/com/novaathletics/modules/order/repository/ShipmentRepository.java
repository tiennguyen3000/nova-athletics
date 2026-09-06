package com.novaathletics.modules.order.repository;
import com.novaathletics.modules.order.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ShipmentRepository extends JpaRepository<Shipment,Long>{ Optional<Shipment> findByOrderId(Long oid); }
