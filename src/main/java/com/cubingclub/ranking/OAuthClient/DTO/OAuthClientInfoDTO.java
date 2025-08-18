package com.cubingclub.ranking.OAuthClient.DTO;

import com.cubingclub.ranking.OAuthClient.Entity.ClientType;
import com.cubingclub.ranking.OAuthClient.Entity.OAuthClient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OAuthClientInfoDTO {
    private Long id;
    private String clientId;
    private String clientName;
    private String clientUri;
    private String clientDescription;
    private String clientOwner;
    private String redirectUri;
    private String postLogoutRedirectUri;
    private ClientType clientType;  // 추가 필드
    private LocalDateTime clientSecretExpiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    public OAuthClientInfoDTO(OAuthClient entity){
        this.id=entity.getId();
        this.clientId=entity.getClientId();
        this.clientName=entity.getClientName();
        this.clientUri=entity.getClientUri();
        this.clientDescription=entity.getClientDescription();
        this.redirectUri=entity.getRedirectUri();
        this.postLogoutRedirectUri=entity.getPostLogoutRedirectUri();
        this.clientType=entity.getClientType();
        this.clientSecretExpiresAt=entity.getClientSecretExpiresAt();
        this.createdAt=entity.getCreatedAt();
        this.updatedAt=entity.getUpdatedAt();
        this.isActive=entity.isActive();
    }
}
