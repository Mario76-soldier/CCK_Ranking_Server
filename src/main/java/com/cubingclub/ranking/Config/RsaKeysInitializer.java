package com.cubingclub.ranking.Config;

import com.cubingclub.ranking.OAuthClient.Service.OAuthClientService;
import com.cubingclub.ranking.RsaKeys.Repository.RsaKeysRepository;
import com.cubingclub.ranking.RsaKeys.Service.RsaKeysService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
/**
@Component
@RequiredArgsConstructor
public class RsaKeysInitializer {

    private final OAuthClientService oAuthClientService;
    private final RsaKeysService rsaKeysService;

    @PostConstruct
    public void initiator() throws Exception {
        // 1. RSA Keys
        String keyId = "bomkey";
        rsaKeysService.saveRsaKeys(keyId);
        System.out.println("✅ RSA Keys created for: " + keyId);

        // 2. OAuth Client
        String clientId = "cck_ranking";
        oAuthClientService.saveOAuthClient(clientId,"{noop}secret", "http://localhost:3000/login/confirm", "http://localhost:3000");
        System.out.println("✅ OAuth Client created: " + clientId);
    }
}
 **/