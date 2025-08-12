package com.cubingclub.ranking.Config;

import com.cubingclub.ranking.OAuthClient.Entity.ClientType;
import com.cubingclub.ranking.OAuthClient.Repository.OAuthClientRepository;
import com.cubingclub.ranking.OAuthClient.Service.OAuthClientService;
import com.cubingclub.ranking.RsaKeys.Repository.RsaKeysRepository;
import com.cubingclub.ranking.RsaKeys.Service.RsaKeysService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RsaKeysInitializer {

    private final OAuthClientRepository oAuthClientRepository;
    private final OAuthClientService oAuthClientService;
    private final RsaKeysRepository rsaKeysRepository;
    private final RsaKeysService rsaKeysService;

    @PostConstruct
    public void initiator() throws Exception {
        // 1. RSA Keys
        String keyId = "bomkey";
        if(rsaKeysRepository.findByIdentifier(keyId).isEmpty()) {
            rsaKeysService.saveRsaKeys(keyId);
            System.out.println("✅ RSA Keys created for: " + keyId);
        }

        // 2. OAuth Client
        String clientId = "cck_ranking";
        if(oAuthClientRepository.findByClientId(clientId).isEmpty()) {
            oAuthClientService.saveOAuthClient(clientId, "{noop}secret", "http://localhost:3000/login/confirm", "http://localhost:8080/", ClientType.NORMAL);
            System.out.println("✅ OAuth Client created: " + clientId);
        }
        clientId = "cck_recorder";
        if(oAuthClientRepository.findByClientId(clientId).isEmpty()) {
            oAuthClientService.saveOAuthClient(clientId, "{noop}secret", "", "", ClientType.IOT);
            System.out.println("✅ OAuth Client created: " + clientId);
        }
    }
}