package com.cubingclub.ranking.OAuthClient.DTO;

import com.cubingclub.ranking.OAuthClient.Entity.ClientType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OAuthClientRequestDTO {
    private String clientId;
    private String clientName;
    private String clientUri;
    private String clientDescription;
    private String redirectUri;
    private String postLogoutRedirectUri;
    private boolean requireAuthorizationConsent;
    private ClientType clientType;  // 추가 필드
}
