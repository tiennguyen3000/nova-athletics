package com.novaathletics.modules.cart.repository;
import com.novaathletics.modules.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CartRepository extends JpaRepository<Cart,Long>{
  Optional<Cart> findByCustomerIdAndStatus(Long cid,String status);
  Optional<Cart> findByGuestIdAndStatus(String gid,String status);
}
