package com.cubingclub.ranking.OAuthClient.Service;

import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.OAuthClient.DTO.OAuthClientInfoDTO;
import com.cubingclub.ranking.OAuthClient.DTO.OAuthClientRequestDTO;
import com.cubingclub.ranking.OAuthClient.DTO.OAuthClientResponseDTO;
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

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthClientService {
    private final OAuthClientRepository oAuthClientRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;

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

    public List<OAuthClientInfoDTO> findByClientEmail(String email){
        List<OAuthClient> oAuthClientList=oAuthClientRepository.findByClientOwner_Email(email);
        List<OAuthClientInfoDTO> oAuthClientInfoDTOList=new ArrayList<>();
        for(OAuthClient oAuthClient:oAuthClientList){
            oAuthClientInfoDTOList.add(new OAuthClientInfoDTO(oAuthClient));
        }
        return oAuthClientInfoDTOList;
    }

    public Map<String, List<OAuthClientInfoDTO>> findForAdmin(LocalDate date){
        List<OAuthClient> list=oAuthClientRepository.findAll();

        Map<String, List<OAuthClientInfoDTO>> result = new HashMap<>();
        result.put("confirmed", new ArrayList<>());
        result.put("waiting", new ArrayList<>());

        for(OAuthClient oAuthClient:list){
            OAuthClientInfoDTO oAuthClientInfoDTO=new OAuthClientInfoDTO(oAuthClient);
            if(oAuthClient.isActive()){
                result.get("confirmed").add(oAuthClientInfoDTO);
            }
            else{
                result.get("waiting").add(oAuthClientInfoDTO);
            }
        }
        return result;
    }

    public String makePassword(){
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom r=new SecureRandom();
        StringBuilder pw=new StringBuilder();
        for(int i=0; i<32; i++){
            int index=r.nextInt(chars.length());
            pw.append(chars.charAt(index));
        }
        return pw.toString();
    }

    public OAuthClientResponseDTO updateSecret(String clientId, String email, Integer expireDays) throws IllegalAccessException {
        OAuthClient oldClient=oAuthClientRepository.findByClientId(clientId).orElseThrow();
        if (!oldClient.isActive()) {
            throw new IllegalStateException("Client should be activated: " + oldClient.getClientId());
        }
        if(!oldClient.getClientOwner().getEmail().equals(email)){
            throw new IllegalAccessException("Forbidden: Owner not match");
        }
        String newSecret=makePassword();
        OAuthClient updatedClient = OAuthClient.builder()
                .id(oldClient.getId())  // 기존 ID 유지 → update
                .clientId(oldClient.getClientId())
                .clientSecret(bCryptPasswordEncoder.encode(newSecret)) // 새 비밀번호
                .clientName(oldClient.getClientName())
                .clientUri(oldClient.getClientUri())
                .clientDescription(oldClient.getClientDescription())
                .clientOwner(oldClient.getClientOwner())
                .redirectUri(oldClient.getRedirectUri())
                .postLogoutRedirectUri(oldClient.getPostLogoutRedirectUri())
                .requireAuthorizationConsent(oldClient.isRequireAuthorizationConsent())
                .clientType(oldClient.getClientType())
                .clientSecretExpiresAt(LocalDateTime.now().plusDays(expireDays)) // 새 만료일
                .createdAt(oldClient.getCreatedAt())
                .updatedAt(LocalDateTime.now()) // 갱신 시간
                .isActive(true)
                .build();

        return new OAuthClientResponseDTO(oAuthClientRepository.save(updatedClient), newSecret);
    }

    public OAuthClientResponseDTO updateSecretDefault(String clientId, String secret, Integer expireDays){
        OAuthClient oldClient=oAuthClientRepository.findByClientId(clientId).orElseThrow();
        OAuthClient updatedClient = OAuthClient.builder()
                .id(oldClient.getId())  // 기존 ID 유지 → update
                .clientId(oldClient.getClientId())
                .clientSecret(bCryptPasswordEncoder.encode(secret)) // 새 비밀번호
                .clientName(oldClient.getClientName())
                .clientUri(oldClient.getClientUri())
                .clientDescription(oldClient.getClientDescription())
                .clientOwner(oldClient.getClientOwner())
                .redirectUri(oldClient.getRedirectUri())
                .postLogoutRedirectUri(oldClient.getPostLogoutRedirectUri())
                .requireAuthorizationConsent(oldClient.isRequireAuthorizationConsent())
                .clientType(oldClient.getClientType())
                .clientSecretExpiresAt(LocalDateTime.now().plusDays(expireDays)) // 새 만료일
                .createdAt(oldClient.getCreatedAt())
                .updatedAt(LocalDateTime.now()) // 갱신 시간
                .isActive(true)
                .build();

        return new OAuthClientResponseDTO(oAuthClientRepository.save(updatedClient));
    }

    public OAuthClientResponseDTO updateConfirm(Long id){
        OAuthClient oldClient=oAuthClientRepository.findById(id).orElseThrow();
        OAuthClient updatedClient = OAuthClient.builder()
                .id(oldClient.getId())  // 기존 ID 유지 → update
                .clientId(oldClient.getClientId())
                .clientName(oldClient.getClientName())
                .clientUri(oldClient.getClientUri())
                .clientDescription(oldClient.getClientDescription())
                .clientOwner(oldClient.getClientOwner())
                .redirectUri(oldClient.getRedirectUri())
                .postLogoutRedirectUri(oldClient.getPostLogoutRedirectUri())
                .requireAuthorizationConsent(oldClient.isRequireAuthorizationConsent())
                .clientType(oldClient.getClientType())
                .createdAt(oldClient.getCreatedAt())
                .updatedAt(LocalDateTime.now()) // 갱신 시간
                .isActive(true)
                .build();
        return new OAuthClientResponseDTO(oAuthClientRepository.save(updatedClient));
    }

    public OAuthClient saveOAuthClient(OAuthClientRequestDTO oAuthClientRequestDTO, String email){
        log.info(email);
        if (oAuthClientRepository.findByClientId(oAuthClientRequestDTO.getClientId()).isEmpty()) {
            OAuthClient client = OAuthClient.builder()
                    .clientId(oAuthClientRequestDTO.getClientId())
                    .clientName(oAuthClientRequestDTO.getClientName())
                    .clientUri(oAuthClientRequestDTO.getClientUri())
                    .clientDescription(oAuthClientRequestDTO.getClientDescription())
                    .clientOwner(accountRepository.findByEmail(email).orElseThrow())
                    .redirectUri(oAuthClientRequestDTO.getRedirectUri())
                    .postLogoutRedirectUri(oAuthClientRequestDTO.getPostLogoutRedirectUri())
                    .requireAuthorizationConsent(oAuthClientRequestDTO.isRequireAuthorizationConsent())
                    .clientType(oAuthClientRequestDTO.getClientType())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isActive(false)
                    .build();
            return oAuthClientRepository.save(client);
        }
        return null;
    }

    public void deleteOAuthClient(Long id){
        oAuthClientRepository.deleteById(id);
    }

    private RegisteredClient loadClientByResult(OAuthClient result){
        if (!result.isActive()) {
            throw new IllegalStateException("Client should be activated: " + result.getClientId());
        }
        if (result.getClientSecretExpiresAt() != null &&
                result.getClientSecretExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Client secret expired for clientId: " + result.getClientId());
        }

        RegisteredClient.Builder builder = RegisteredClient.withId(result.getClientId())
                .clientId(result.getClientId())
                .clientSecret(result.getClientSecret())
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
