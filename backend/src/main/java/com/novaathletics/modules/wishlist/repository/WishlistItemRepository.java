package com.novaathletics.modules.wishlist.repository;
import com.novaathletics.modules.wishlist.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface WishlistItemRepository extends JpaRepository<WishlistItem,Long>{ List<WishlistItem> findByWishlistId(Long wid); Optional<WishlistItem> findByWishlistIdAndProductId(Long wid, Long pid); }
