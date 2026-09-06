package com.novaathletics.modules.product.repository;
import com.novaathletics.modules.product.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CollectionRepository extends JpaRepository<Collection,Long>{ Optional<Collection> findBySlug(String slug); }
