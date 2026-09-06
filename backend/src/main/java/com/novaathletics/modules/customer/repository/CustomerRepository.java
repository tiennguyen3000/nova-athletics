package com.novaathletics.modules.customer.repository;
import com.novaathletics.modules.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CustomerRepository extends JpaRepository<Customer,Long>{ Optional<Customer> findByUserId(Long uid); }
