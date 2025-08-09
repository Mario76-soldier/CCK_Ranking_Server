package com.cubingclub.ranking.OAuthClient.Service;

import com.cubingclub.ranking.OAuthClient.Entity.ClientType;
import com.cubingclub.ranking.OAuthClient.Entity.OAuthClient;
import com.cubingclub.ranking.OAuthClient.Repository.OAuthClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthClientService {
    private final OAuthClientRepository oAuthClientRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisteredClient loadClientByClientId(String clientId){
        OAuthClient result=oAuthClientRepository.findByClientId(clientId)
                .orElseThrow(()->new IllegalArgumentException("Client not found exception: "+clientId));
        return loadClientByResult(result);
    }

    public RegisteredClient findByIdString(String id){
        Long longId=Long.valueOf(id);
        OAuthClient result=oAuthClientRepository.findById(longId).get();
        return loadClientByResult(result);
    }

    public OAuthClient saveOAuthClient(String clientId, String clientSecret, String redirectUri, String postLogoutRedirectUri, ClientType clientType){
        if (oAuthClientRepository.findByClientId(clientId).isEmpty()) {
            OAuthClient client = OAuthClient.builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .redirectUri(redirectUri)
                    .postLogoutRedirectUri(postLogoutRedirectUri)
                    .requireAuthorizationConsent(true)
                    .clientType(clientType)
                    .build();
            return oAuthClientRepository.save(client);
        }
        return null;
    }

    private RegisteredClient loadClientByResult(OAuthClient result){
        RegisteredClient.Builder builder = RegisteredClient.withId(result.getClientId())
                .clientId(result.getClientId())
                .clientSecret(bCryptPasswordEncoder.encode(result.getClientSecret()))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

        if(result.getClientType()==ClientType.IOT){
            builder
                    .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                    .scope("limited.read")
                    .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build());
        }
        else{
            builder
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                    .redirectUri(result.getRedirectUri())
                    .postLogoutRedirectUri(result.getPostLogoutRedirectUri())
                    .scope(OidcScopes.OPENID)
                    .scope(OidcScopes.PROFILE)
                    .clientSettings(ClientSettings.builder()
                            .requireAuthorizationConsent(result.isRequireAuthorizationConsent())
                            .build());
        }
        return builder.build();
    }
}
