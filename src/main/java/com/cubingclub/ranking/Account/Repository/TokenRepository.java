package com.cubingclub.ranking.Account.Repository;

import com.cubingclub.ranking.Account.Entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    default Boolean existsByInvalidRefreshToken(String refreshToken) {
        return findByInvalidRefreshToken(refreshToken).isPresent();
    }
    Optional<Token> findByInvalidRefreshToken(String refreshToken);
}
