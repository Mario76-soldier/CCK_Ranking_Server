# CCK_Ranking_API_SERVER

## Overview
이 문서는 **CCK Ranking API** 사용 방법을 설명합니다.  
해당 API는 JSON 형식으로 요청과 응답을 주고받습니다.

- Base URL: `https://ranking.cubingclub.com`
- Authentication: Cookie (withCredentials)
- Response Format: JSON
- Rate Limit: 100 requests/minute

## 🔑 Authentication
이 API는 OAuth2 SSO 인증 방식을 사용합니다.  
각 서비스별로 인증을 원하신다면 **CCK Ranking**에서 Client 등록을 해야 합니다.  

인증 방식은 **세션 쿠키(Session Cookie)** 기반 인증을 사용합니다.  
브라우저 또는 클라이언트에서 요청 시 `withCredentials: true` 옵션을 반드시 설정해야 합니다.

**Axios 예시**
```javascript
import axios from 'axios';

axios.get('https://ranking.cubingclub.com/api/auth/info', {
    withCredentials: true
})
    .then(res => console.log(res.data))
    .catch(err => console.error(err));
```

## Endpoints Summary
**굵은 글씨는 개인 혹은 관리자 인증 필요**  
각 Path마다 상세정보 링크 설정됨
## Account 
| Method   | Path                                                                                                | Description                |
|----------|-----------------------------------------------------------------------------------------------------|----------------------------|
| GET      | `/oauth2/authorize?response_type=code&client_id={client_id}&scope=openid&redirect_uri={direct_uri}` | 로그인창으로 다이렉트, OAuth2로 코드 반환 |
| **POST** | [`/oauth2/token`](src/main/java/com/cubingclub/ranking/Account/README.md#1-인증-코드로-로그인-마무리)          | **인증 코드 제공으로 로그인 마무리**     |
| GET      | `/signup`                                                                                           | 회원가입                       |
| GET      | `/password-reset`                                                                                   | 비밀번호 재발급                   |
| **GET**  | [`/api/auth/info`](src/main/java/com/cubingclub/ranking/Account/README.md#2-본인-계정-정보-반환)            | **본인 계정 정보 반환**            |
| **POST** | [`/api/auth/edit`](src/main/java/com/cubingclub/ranking/Account/README.md#3-개인-정보-수정)               | **개인 정보 수정**               |

## Mail
| Method | Path                                                                                                 | Description       |
|--------|------------------------------------------------------------------------------------------------------|-------------------|
| POST   | [`/api/mail/send/signup`](src/main/java/com/cubingclub/ranking/Mail/README.md#1-회원가입-인증-메일-전송)       | 회원가입 인증 메일 전송     |
| POST   | [`/api/mail/send/password`](src/main/java/com/cubingclub/ranking/Mail/README.md#2-비밀번호-재설정-인증-메일-전송) | 비밀번호 재발급 인증 메일 전송 |
| POST   | [`/api/mail/password`](src/main/java/com/cubingclub/ranking/Mail/README.md#3-인증번호를-통한-비밀번호-재발급)      | 재발급된 비밀번호 메일 전송   |


## Competition
| Method     | Path                              | Description                 |
|------------|-----------------------------------|-----------------------------|
| GET        | `/api/comps`                      | past, now, future 대회 리스트 반환 |
| GET        | `/api/comp/{compIdx}`             | 특정 대회 정보 조회                 |
| **POST**   | `/api/comp`                       | **대회 생성 및 수정**              |
| **DELETE** | `/api/comp/{compIdx}`             | **대회 삭제(cascade)**          |
| **GET**    | `/api/competitor/{compIdx}`       | **특정 대회의 선수 목록 조회**         |
| **POST**   | `/api/competitor/{compIdx}/{uid}` | **카드 태그로 선수 참여 확인**         |

## Round
| Method     | Path                                | Description                         |
|------------|-------------------------------------|-------------------------------------|
| GET        | `/api/rounds/{compIdx}`             | 특정 대회의 past, now, future 라운드 리스트 반환 |
| GET        | `/api/round/{roundIdx}`             | 특정 라운드 정보 조회                        |
| **POST**   | `/api/round`                        | **라운드 생성 및 수정**                     |
| **DELETE** | `/api/round/{roundIdx}`             | **라운드 삭제(cascade)**                 |
| **PUT**    | `/api/advance/{roundIdx}/{advance}` | **다음 라운드 진출 인원 조정**                 |
| GET        | `/api/group/{roundIdx}`             | 라운드 그룹 리스트 확인                       |

## CubeEvent
| Method     | Path         | Description |
|------------|--------------|-------------|
| GET        | `/api/event` | 종목 리스트 조회   |

## Record
| Method     | Path                              | Description           |
|------------|-----------------------------------|-----------------------|
| GET        | `/api/records/{roundIdx}`         | 특정 라운드의 전체 기록 리스트 반환  |
| **POST**   | `/api/record`                     | **특정 기록 수정**          |
| **DELETE** | `/api/record/{idx}`               | **특정 기록 삭제**          |
| **DELETE** | `/api/record/quit`                | **선택한 기록 리스트 모두 삭제**  |
| **POST**   | `/api/participate`                | **선수(기록) 추가**         |
| **POST**   | `/api/extra`                      | **특정 기록에 대한 엑스트라 추가** |
| **POST**   | `/api/extra/{partIdx}/{extraNum}` | **특정 엑스트라 삭제**        |

## Recorder(IoT전용)
### 권한: RECORDER(독자 인증 방식 사용)
| Method   | Path                                     | Description             |
|----------|------------------------------------------|-------------------------|
| **GET**  | `/api/recorder/comp`                     | **현재 진행중인 대회 리스트 반환**   |
| **GET**  | `/api/recorder/info/{uid}`               | **카드 태그 후 해당 선수 정보 반환** |
| **GET**  | `/api/recorder/record/{email}/{compIdx}` | **특정 선수의 진행상황 반환**      |
| **POST** | `/api/recorder/record`                   | **선수가 시도한 기록 입력**       |

## Scrambler
### 권한: SCRAMBLER, ROOT
| Method   | Path                            | Description            |
|----------|---------------------------------|------------------------|
| **GET**  | `/api/scramble/{compIdx}/{uid}` | **특정 선수의 스크램블 정보 반환**  |
| **GET**  | `/api/scrambles/{roundIdx}`     | **특정 라운드 스크램블 리스트 반환** |
| **POST** | `/api/scrambler`                | **스크램블러 승인 입력**        |

## 기술 스택

- **Spring Boot**
- **OAuth2**(인증 서버)
- **Thymeleaf**(로그인, 회원가입, 비밀번호 재발급, 메일 html)
- **JPA** + **Mysql** (ORM과 데이터베이스)

## 설치 및 실행

### 1. 저장소 클론

```bash
git clone https://github.com/cubingclubkorea/CompRanking_B.git
cd CompRanking_B
```

### 2. 환경변수 설정

```bash
cd src/main/resources
cp application_example.yml application.yml
#YOUR_DB_URL=localhost:3306
#YOUR_DB=cck
#YOUR_DB_USERNAME=root
#YOUR_DB_PASSWORD=password
#YOUR_MAIL_APP_EMAIL=example@example.com
#YOUR_MAIL_APP_PASSWORD=password
#YOUR_CLIENT_SECRET=password
#YOUR_REDIRECT_URI=http://localhost:3000/confirm
cd ../../..

```

### 3. 빌드 및 실행
```bash
./gradlew clean build
java -jar build/libs/com.cubingclub.ranking-{version}.jar
```

### Docker-compose를 사용한 동시 실행(실행환경 구축 곤란 시 사용)
```bash
docker network create my_network #첫 실행 시 네트워크 생성 필요, 이후에는 할 필요 없음
docker-compose up -d #백그라운드로 실행
```
## 프로젝트 구조

```
CompRanking_B/
├── src/
│ ├── main/
│ │ ├── java/com/cubingclub/ranking/
│ │ │ ├── Account/ # 계정 관련 기능
│ │ │ ├── Competition/ # 대회 관리 기능
│ │ │ ├── Config/ # 설정 파일 및 설정 클래스
│ │ │ ├── CubeEvent/ # 큐브 이벤트 처리
│ │ │ ├── ExceptionController/ # 예외 처리 컨트롤러
│ │ │ ├── Mail/ # 이메일 전송 기능
│ │ │ ├── OAuthClient/ # OAuth 클라이언트 구현
│ │ │ ├── PersonalRecord/ # 개인 기록 관리
│ │ │ ├── Record/ # 기록 관련 공통 로직
│ │ │ ├── RFID/ # RFID 리더기 처리
│ │ │ ├── Round/ # 라운드 관리
│ │ │ ├── RsaKeys/ # RSA 키 관리
│ │ │ ├── Schedule/ # 스케줄 관리
│ │ │ ├── Scrambler/ # 스크램블 생성
│ │ │ └── RankingApplication # 스프링 부트 메인 애플리케이션
│ │ └── resources/ # 설정 파일 및 리소스 (application.yml 등)
│ └── test/
│ └── java/com/cubingclub/ranking/
│ └── RankingApplicationTests # 테스트 코드
├── build.gradle
├── gradlew
├── gradlew.bat
├── README.md
└── settings.gradle
```

## 개발 가이드

## 기여하기

이 프로젝트에 기여하고 싶으시다면:

1. 이 저장소를 Fork 하세요
2. Feature 브랜치를 생성하세요 (`git checkout -b feature/amazing-feature`)
3. 변경사항을 커밋하세요 (`git commit -m 'Add some amazing feature'`)
4. 브랜치에 Push 하세요 (`git push origin feature/amazing-feature`)
5. Pull Request를 열어주세요

### 코드 기여 가이드라인

- 커밋 메시지는 [Conventional Commits](https://www.conventionalcommits.org/) 형식을 따라주세요
- 새로운 기능은 테스트 코드와 함께 제출해주세요

## 지원 및 문의

- **이슈 신고**: [GitHub Issues](https://github.com/cubingclubkorea/CompRanking_B/issues)
- **큐빙클럽코리아**: [공식 웹사이트](https://cubingclub.com)

## 개발팀

이 프로젝트는 **큐빙클럽코리아(CCK) 개발팀**에서 관리하고 있습니다.

---

**큐빙클럽코리아(CCK)** 는 한국의 루빅스 큐브 커뮤니티 발전을 위해 노력하고 있습니다.