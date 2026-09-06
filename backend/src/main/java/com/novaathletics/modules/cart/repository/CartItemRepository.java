package com.novaathletics.modules.cart.repository;
import com.novaathletics.modules.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CartItemRepository extends JpaRepository<CartItem,Long>{
  List<CartItem> findByCartId(Long cartId);
  Optional<CartItem> findByCartIdAndVariantId(Long cartId, Long vid);
}
