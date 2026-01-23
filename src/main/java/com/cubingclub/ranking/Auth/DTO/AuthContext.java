package com.cubingclub.ranking.Auth.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JWT 토큰과 UID를 담는 컨텍스트 클래스
 */
public record AuthContext(String token, String uid) {
}
