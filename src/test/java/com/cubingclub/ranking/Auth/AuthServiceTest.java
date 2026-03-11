package com.cubingclub.ranking.Auth;

import com.cubingclub.ranking.Auth.DTO.AccountDTO;
import com.cubingclub.ranking.Auth.DTO.PlayerInfo;
import com.cubingclub.ranking.Auth.DTO.TokenResponse;
import com.cubingclub.ranking.Auth.Service.AuthClientService;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Auth.Service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service 테스트")
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthClientService authClientService;

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private TokenResponse testTokenResponse;
    private AccountDTO testAccountDTO;
    private PlayerInfo testPlayerInfo;

    @BeforeEach
    void setUp() {
        testTokenResponse = new TokenResponse();
        testTokenResponse.setAccessToken("test-access-token");
        testTokenResponse.setTokenType("Bearer");
        testTokenResponse.setExpiresIn(3600);
        testTokenResponse.setScope("openid profile email");

        testAccountDTO = new AccountDTO();
        testAccountDTO.setCckId("TEST001");
        testAccountDTO.setName("테스트 사용자");
        testAccountDTO.setEmail("test@example.com");

        testPlayerInfo = new PlayerInfo();
        testPlayerInfo.setCckId("TEST001");
        testPlayerInfo.setName("테스트 사용자");
        testPlayerInfo.setState("서울");
    }

    @Test
    @DisplayName("토큰 교환 성공")
    void exchangeTokenWithCookieSuccess() {
        // Given
        String code = "authorization-code";
        when(tokenService.exchangeTokenWithCookie(code, response)).thenReturn(testTokenResponse);

        // When
        TokenResponse result = authService.exchangeTokenWithCookie(code, response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("test-access-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        verify(tokenService, times(1)).exchangeTokenWithCookie(code, response);
    }

    @Test
    @DisplayName("액세스 토큰 갱신 성공")
    void refreshAccessTokenWithCookieSuccess() {
        // Given
        when(tokenService.refreshAccessTokenWithCookie(request, response)).thenReturn(testTokenResponse);

        // When
        TokenResponse result = authService.refreshAccessTokenWithCookie(request, response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("test-access-token");
        verify(tokenService, times(1)).refreshAccessTokenWithCookie(request, response);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() {
        // Given
        doNothing().when(tokenService).logout(request, response);

        // When
        authService.logout(request, response);

        // Then
        verify(tokenService, times(1)).logout(request, response);
    }

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfoSuccess() {
        // Given
        String token = "test-access-token";
        when(authClientService.getMyInfo(token)).thenReturn(testAccountDTO);

        // When
        AccountDTO result = authService.getMyInfo(token);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCckId()).isEqualTo("TEST001");
        assertThat(result.getName()).isEqualTo("테스트 사용자");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(authClientService, times(1)).getMyInfo(token);
    }

    @Test
    @DisplayName("플레이어 정보 조회 성공")
    void getPlayerInfoSuccess() {
        // Given
        String cckId = "TEST001";
        when(authClientService.getPlayerInfo(cckId)).thenReturn(testPlayerInfo);

        // When
        PlayerInfo result = authService.getPlayerInfo(cckId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCckId()).isEqualTo("TEST001");
        assertThat(result.getName()).isEqualTo("테스트 사용자");
        assertThat(result.getState()).isEqualTo("서울");
        verify(authClientService, times(1)).getPlayerInfo(cckId);
    }

    @Test
    @DisplayName("RFID 정보 조회 성공")
    void getRFIDInfoSuccess() {
        // Given
        String token = "test-access-token";
        String uid = "rfid-uid-123";
        when(authClientService.getRFIDInfo(token, uid)).thenReturn(Optional.of(testPlayerInfo));

        // When
        Optional<PlayerInfo> result = authService.getRFIDInfo(token, uid);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getCckId()).isEqualTo("TEST001");
        assertThat(result.get().getName()).isEqualTo("테스트 사용자");
        verify(authClientService, times(1)).getRFIDInfo(token, uid);
    }

    @Test
    @DisplayName("RFID 정보 조회 - 정보 없음")
    void getRFIDInfoNotFound() {
        // Given
        String token = "test-access-token";
        String uid = "invalid-uid";
        when(authClientService.getRFIDInfo(token, uid)).thenReturn(Optional.empty());

        // When
        Optional<PlayerInfo> result = authService.getRFIDInfo(token, uid);

        // Then
        assertThat(result).isEmpty();
        verify(authClientService, times(1)).getRFIDInfo(token, uid);
    }
}
