package com.novaathletics.modules.common;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.Optional;
public interface IdempotencyRepository extends JpaRepository<IdempotencyKey,Long>{ Optional<IdempotencyKey> findByIdemKey(String key); default Optional<IdempotencyKey> findByKey(String k){return findByIdemKey(k);} }
