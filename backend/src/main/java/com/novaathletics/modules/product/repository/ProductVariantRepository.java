package com.novaathletics.modules.product.repository;
import com.novaathletics.modules.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long>{ List<ProductVariant> findByProductId(Long pid); }
