package com.novaathletics.modules.user.repository;
import com.novaathletics.modules.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long>{ Optional<RefreshToken> findByTokenHash(String hash); }
