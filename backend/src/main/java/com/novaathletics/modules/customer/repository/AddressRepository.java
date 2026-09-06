package com.novaathletics.modules.customer.repository;
import com.novaathletics.modules.customer.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface AddressRepository extends JpaRepository<Address,Long>{ List<Address> findByCustomerId(Long cid); }
