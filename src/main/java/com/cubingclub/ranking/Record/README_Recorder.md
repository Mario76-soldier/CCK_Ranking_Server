# Recorder API 상세 문서

## 개요
대회 현장에서 기록을 입력하는 **CCK Recorder 전용 API**입니다.  
참가자 계정 조회, 시도 기록 입력, 실시간 성적 계산 기능을 제공합니다.

## 1. 당일 대회 조회

**GET** `/api/recorder/comp`\
당일 대회 리스트를 조회합니다.  
**CCK Recorder**에서 해당 리스트에서 대회를 선택합니다.

### 응답 예시

``` json
[
  {
      "idx": 2,
      "compName": "Summer Open 2025",
      "compDateStart": "2025-08-15",
      "compDateEnd": "2025-08-16",
      "joinDateStart": "2025-07-01T00:00:00",
      "joinDateEnd": "2025-07-20T23:59:59",
      "maxParticipate": 120
    }
]
```

### 응답

-   성공: `200 OK` + `List<CompetitionDTO>`

------------------------------------------------------------------------

## 2. 참가자 정보 조회

**GET** `/api/recorder/info/{uid}`  
참가자의 정보를 조회합니다.

### 요청 파라미터
| 이름    | 타입               | 설명        |
|-------|------------------|-----------|
| uid   | String           | 선수 카드 UID |

### 응답 예시

``` json
{
  "email": "user@example.com",
  "name": "홍길동",
  "enName": "Hong Gil-dong",
  "birthDate": "1990-01-01",
  "position": "CUSTOMER",
  "state": "Seoul",
  "gender": "MALE"
}
```

### 응답

-   성공: `200 OK`
- 실패: `204 NO CONTENT`

------------------------------------------------------------------------

## 3. 시도해야 하는 기록 조회

**GET** `/api/recorder/record/{email}/{compIdx}`\
특정 대회에서의 선수가 시도해야 하는 기록을 조회합니다.

### 요청 파라미터
| 이름      | 타입      | 설명       |
|---------|---------|----------|
| email   | String  | 선수 email |
| compIdx | Integer | 대회 ID    |

### 응답 예시

``` json
{
  "attemptIdx": 13,
  "seq": 3,
  "name": "Hong Gil-dong",
  "state": "Seoul",
  "roundName": "3x3x3 Final",
  "extraNum": null,
}
```

### 응답

-   성공: `200 OK`
- 실패: `204 NO CONTENT`

------------------------------------------------------------------------

## 4. 기록 업로드

**POST** `/api/recorder/record`\
선수 시도 결과인 기록을 업로드합니다.

### 요청 파라미터(해당 내용을 모아서 List로 보내야 함)
| 이름         | 타입         | 설명       |
|------------|------------|----------|
| attemptIdx | Integer    | 시도 ID    |
| judgeEmail | String     | 심판 email |
| record     | BigDecimal | 기록       |

### 요청 본문
```json
{
  "attemptIdx": 13,
  "judgeEmail": "example@example.com",
  "record": 9.42
}
```

### 응답 예시
``` json
{
    "single": 7.64,
    "average": 9.46
}
```

### 응답

-   성공: `200 OK`
-   성공, ao5에서 4회차: `200 OK`+`BPA5, WPA5`
-   성공, 마지막 시도: `200 OK`+`Single, Average`