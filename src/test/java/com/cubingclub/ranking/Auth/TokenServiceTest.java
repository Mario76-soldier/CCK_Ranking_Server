package com.cubingclub.ranking.Auth;

import com.cubingclub.ranking.Auth.DTO.TokenResponse;
import com.cubingclub.ranking.Auth.Service.AuthClientService;
import com.cubingclub.ranking.Auth.Service.TokenService;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Token Service 테스트")
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private AuthClientService authClientService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private Map<String, Object> testTokenMap;

    @BeforeEach
    void setUp() {
        testTokenMap = new HashMap<>();
        testTokenMap.put("access_token", "test-access-token");
        testTokenMap.put("refresh_token", "test-refresh-token");
        testTokenMap.put("token_type", "Bearer");
        testTokenMap.put("expires_in", 3600);
        testTokenMap.put("scope", "openid profile email");
    }

    @Test
    @DisplayName("코드로 토큰 교환 성공")
    void exchangeTokenWithCookieSuccess() {
        // Given
        String code = "authorization-code";
        when(authClientService.exchangeCodeForToken(code)).thenReturn(testTokenMap);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // When
        TokenResponse result = tokenService.exchangeTokenWithCookie(code, response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("test-access-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isEqualTo(3600);

        verify(authClientService, times(1)).exchangeCodeForToken(code);
        verify(response, times(1)).addCookie(cookieCaptor.capture());

        Cookie savedCookie = cookieCaptor.getValue();
        assertThat(savedCookie.getName()).isEqualTo("refresh_token");
        assertThat(savedCookie.getValue()).isEqualTo("test-refresh-token");
        assertThat(savedCookie.isHttpOnly()).isTrue();
        assertThat(savedCookie.getSecure()).isTrue();
    }

    @Test
    @DisplayName("리프레시 토큰으로 액세스 토큰 갱신 성공")
    void refreshAccessTokenWithCookieSuccess() {
        // Given
        String refreshToken = "test-refresh-token";
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        when(authClientService.refreshAccessToken(refreshToken)).thenReturn(testTokenMap);

        // When
        TokenResponse result = tokenService.refreshAccessTokenWithCookie(request, response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("test-access-token");
        verify(authClientService, times(1)).refreshAccessToken(refreshToken);
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    @DisplayName("리프레시 토큰 없이 액세스 토큰 갱신 시도 - 실패")
    void refreshAccessTokenWithoutRefreshToken() {
        // Given
        when(request.getCookies()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> tokenService.refreshAccessTokenWithCookie(request, response))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_REFRESH_TOKEN_NOT_FOUND);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logoutSuccess() {
        // Given
        String accessToken = "test-access-token";
        String refreshToken = "test-refresh-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken);
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        doNothing().when(authClientService).logout(accessToken, refreshToken);

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // When
        tokenService.logout(request, response);

        // Then
        verify(authClientService, times(1)).logout(accessToken, refreshToken);
        verify(response, times(1)).addCookie(cookieCaptor.capture());

        Cookie deletedCookie = cookieCaptor.getValue();
        assertThat(deletedCookie.getName()).isEqualTo("refresh_token");
        assertThat(deletedCookie.getMaxAge()).isEqualTo(0);
    }

    @Test
    @DisplayName("로그아웃 - 액세스 토큰 없음")
    void logoutWithoutAccessToken() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> tokenService.logout(request, response))
                .isInstanceOf(CustomizedException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_ACCESS_TOKEN_NOT_FOUND);
    }

    @Test
    @DisplayName("로그아웃 - 서버 에러 발생해도 쿠키는 삭제")
    void logoutWithServerErrorStillDeletesCookie() {
        // Given
        String accessToken = "test-access-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + accessToken);
        when(request.getCookies()).thenReturn(null);
        doThrow(new RuntimeException("Server error")).when(authClientService).logout(anyString(), anyString());

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        // When
        tokenService.logout(request, response);

        // Then
        verify(response, times(1)).addCookie(cookieCaptor.capture());
        Cookie deletedCookie = cookieCaptor.getValue();
        assertThat(deletedCookie.getMaxAge()).isEqualTo(0);
    }

    @Test
    @DisplayName("리프레시 토큰 교환 - 새 리프레시 토큰이 없는 경우")
    void refreshAccessTokenWithoutNewRefreshToken() {
        // Given
        String refreshToken = "test-refresh-token";
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        Map<String, Object> tokenMapWithoutRefresh = new HashMap<>();
        tokenMapWithoutRefresh.put("access_token", "new-access-token");
        tokenMapWithoutRefresh.put("token_type", "Bearer");
        tokenMapWithoutRefresh.put("expires_in", 3600);
        tokenMapWithoutRefresh.put("scope", "openid profile email");
        
        when(authClientService.refreshAccessToken(refreshToken)).thenReturn(tokenMapWithoutRefresh);

        // When
        TokenResponse result = tokenService.refreshAccessTokenWithCookie(request, response);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("new-access-token");
        verify(authClientService, times(1)).refreshAccessToken(refreshToken);
    }
}
