package com.novaathletics.modules.order.repository;
import com.novaathletics.modules.order.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment,Long>{ Optional<Payment> findByOrderId(Long oid); Optional<Payment> findByIdempotencyKey(String k); }
