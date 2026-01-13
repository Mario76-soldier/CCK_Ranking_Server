package com.cubingclub.ranking.Auth.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    @Value("${spring.oauth.uri}")
    private String tokenUrl;
    @Value("${spring.oauth.id}")
    private String clientId;
    @Value("${spring.oauth.secret}")
    private String clientSecret;
    @Value("${spring.oauth.redirect-uri}")
    private String clientRedirect;
    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/token")
    public ResponseEntity<?> exchangeTokenForRanking(@RequestParam(name = "code")String code){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", clientRedirect);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl+"/oauth2/token", request, String.class);

        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshTokenForRanking(@RequestParam(name = "refresh")String refresh){
        HttpHeaders headers=new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refresh);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl+"/oauth2/token", request, String.class);

        return ResponseEntity.ok(response.getBody());
    }
}