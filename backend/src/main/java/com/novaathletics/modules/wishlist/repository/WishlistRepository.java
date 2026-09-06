package com.novaathletics.modules.wishlist.repository;
import com.novaathletics.modules.wishlist.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface WishlistRepository extends JpaRepository<Wishlist,Long>{ Optional<Wishlist> findByCustomerId(Long cid); }
