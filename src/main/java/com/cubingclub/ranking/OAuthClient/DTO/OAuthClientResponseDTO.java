package com.cubingclub.ranking.OAuthClient.DTO;

import com.cubingclub.ranking.OAuthClient.Entity.ClientType;
import com.cubingclub.ranking.OAuthClient.Entity.OAuthClient;
import com.cubingclub.ranking.OAuthClient.Entity.OAuthScopes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OAuthClientResponseDTO {
    private String clientId;
    private String clientSecret;
    private String clientName;
    private String clientUri;
    private String redirectUri;
    private String postLogoutRedirectUri;
    private List<OAuthScopes> scopes;
    private ClientType clientType;
    private LocalDateTime clientSecretExpiresAt;

    public OAuthClientResponseDTO(OAuthClient entity){
        this.clientId=entity.getClientId();
        this.clientName=entity.getClientName();
        this.clientUri=entity.getClientUri();
        this.redirectUri=entity.getRedirectUri();
        this.postLogoutRedirectUri=entity.getPostLogoutRedirectUri();
        this.scopes=entity.getScopes();
        this.clientType=entity.getClientType();
        this.clientSecretExpiresAt=entity.getClientSecretExpiresAt();
    }
    public OAuthClientResponseDTO(OAuthClient entity, String secret){
        this.clientId=entity.getClientId();
        this.clientSecret=secret;
        this.clientName=entity.getClientName();
        this.clientUri=entity.getClientUri();
        this.redirectUri=entity.getRedirectUri();
        this.postLogoutRedirectUri=entity.getPostLogoutRedirectUri();
        this.scopes=entity.getScopes();
        this.clientType=entity.getClientType();
        this.clientSecretExpiresAt=entity.getClientSecretExpiresAt();
    }
}
