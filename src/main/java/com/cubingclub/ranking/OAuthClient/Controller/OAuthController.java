package com.cubingclub.ranking.OAuthClient.Controller;

import com.cubingclub.ranking.OAuthClient.DTO.OAuthClientRequestDTO;
import com.cubingclub.ranking.OAuthClient.DTO.OAuthClientSecretDTO;
import com.cubingclub.ranking.OAuthClient.Service.OAuthClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth-client")
public class OAuthController {
    private final OAuthClientService oAuthClientService;

    @GetMapping("/")
    public ResponseEntity<?> GetClientList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(oAuthClientService.findByClientEmail(authentication.getName()));
    }
    @PostMapping("/")
    public ResponseEntity<?> CreateClient(@RequestBody OAuthClientRequestDTO oAuthClientRequestDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return ResponseEntity.ok(oAuthClientService.saveOAuthClient(oAuthClientRequestDTO, authentication.getName()));
        }
        catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    @PostMapping("/secret")
    public ResponseEntity<?> UpdateSecret(@RequestBody OAuthClientSecretDTO oAuthClientSecretDTO){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            return ResponseEntity.ok(oAuthClientService.updateSecret(oAuthClientSecretDTO.getClientId(), authentication.getName(), oAuthClientSecretDTO.getExpireDay()));
        } catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Client Should be confirmed by Admin."));
        } catch(IllegalAccessException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
