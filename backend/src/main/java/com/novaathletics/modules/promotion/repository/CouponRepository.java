package com.novaathletics.modules.promotion.repository;
import com.novaathletics.modules.promotion.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CouponRepository extends JpaRepository<Coupon,Long>{ Optional<Coupon> findByCode(String code); }
