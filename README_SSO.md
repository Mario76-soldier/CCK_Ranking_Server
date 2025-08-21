# Spring SSO 인증 가이드

## 개요
이 문서는 Spring 기반으로 구축된 **SSO(Single Sign-On) 서버**를 사용하는 방법을 설명합니다.  
SSO 서버는 OAuth2.0 / OpenID Connect 표준을 기반으로 하며, 하나의 계정으로 여러 서비스에 안전하게 로그인할 수 있도록 지원합니다.

---

## 인증 플로우

1. 사용자가 클라이언트 서비스(웹/앱)에 접근합니다.
2. 클라이언트는 SSO 서버의 **인증 엔드포인트**로 리다이렉트합니다.
3. 사용자가 로그인하면, SSO 서버는 **인가 코드(Authorization Code)** 를 클라이언트에 전달합니다.
4. 클라이언트는 인가 코드를 이용해 SSO 서버의 **토큰 엔드포인트**에서 Access Token, Refresh Token, ID Token을 발급받습니다.
5. 클라이언트는 Access Token을 사용해 보호된 API에 접근할 수 있습니다.

---

## 엔드포인트

### 1. 인증 요청
**GET** `/oauth2/authorize`

#### 요청 파라미터
| 이름            | 설명                       |
|---------------|--------------------------|
| response_type | `code` (인가 코드 방식)        |
| client_id     | 발급받은 클라이언트 ID            |
| scope         | 요청 권한 범위 (예: `openid 등`) |
| redirect_uri  | 인증 완료 후 리다이렉트될 URI       |

#### 예시
```
GET https://ranking.cubingclub.com/oauth2/authorize?response_type=code&client_id=cck_ranking&scope=openid&redirect_uri=https://ranking.cubingclub.com/confirm
```

### 응답

-   성공: `302 Found`->`/login`-로그인 창으로 리다이렉트
-   실패: `400 Bad request`

---

### 2. 토큰 발급
**POST** `/oauth2/token`

#### 요청1 (Authorization: Basic Auth)
| 이름            | 설명                   |
|---------------|----------------------|
| client_id     | 클라이언트 ID             |
| client_secret | 클라이언트 Secret         |

#### 요청2 (x-www-form-urlencoded)
| 이름           | 설명                       |
|--------------|--------------------------|
| grant_type   | `authorization_code`     |
| code         | 인가 코드                    |
| scope        | 요청 권한 범위 (예: `openid 등`) |
| redirect_uri | 인증 요청 시와 동일해야 함          |

#### 요청 예시
```http request
POST /oauth/token HTTP/1.1
Host: sso.example.com
Authorization: Basic Y2xpZW50X2lkOmNsaWVudF9zZWNyZXQ= #client_id:client_secret 을 Base64 인코딩한 값
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&code=abc123&redirect_uri=https://client.example.com/callback&scope=openid%20profile
```

#### 응답 예시
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIs...",
  "refresh_token": "def50200ab...",
  "id_token": "...",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

---

### 3. 사용자 정보 조회
**GET** `/api/auth/info`

#### 요청 헤더
```
Authorization: Bearer {access_token}
```

#### 응답 예시
```json
{
  "email": "example@example.com",
  "name": "홍길동",
  "enName": "Gildong Hong",
  "birthDate": "2001-01-01",
  "state": "Gyeonggi-South",
  "gender": "Male",
  "phoneNum": null
}
```

---

### 4. JWT 서명키 공개
**GET** `/oauth2/jwks`

#### 응답 예시
```json
{
  "keys": [
    {
      "kty": "RSA",
      "e": "AQAB",
      "kid": "c4fcbb30-fb02-4c9c-93a7-bd2ba9e96a29",
      "n": "06M0ly8iLkv5h3pELxo1ZxdQC9bEASHynDAdPnfmko_TS1fEE_BBIv7HjJ00Z141LrKf3WkLbgvTTqaSMcUQo3HGigMMCfteALehXUXZliyAo1E4zeiXUAESwMg2ZkJQuPvVUr1CGOk2JZFXUSKws5wSZ_Wp0UgqsRlkL19AeEDdZ1iY1sUFVO81SaG7540gbrTB5kiRD0RMbCaYaK6rGvaXbom4HG3Jo5KY9-KNy6O2SA2oZKoEFKNCWgl3HZUUSDFZXqgqoscmN8v_keXVrvdw2dvIdhE6sKhK6SmT8mrsz7TeLX4N5m47AmR6_R10mL3eIvXsw7UbsETJ-oPxmQ"
    }
  ]
}
```

---

## 인증 가이드라인
1. 로그인 버튼에`/oauth2/authorize`로 요청을 연결하세요.
    - client_id와 redirect uri는 반드시 등록한 것과 일치해야 정상적으로 로그인 창이 뜹니다.
2. 로그인 이후 리다이렉트에서 `code`를 추출하세요.
    - redirect uri는 해당 코드를 추출한 이후, 토큰을 발급하기 위해 사용하는 uri입니다.
3. 해당 코드를 가지고 `/oauth2/token`으로 토큰 요청을 보내세요.
    - 토큰은 access token, refresh token, id token이 있습니다.
    - 반드시 백엔드 서버를 통해서 client id, client secret을 다루세요. 클라이언트에서 진행 시 보안 상의 취약점이 발생합니다.
4. 해당 토큰을 가지고 `/api/auth/info`로 요청을 보내세요.
    - 사용자 정보가 반환됩니다.
5. 마이크로서비스에서 토큰 검증할 시, `/oauth2/jwks`에서 받은 서명키로 검증하세요.
    - 해당 작업으로 사용자 검증이 가능합니다.
---

## [클라이언트 등록 방법](src/main/java/com/cubingclub/ranking/OAuthClient/README.md)

---

## 보안 권장사항
- Access Token은 만료 시간을 짧게(예: 1시간) 설정합니다.
- Refresh Token을 활용하여 장기 인증을 지원합니다.
- 쿠키 사용 시 `HttpOnly + Secure + SameSite` 옵션을 적용합니다.
- HTTPS만 허용해야 합니다.
