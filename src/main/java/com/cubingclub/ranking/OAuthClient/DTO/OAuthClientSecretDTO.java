package com.cubingclub.ranking.OAuthClient.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthClientSecretDTO {
    private String clientId;
    private Integer expireDay;
}