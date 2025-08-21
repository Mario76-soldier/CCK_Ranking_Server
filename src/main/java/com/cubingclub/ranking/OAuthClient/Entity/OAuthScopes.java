package com.cubingclub.ranking.OAuthClient.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.oidc.OidcScopes;

@Entity
@Getter
@NoArgsConstructor
@Table
@Builder
@AllArgsConstructor
public class OAuthScopes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Column
    private Long idx;
    @ManyToOne
    @JsonIgnore
    @JoinColumn
    private OAuthClient oAuthClient;
    @Column
    private String scope;
}
