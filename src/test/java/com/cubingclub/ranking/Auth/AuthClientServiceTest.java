package com.cubingclub.ranking.Auth;

import com.cubingclub.ranking.Auth.DTO.AccountDTO;
import com.cubingclub.ranking.Auth.DTO.PlayerInfo;
import com.cubingclub.ranking.Auth.Service.AuthClientService;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthClient Service 테스트")
class AuthClientServiceTest {

    @InjectMocks
    private AuthClientService authClientService;

    @Mock
    private RestTemplate restTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authClientService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(authClientService, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(authClientService, "authUri", "http://auth-server");
        ReflectionTestUtils.setField(authClientService, "clientId", "test-client-id");
        ReflectionTestUtils.setField(authClientService, "clientSecret", "test-client-secret");
        ReflectionTestUtils.setField(authClientService, "clientRedirect", "http://localhost/callback");
    }

    @Test
    @DisplayName("인증 코드로 토큰 교환 성공")
    void exchangeCodeForTokenSuccess() throws JsonProcessingException {
        // Given
        String code = "authorization-code";
        Map<String, Object> expectedTokenMap = new HashMap<>();
        expectedTokenMap.put("access_token", "test-access-token");
        expectedTokenMap.put("refresh_token", "test-refresh-token");
        expectedTokenMap.put("token_type", "Bearer");
        expectedTokenMap.put("expires_in", 3600);

        String responseBody = objectMapper.writeValueAsString(expectedTokenMap);
        ResponseEntity<String> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // When
        Map<String, Object> result = authClientService.exchangeCodeForToken(code);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("access_token")).isEqualTo("test-access-token");
        assertThat(result.get("refresh_token")).isEqualTo("test-refresh-token");
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("리프레시 토큰으로 액세스 토큰 갱신 성공")
    void refreshAccessTokenSuccess() throws JsonProcessingException {
        // Given
        String refreshToken = "test-refresh-token";
        Map<String, Object> expectedTokenMap = new HashMap<>();
        expectedTokenMap.put("access_token", "new-access-token");
        expectedTokenMap.put("refresh_token", "new-refresh-token");
        expectedTokenMap.put("token_type", "Bearer");
        expectedTokenMap.put("expires_in", 3600);

        String responseBody = objectMapper.writeValueAsString(expectedTokenMap);
        ResponseEntity<String> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // When
        Map<String, Object> result = authClientService.refreshAccessToken(refreshToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("access_token")).isEqualTo("new-access-token");
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        ResponseEntity<String> responseEntity = ResponseEntity.ok("Logged out");

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // When
        authClientService.logout(accessToken, refreshToken);

        // Then
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfoSuccess() {
        // Given
        String token = "test-access-token";
        AccountDTO expectedAccount = new AccountDTO();
        expectedAccount.setCckId("TEST001");
        expectedAccount.setName("테스트 사용자");
        expectedAccount.setEmail("test@example.com");

        ResponseEntity<AccountDTO> responseEntity = ResponseEntity.ok(expectedAccount);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AccountDTO.class)))
                .thenReturn(responseEntity);

        // When
        AccountDTO result = authClientService.getMyInfo(token);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCckId()).isEqualTo("TEST001");
        assertThat(result.getName()).isEqualTo("테스트 사용자");
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(AccountDTO.class));
    }

    @Test
    @DisplayName("플레이어 정보 조회 성공")
    void getPlayerInfoSuccess() {
        // Given
        String cckId = "TEST001";
        PlayerInfo expectedPlayer = new PlayerInfo();
        expectedPlayer.setCckId(cckId);
        expectedPlayer.setName("테스트 사용자");
        expectedPlayer.setState("서울");

        ResponseEntity<PlayerInfo> responseEntity = ResponseEntity.ok(expectedPlayer);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(PlayerInfo.class)))
                .thenReturn(responseEntity);

        // When
        PlayerInfo result = authClientService.getPlayerInfo(cckId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCckId()).isEqualTo("TEST001");
        assertThat(result.getName()).isEqualTo("테스트 사용자");
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(PlayerInfo.class));
    }

    @Test
    @DisplayName("플레이어 정보 조회 실패 - 서버 에러")
    void getPlayerInfoFailure() {
        // Given
        String cckId = "TEST001";
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(PlayerInfo.class)))
                .thenThrow(new RuntimeException("Server error"));

        // When & Then
        assertThatThrownBy(() -> authClientService.getPlayerInfo(cckId))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_SERVER_FAILED);
    }

    @Test
    @DisplayName("RFID 정보 조회 성공")
    void getRFIDInfoSuccess() {
        // Given
        String token = "test-access-token";
        String uid = "rfid-uid-123";
        PlayerInfo expectedPlayer = new PlayerInfo();
        expectedPlayer.setCckId("TEST001");
        expectedPlayer.setName("테스트 사용자");

        ResponseEntity<PlayerInfo> responseEntity = ResponseEntity.ok(expectedPlayer);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(PlayerInfo.class)))
                .thenReturn(responseEntity);

        // When
        Optional<PlayerInfo> result = authClientService.getRFIDInfo(token, uid);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCckId()).isEqualTo("TEST001");
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(PlayerInfo.class));
    }

    @Test
    @DisplayName("RFID 정보 조회 - 찾을 수 없음")
    void getRFIDInfoNotFound() {
        // Given
        String token = "test-access-token";
        String uid = "invalid-uid";

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(PlayerInfo.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When
        Optional<PlayerInfo> result = authClientService.getRFIDInfo(token, uid);

        // Then
        assertThat(result).isEmpty();
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(PlayerInfo.class));
    }
}
