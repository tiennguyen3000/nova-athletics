package com.novaathletics.modules.product.repository;
import com.novaathletics.modules.product.entity.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface ProductRepository extends JpaRepository<Product,Long> {
  Optional<Product> findBySlug(String slug);
  @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL AND p.status='ACTIVE' AND (CAST(:q AS string) IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%',CAST(:q AS string),'%')) OR LOWER(p.subtitle) LIKE LOWER(CONCAT('%',CAST(:q AS string),'%')) ) AND (CAST(:gender AS string) IS NULL OR p.gender=:gender) AND (CAST(:sport AS string) IS NULL OR p.sport=:sport)")
  Page<Product> search(@Param("q") String q,@Param("gender") String gender,@Param("sport") String sport, Pageable pageable);
  Page<Product> findByDeletedAtIsNullAndStatus(String status, Pageable pageable);
}
