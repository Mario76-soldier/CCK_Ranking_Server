# Record API 상세 문서

## 개요

참가자 기록 생성, 조회, 삭제, 수정 및 추가 기록 관리 기능을 제공합니다.  
이 문서에서는 **기록 관리 API**의 엔드포인트에 대해 설명합니다.

------------------------------------------------------------------------

## 1. 라운드 기록 목록 조회

**GET** `/api/records/{roundIdx}`\
특정 라운드의 참가자 기록 목록을 조회합니다.

### 요청 파라미터
| 이름       | 타입      | 설명     |
|----------|---------|--------|
| roundIdx | Integer | 라운드 ID |

### 응답 예시

``` json
[
  {
    "idx": 1,
    "pdx": 10,
    "account": { ... },
    "round": { ... },
    "roundGroup": "A",
    "attempts": [ ... ],//해당 기록 내의 시도. ao5면 5개, bo3면 3개.
    "single": 12.34,
    "average": 13.21,
    "ranking": 1,
    "stateRanking": 1,
    "singleBestType": "SR",
    "averageBestType": "CCR"
  }
]
```

### 응답

-   성공: `200 OK` + `List<RecordDTO>`

------------------------------------------------------------------------

## 2. 기록 수정

**POST** `/api/record`  
특정 참가자의 기록을 수정합니다.

### 요청 파라미터
| 이름    | 타입               | 설명    |
|-------|------------------|-------|
| idx   | Integer          | 기록 ID |
| times | List<BigDecimal> | 기록 목록 |

### 요청 본문 (ParticipateRecordDTO)

``` json
{
  "idx": 1,
  "times": [12.34, 13.21, 14.55, 12.87, 13.00]
}
```

### 응답

-   성공: `200 OK`

------------------------------------------------------------------------

## 3. 기록 삭제

**DELETE** `/api/record/{idx}`\
특정 참가자의 기록을 삭제합니다.

### 요청 파라미터
| 이름    | 타입            | 설명    |
|-------|---------------|-------|
| idx   | Integer       | 기록 ID |

### 응답

-   성공: `200 OK`

------------------------------------------------------------------------

## 4. 참가자 기권 처리

**DELETE** `/api/record/quit`\
여러 참가자를 기권 처리합니다.

### 요청 파라미터(해당 내용을 모아서 List로 보내야 함)
| 이름  | 타입      | 설명            |
|-----|---------|---------------|
| idx | Integer | 기권한 기록 ID 리스트 |

### 요청 본문 예시

``` json
[1, 2, 3]
```

### 응답

-   성공: `200 OK`

------------------------------------------------------------------------

## 5. 참가자 추가

**POST** `/api/participate`\
새로운 참가자를 라운드에 추가합니다.

### 요청 파라미터(해당 내용을 모아서 List로 보내야 함)
| 이름         | 타입      | 설명                          |
|------------|---------|-----------------------------|
| pdx        | Integer | 해당 대회 내에서의 ID(수동 입력시 구분 위함) |
| email      | String  | 선수 이메일(식별자)                 |
| round      | Integer | 라운드 ID                      |
| roundGroup | String  | 라운드 내 그룹                    |

### 요청 본문 (RecordRequestDTO)

``` json
[
  {
    "pdx": 10,
    "email": "test@example.com",
    "round": 1,
    "roundGroup": "A"
  },
  {
    "pdx": 11,
    "email": "player2@example.com",
    "round": 1,
    "roundGroup": "B"
  }
]
```

### 응답 예시

``` json
2 // 추가된 참가자 수
```

### 응답

-   성공: `200 OK` + `참가자 수`

------------------------------------------------------------------------

## 6. 엑스트라 사유 등록

**POST** `/api/extra`\
엑스트라 발생 지점, 사유를 등록합니다.

### 요청 파라미터
| 이름          | 타입      | 설명           |
|-------------|---------|--------------|
| partIdx     | Integer | 기록 ID        |
| extraNum    | Integer | 엑스트라 번호(1,2) |
| extraTry    | Integer | 엑스트라 발생 지점   |
| extraReason | String  | 엑스트라 사유      |

### 요청 본문 (ExtraDTO)

``` json
{
  "partIdx": 1,
  "extraNum": 1,
  "extraTry": 6,
  "extraReason": "0.07초 이내 타이머 정지"
}
```

### 응답

-   성공: `200 OK`
-   실패: `400 Bad Request`

------------------------------------------------------------------------

## 7. 추가 기록 삭제

**DELETE** `/api/extra/{partIdx}/{extraNum}`\
특정 참가자의 추가 기록을 삭제합니다.

### 요청 파라미터
| 이름          | 타입      | 설명           |
|-------------|---------|--------------|
| partIdx     | Integer | 기록 ID        |
| extraNum    | Integer | 엑스트라 번호(1,2) |

### 응답

-   성공: `200 OK`
-   실패: `400 Bad Request`

---

## 비고: AttemptDTO

| 필드          | 타입         | 설명          |
|-------------|------------|-------------|
| idx         | Integer    | 시도 ID       |
| recordIdx   | Integer    | 기록 ID       |
| attemptTry  | Integer    | 시도 횟수       |
| time        | BigDecimal | 기록된 시간      |
| scrambler   | AccountDTO | 스크램블 담당자    |
| judge       | AccountDTO | 심판 계정       |
| extra       | Integer    | 추가 기록 여부    |
| exReason    | String     | 추가 사유       |
| delegate    | AccountDTO | 대표 계정       |
| exScrambler | AccountDTO | 추가 스크램블 담당자 |
