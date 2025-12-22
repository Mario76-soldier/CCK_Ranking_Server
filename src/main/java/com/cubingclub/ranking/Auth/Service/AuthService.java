package com.cubingclub.ranking.Auth.Service;

import com.cubingclub.ranking.Auth.DTO.JudgeInfo;
import com.cubingclub.ranking.Auth.DTO.PlayerInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${spring.oauth.uri}")
    String authUri;

    private <T> T getInfo(String token, String cckId, boolean judge, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();

        if (cckId == null || cckId.isEmpty()) {
            headers.setBearerAuth(token);
        }

        HttpEntity<?> entity = new HttpEntity<>(headers);

        StringBuilder url = new StringBuilder(authUri + "/info?type=");
        url.append(judge ? "judge" : "player");

        if (cckId != null && !cckId.isEmpty()) {
            url.append("&cckId=").append(cckId);
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<T> response = restTemplate.exchange(
                    url.toString(),
                    HttpMethod.GET,
                    entity,
                    responseType
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling auth server", e);
            return null;
        }
    }

    public PlayerInfo getPlayerInfo(String token) {
        return getInfo(token, null, true, PlayerInfo.class);
    }

    public PlayerInfo getPlayerInfo(String token, String cckId) {
        return getInfo(token, cckId, true, PlayerInfo.class);
    }

    public JudgeInfo getJudgeInfo(String token) {
        return getInfo(token, null, false, JudgeInfo.class);
    }

    public JudgeInfo getJudgeInfo(String token, String cckId) {
        return getInfo(token, cckId, false, JudgeInfo.class);
    }

    public Optional<JudgeInfo> getRFIDInfo(String token, String uid){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Collections.singletonMap("uid", uid);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        String url = authUri + "/rfid/info"; // GET으로 body 전송

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<JudgeInfo> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JudgeInfo.class
            );
            return Optional.ofNullable(response.getBody());
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("Client error from auth server: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return Optional.empty();
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            log.error("Server error from auth server: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error calling auth server", e);
            return Optional.empty();
        }
    }
}
