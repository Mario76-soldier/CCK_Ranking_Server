# 환경변수 설정 가이드

## 개요

이 문서는 CompRanking 프로젝트의 환경변수 설정 방법을 설명합니다.

## 설정 파일 구조

```
src/main/resources/
├── application.yaml         # 실제 사용할 설정 파일 (git ignore)
└── application_example.yaml # 설정 예시 파일 (템플릿)
```

## 설정 방법

### 1. 설정 파일 생성

`application_example.yaml` 파일을 복사하여 `application.yaml` 파일을 생성합니다.

```bash
cd src/main/resources
cp application_example.yaml application.yaml
```

### 2. 데이터베이스 설정

#### MySQL 연결 정보
```yaml
spring:
  datasource:
    url: jdbc:mysql://{DB_HOST}:{DB_PORT}/{DB_NAME}?autoReconnect=true
    username: {DB_USERNAME}
    password: {DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

**설정값 설명:**
- `{DB_HOST}`: 데이터베이스 호스트 주소 (예: localhost, 192.168.1.100)
- `{DB_PORT}`: MySQL 포트 번호 (기본값: 3306)
- `{DB_NAME}`: 사용할 데이터베이스 이름
- `{DB_USERNAME}`: 데이터베이스 사용자명
- `{DB_PASSWORD}`: 데이터베이스 비밀번호

**예시:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ranking_db?autoReconnect=true
    username: root
    password: mypassword
```

#### JPA 설정
```yaml
spring:
  jpa:
    database: mysql
    database-platform: org.hibernate.dialect.MySQL8Dialect
    generate-ddl: true
    show-sql: true  # 개발 환경에서만 true, 운영 환경에서는 false
    hibernate:
      naming:
        implicit-strategy: org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
```

**주의사항:**
- `generate-ddl: true`는 개발 환경에서만 사용
- 운영 환경에서는 `false`로 설정하고 마이그레이션 도구 사용 권장

### 3. 이메일 설정

```yaml
spring:
  mail:
    email: {YOUR_MAIL_APP_EMAIL}
    password: {YOUR_MAIL_APP_PASSWORD}
```

**설정값 설명:**
- `{YOUR_MAIL_APP_EMAIL}`: 발신용 이메일 주소
- `{YOUR_MAIL_APP_PASSWORD}`: 앱 비밀번호 (Gmail의 경우 2단계 인증 후 앱 비밀번호 생성)

**Gmail 앱 비밀번호 생성 방법:**
1. Google 계정 설정 → 보안
2. 2단계 인증 활성화
3. 앱 비밀번호 생성
4. 생성된 16자리 비밀번호 사용

**예시:**
```yaml
spring:
  mail:
    email: noreply@example.com
    password: abcd efgh ijkl mnop
```

### 4. TNoodle 설정

TNoodle은 공식 WCA 스크램블 생성 라이브러리입니다.

```yaml
spring:
  tnoodle:
    url: http://localhost:2014
```

**TNoodle 서버 실행:**
1. TNoodle JAR 다운로드: https://www.worldcubeassociation.org/regulations/scrambles/
2. 실행: `java -jar TNoodle-WCA-*.jar`
3. 기본 포트: 2014

**다른 포트 사용 시:**
```yaml
spring:
  tnoodle:
    url: http://localhost:3000
```

### 5. OAuth2 설정

```yaml
spring:
  oauth:
    client:
      uri: http://localhost:8080/oauth2/token
      id: cck_ranking
      secret: {YOUR_CLIENT_SECRET}
      redirect-uri: {YOUR_REDIRECT_URI}
```

**설정값 설명:**
- `uri`: OAuth2 토큰 엔드포인트 URL
- `id`: OAuth2 클라이언트 ID
- `secret`: OAuth2 클라이언트 시크릿
- `redirect-uri`: OAuth2 리다이렉트 URI

**예시:**
```yaml
spring:
  oauth:
    client:
      uri: http://localhost:8080/oauth2/token
      id: cck_ranking
      secret: my-super-secret-key-12345
      redirect-uri: http://localhost:3000/callback
```

### 6. 이미지 저장 경로 설정

```yaml
spring:
  img:
    path: /path/to/image/directory
```

**설정값 설명:**
- 큐브 이벤트 이미지 파일(SVG)이 저장될 디렉토리 경로
- 절대 경로 사용 권장

**예시:**
```yaml
spring:
  img:
    path: /var/www/ranking/images
```

**개발 환경 예시:**
```yaml
spring:
  img:
    path: /Users/username/projects/ranking/images  # macOS/Linux
    # 또는
    path: C:/Users/username/projects/ranking/images  # Windows
```

### 7. 로깅 설정

```yaml
logging:
  level:
    org:
      springframework:
        mail: INFO
        mail.javamail: INFO
        #security: DEBUG  # 보안 관련 디버깅 필요 시 주석 해제
    com:
      sun:
        mail: INFO
    javax:
      mail: INFO
```

**로그 레벨:**
- `TRACE`: 가장 상세한 로그
- `DEBUG`: 디버깅용 상세 정보
- `INFO`: 일반 정보 (기본값 권장)
- `WARN`: 경고
- `ERROR`: 오류

**개발 환경 로깅 예시:**
```yaml
logging:
  level:
    com.cubingclub.ranking: DEBUG  # 프로젝트 패키지 디버그 모드
    org.hibernate.SQL: DEBUG       # SQL 쿼리 출력
    org.hibernate.type: TRACE      # SQL 파라미터 출력
```

## 환경별 설정

### 개발 환경 (Local)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ranking_dev
    username: dev_user
    password: dev_password
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
```

### 테스트 환경

```yaml
spring:
  datasource:
    url: jdbc:mysql://test-server:3306/ranking_test
    username: test_user
    password: test_password
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
```

### 운영 환경 (Production)

```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-server:3306/ranking_prod
    username: prod_user
    password: ${DB_PASSWORD}  # 환경변수 사용
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: none  # 운영에서는 절대 DDL 자동 생성 금지
logging:
  level:
    root: WARN
    com.cubingclub.ranking: INFO
```

## 환경변수 사용 (권장)

민감한 정보는 환경변수로 관리하는 것을 권장합니다.

```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  mail:
    email: ${MAIL_EMAIL}
    password: ${MAIL_PASSWORD}
  oauth:
    client:
      secret: ${OAUTH_CLIENT_SECRET}
```

**환경변수 설정 방법:**

### macOS/Linux
```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=ranking_db
export DB_USERNAME=root
export DB_PASSWORD=mypassword
export MAIL_EMAIL=noreply@example.com
export MAIL_PASSWORD=mailpassword
export OAUTH_CLIENT_SECRET=oauth-secret
```

### Windows (CMD)
```cmd
set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=ranking_db
set DB_USERNAME=root
set DB_PASSWORD=mypassword
```

### Windows (PowerShell)
```powershell
$env:DB_HOST="localhost"
$env:DB_PORT="3306"
$env:DB_NAME="ranking_db"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="mypassword"
```

## 보안 주의사항

⚠️ **중요:**
- `application.yaml` 파일은 **절대 Git에 커밋하지 마세요**
- `.gitignore`에 `application.yaml` 추가 확인
- 민감한 정보는 환경변수 또는 암호화된 설정 사용
- 운영 환경 설정은 안전한 곳에 별도 관리

## 설정 검증

애플리케이션 시작 시 다음 사항을 확인하세요:

1. ✅ 데이터베이스 연결 성공
2. ✅ TNoodle 서버 연결 가능
3. ✅ 이메일 발송 테스트 성공
4. ✅ 이미지 디렉토리 쓰기 권한 확인

## 문제 해결

### 데이터베이스 연결 실패
```
Error: Communications link failure
```
- DB 서버 실행 여부 확인
- 호스트/포트 정보 확인
- 방화벽 설정 확인

### TNoodle 연결 실패
```
Error: Connection refused to http://localhost:2014
```
- TNoodle 서버 실행 확인
- 포트 번호 일치 확인

### 이메일 발송 실패
```
Error: Authentication failed
```
- 이메일/비밀번호 확인
- Gmail의 경우 앱 비밀번호 사용 확인
- 2단계 인증 활성화 확인
