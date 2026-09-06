package com.novaathletics.modules.order.repository;
import com.novaathletics.modules.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface OrderItemRepository extends JpaRepository<OrderItem,Long>{ List<OrderItem> findByOrderId(Long oid); }
