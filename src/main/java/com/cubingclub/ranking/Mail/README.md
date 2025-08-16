# Mail API 상세 문서

## 개요
회원가입, 비밀번호 재발급 등 메일 전송과 관련된 API를 제공합니다.  
이 문서에서는 **메일 발송과 인증 처리** 예시를 다룹니다.

---

## 1. 회원가입 인증 메일 전송
**POST** `/api/mail/send/signup`  
입력한 이메일 주소로 회원가입 인증 메일을 발송합니다.

### 요청 예시
```javascript
import axios from 'axios';

axios.post(
  '/api/mail/send/signup',
  { email: 'user@example.com' },
);
```

### 응답 예시
성공 시:
```
HTTP/1.1 200 OK
```

이메일 중복 시:
```
HTTP/1.1 409 Conflict
Content-Type: text/plain

Email duplicate
```

### 요청 객체 (`EmailRequestDTO`)
| 필드    | 타입     | 설명     |
|-------|--------|--------|
| email | string | 이메일 주소 |

---

## 2. 비밀번호 재설정 인증 메일 전송
**POST** `/api/mail/send/password`  
가입된 이메일인지 확인 후, 비밀번호 재설정 인증 메일을 발송합니다.

### 요청 예시
```javascript
import axios from 'axios';

axios.post(
  '/api/mail/send/password',
  { email: 'user@example.com' },
);
```

### 응답 예시
성공 시:
```
HTTP/1.1 200 OK
```

이메일 없음:
```
HTTP/1.1 204 No Content
Content-Type: text/plain

Email doesn't exist
```

---

## 3. 인증번호를 통한 비밀번호 재발급
**POST** `/api/mail/password`  
이메일과 인증번호를 사용해 비밀번호를 재발급받습니다.

### 요청 예시
```javascript
import axios from 'axios';

axios.post(
  '/api/mail/password',
  {
    email: 'user@example.com',
    authNum: '123456'
  },
);
```

### 응답 예시
성공 시:
```
HTTP/1.1 200 OK
```

인증번호 불일치:
```
HTTP/1.1 400 Bad Request
Content-Type: text/plain

Mail authenticate incorrect
```

비밀번호 생성 실패:
```
HTTP/1.1 500 Internal Server Error
Content-Type: text/plain

Failed to create password
```

### 요청 객체 (`PwReissueRequestDTO`)
| 필드      | 타입     | 설명          |
|---------|--------|-------------|
| email   | string | 이메일 주소      |
| authNum | string | 메일 인증번호(선택) |
