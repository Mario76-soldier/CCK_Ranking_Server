package com.cubingclub.ranking.RsaKeys.Repository;

import com.cubingclub.ranking.RsaKeys.Entity.RsaKeys;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RsaKeysRepository extends JpaRepository<RsaKeys, Long> {
    Optional<RsaKeys> findByIdentifier(String identifier);
}
