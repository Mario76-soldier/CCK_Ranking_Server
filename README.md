# CCK Ranking Backend

> 큐빙 대회 순위 관리 및 기록 시스템

## 📋 프로젝트 소개

CCK Ranking은 큐브 대회의 참가자 관리, 라운드 스케줄링, 스크램블 생성, 기록 관리 등을 종합적으로 처리하는 백엔드 시스템입니다.

### 주요 기능

- 🏆 **대회 관리**: 대회 생성, 수정, 조회 및 일정 관리
- 🎲 **라운드 관리**: 라운드별 그룹 설정, 컷오프, 어드밴스 인원 관리
- 🧩 **큐브 이벤트**: 다양한 큐브 종목 관리 (3x3x3, 2x2x2, Pyraminx 등)
- 🔀 **스크램블 생성**: WCA 공식 TNoodle 라이브러리를 통한 스크램블 생성
- 📊 **기록 관리**: 참가자별 기록 입력 및 순위 집계
- 🔐 **인증/인가**: OAuth2 기반 사용자 인증 및 권한 관리

## 🛠 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Spring Security**
- **MySQL 8.x**

### Testing
- **JUnit 5**
- **Mockito**
- **AssertJ**

### Build & Deploy
- **Gradle**

## 📁 프로젝트 구조

```
CompRanking_B/
├── src/
│   ├── main/
│   │   ├── java/com/cubingclub/ranking/
│   │   │   ├── Auth/              # 인증/인가
│   │   │   ├── Competition/       # 대회 관리
│   │   │   ├── Round/             # 라운드 관리
│   │   │   ├── CubeEvent/         # 큐브 이벤트
│   │   │   ├── Scrambler/         # 스크램블 생성
│   │   │   ├── Record/            # 기록 관리
│   │   │   ├── PersonalRecord/    # 개인 최고 기록
│   │   │   ├── Config/            # 설정
│   │   │   └── Exception/         # 예외 처리
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── static/
│   └── test/
│       └── java/com/cubingclub/ranking/
│           ├── Competition/
│           ├── Round/
│           ├── CubeEvent/
│           └── Scrambler/
├── docs/                          # 프로젝트 문서
│   ├── API.md                     # API 명세서
│   ├── CONVENTION.md              # 코딩 컨벤션
│   ├── ENVIRONMENT.md             # 환경 설정 가이드
│   └── TESTING.md                 # 테스트 가이드
├── build.gradle
└── README.md
```

## 🚀 시작하기

### 사전 요구사항

- Java 17 이상
- MySQL 8.0 이상
- Gradle 7.x 이상
- TNoodle 서버 (스크램블 생성용)
### 설치 및 실행

1. **저장소 클론**
```bash
git clone https://github.com/cubingclubkorea/CompRanking_B.git
cd CompRanking_B
```

2. **환경 설정**

`src/main/resources/application_example.yaml`을 복사하여 `application.yaml` 생성 후 환경변수 설정

```bash
cd src/main/resources
cp application_example.yaml application.yaml
# application.yaml 파일을 편집하여 DB, 메일 등 설정
```

상세한 환경 설정은 [환경 설정 가이드](docs/ENVIRONMENT.md)를 참조하세요.

3. **데이터베이스 생성**
```sql
CREATE DATABASE ranking_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

4. **TNoodle 서버 실행**
```bash
java -jar TNoodle-WCA-*.jar
# 기본 포트: 2014
```

5. **애플리케이션 빌드**
```bash
./gradlew build
```

6. **애플리케이션 실행**
```bash
./gradlew bootRun
```

애플리케이션은 `http://localhost:8080`에서 실행됩니다.

## 📖 문서

- [API 명세서](docs/API.md) - REST API 엔드포인트 및 사용법
- [코딩 컨벤션](docs/CONVENTION.md) - 프로젝트 코딩 규칙
- [환경 설정 가이드](docs/ENVIRONMENT.md) - 환경변수 및 설정 방법
- [테스트 가이드](docs/TESTING.md) - 테스트 코드 작성 방법

## 🧪 테스트

### 전체 테스트 실행
```bash
./gradlew test
```

### 특정 도메인 테스트
```bash
./gradlew test --tests com.cubingclub.ranking.Competition.*
./gradlew test --tests com.cubingclub.ranking.Round.*
```

### 테스트 커버리지 리포트
```bash
./gradlew test jacocoTestReport
```

리포트는 `build/reports/jacoco/test/html/index.html`에서 확인할 수 있습니다.

## 📊 주요 API 엔드포인트

### Competition API
```
GET    /comp/past          # 지난 대회 목록
GET    /comp/now           # 진행 중인 대회 목록
GET    /comp/future        # 예정된 대회 목록
GET    /comp/{compIdx}     # 단일 대회 조회
POST   /comp               # 대회 생성
PUT    /comp               # 대회 수정
DELETE /comp/comp/{compIdx} # 대회 삭제
```

### Round API
```
GET    /round/past/{compIdx}   # 지난 라운드 목록
GET    /round/now/{compIdx}    # 진행 중인 라운드 목록
GET    /round/future/{compIdx} # 예정된 라운드 목록
GET    /round/{roundIdx}       # 단일 라운드 조회
POST   /round                  # 라운드 생성
PUT    /round                  # 라운드 수정
DELETE /round/{roundIdx}       # 라운드 삭제
```

### CubeEvent API
```
GET    /event               # 이벤트 목록 조회
POST   /event               # 이벤트 추가
```

### Scrambler API
```
GET    /scramble/check/{uid}     # 사용자 확인
PATCH  /scramble/scrambler       # 스크램블 조회
```

더 자세한 API 문서는 [API.md](docs/API.md)를 참조하세요.

## 🏗 아키텍처

### 레이어 구조

```
Controller Layer
    ↓ (HTTP Request/Response)
Service Layer
    ↓ (Business Logic)
Repository Layer
    ↓ (Data Access)
Database
```

### 도메인 모델

- **Competition**: 대회 정보 및 일정
- **Round**: 종목별 라운드 정보
- **CubeEvent**: 큐브 종목 정보
- **Scrambler**: 스크램블 생성 및 관리
- **Record**: 참가자별 기록
- **PersonalRecord**: 개인 최고 기록

## 🔒 보안

- OAuth2 기반 인증
- JWT 토큰을 통한 세션 관리
- RFID UID를 통한 참가자 확인
- 민감한 정보는 환경변수로 관리

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 코딩 규칙

프로젝트에 기여하기 전에 [코딩 컨벤션](docs/CONVENTION.md)을 확인해주세요.

## 📝 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다.

## 👥 개발팀

이 프로젝트는 **큐빙클럽코리아(CCK) 개발팀**에서 관리하고 있습니다.

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해주세요.

## 🔗 관련 프로젝트

- [TNoodle](https://github.com/thewca/tnoodle) - WCA 공식 스크램블 생성기
- [큐빙클럽코리아 공식 웹사이트](https://cubingclub.com)

## 📈 버전 히스토리

### v1.0.0 (2026-01-13)
- 초기 릴리즈
- Competition, Round, CubeEvent, Scrambler 도메인 구현
- 테스트 코드 작성 완료
- API 문서화 완료

---

Made with ❤️ by Cubing Club Korea