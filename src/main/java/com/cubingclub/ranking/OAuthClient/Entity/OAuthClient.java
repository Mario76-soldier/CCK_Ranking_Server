package com.cubingclub.ranking.OAuthClient.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table
@Builder
@AllArgsConstructor
public class OAuthClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;
    @Column
    private String clientId;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String clientSecret;
    @Column
    private String redirectUri;
    @Column
    private String postLogoutRedirectUri;
    @Column
    private boolean requireAuthorizationConsent;
}
