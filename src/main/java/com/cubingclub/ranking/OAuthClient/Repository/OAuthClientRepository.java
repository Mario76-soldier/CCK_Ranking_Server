package com.cubingclub.ranking.OAuthClient.Repository;

import com.cubingclub.ranking.OAuthClient.Entity.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OAuthClientRepository extends JpaRepository<OAuthClient, Long> {
    Optional<OAuthClient> findByClientId(String clientId);
    List<OAuthClient> findByClientOwner_CckId(String email);
}
