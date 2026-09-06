package com.novaathletics.modules.order.repository;
import com.novaathletics.modules.order.entity.Order;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order,Long>{
  Page<Order> findByCustomerId(Long cid, Pageable p);
  Page<Order> findByStatus(String status, Pageable p);
}
