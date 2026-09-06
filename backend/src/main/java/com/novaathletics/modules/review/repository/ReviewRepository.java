package com.novaathletics.modules.review.repository;
import com.novaathletics.modules.review.entity.Review;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ReviewRepository extends JpaRepository<Review,Long>{ Page<Review> findByProductId(Long pid, Pageable p); Page<Review> findByProductIdAndStatus(Long pid, String status, Pageable p); }
