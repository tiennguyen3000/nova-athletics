package com.novaathletics.modules.promotion.repository;
import com.novaathletics.modules.promotion.entity.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CouponUsageRepository extends JpaRepository<CouponUsage,Long>{ long countByCouponIdAndCustomerId(Long cid, Long custId); List<CouponUsage> findByOrderId(Long oid); }
