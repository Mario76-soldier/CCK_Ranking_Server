# Competition API 상세 문서

## 개요
대회 생성, 조회, 삭제, 수정 및 참가자 관리 기능을 제공합니다.
이 문서에서는 대회 관리 API의 엔드포인트와 DTO 구조를 설명합니다.

---

## 1. 대회 목록 조회
**GET** `/api/comps`  
현재 날짜 기준으로 대회를 조회합니다.

### 응답 예시
```json
{
  "past": [
    {
      "idx": 1,
      "compName": "Spring Open 2025",
      "compDateStart": "2025-04-01",
      "compDateEnd": "2025-04-02",
      "joinDateStart": "2025-03-01T00:00:00",
      "joinDateEnd": "2025-03-15T23:59:59",
      "maxParticipate": 100
    }
  ],
  "now": [
    {
      "idx": 2,
      "compName": "Summer Open 2025",
      "compDateStart": "2025-08-15",
      "compDateEnd": "2025-08-16",
      "joinDateStart": "2025-07-01T00:00:00",
      "joinDateEnd": "2025-07-20T23:59:59",
      "maxParticipate": 120
    }
  ],
  "future": [
    {
      "idx": 3,
      "compName": "Winter Open 2025",
      "compDateStart": "2025-12-10",
      "compDateEnd": "2025-12-11",
      "joinDateStart": "2025-11-01T00:00:00",
      "joinDateEnd": "2025-11-20T23:59:59",
      "maxParticipate": 150
    }
  ]
}
```
### 응답
- 성공: `200 OK` + `List<CompetitionDTO>`
---

## 2. 대회 단일 조회
**GET** `/api/comp/{compIdx}`

### 요청 파라미터
| 이름      | 타입      | 설명     |
|---------|---------|--------|
| compIdx | Integer | 대회 식별자 |

### 응답 예시
```json
{
  "idx": 2,
  "compName": "Summer Open 2025",
  "compDateStart": "2025-08-15",
  "compDateEnd": "2025-08-16",
  "joinDateStart": "2025-07-01T00:00:00",
  "joinDateEnd": "2025-07-20T23:59:59",
  "maxParticipate": 120
}
```
### 응답
- 성공: `200 OK` + `CompetitionDTO`
- 대회 없음: `204 No Content`

---

## 3. 대회 생성/수정
**POST** `/api/comp`

### 요청 파라미터
| 필드             | 타입            | 설명               |
|----------------|---------------|------------------|
| idx            | Integer       | 대회 ID(없으면 신규 생성) |
| compName       | String        | 대회 이름            |
| compDateStart  | LocalDate     | 대회 시작            |
| compDateEnd    | LocalDate     | 대회 종료            |
| joinDateStart  | LocalDateTime | 접수 시작            |
| joinDateEnd    | LocalDateTime | 접수 종료            |
| maxParticipate | Integer       | 최대 인원            |

### 요청 본문 (대회 생성)
```json
{
  "compName": "Autumn Open 2025",
  "compDateStart": "2025-10-01",
  "compDateEnd": "2025-10-02",
  "joinDateStart": "2025-09-01T00:00:00",
  "joinDateEnd": "2025-09-15T23:59:59",
  "maxParticipate": 80
}
```

### 요청 본문 (대회 수정)
```json
{
  "idx": 4, //인덱스를 명시하면 수정으로 간주
  "compName": "Autumn Open 2025-modified",
  "compDateStart": "2025-10-01",
  "compDateEnd": "2025-10-02",
  "joinDateStart": "2025-09-01T00:00:00",
  "joinDateEnd": "2025-09-15T23:59:59",
  "maxParticipate": 80
}
```

### 응답 예시
```json
4   // 생성된 compIdx
```
### 응답
- 성공: `200 OK` + `compIdx`


## 4. 대회 삭제
**DELETE** `/api/comp/{compIdx}`

### 요청 파라미터
| 이름      | 타입      | 설명    |
|---------|---------|-------|
| compIdx | Integer | 대회 ID |

### 응답
- 성공: `200 OK`

---

## 5. 참가자 목록 조회
**GET** `/api/competitor/{compIdx}`  
특정 대회의 참가자 목록을 조회합니다.

### 요청 파라미터
| 이름      | 타입      | 설명    |
|---------|---------|-------|
| compIdx | Integer | 대회 ID |

### 응답 예시
```json
[
  {
    "idx": 1,
    "account": { ... },
    "competition": { ... },
    "status": "WAITING",
    "waitingOrder": 5,
    "timeStamp": "2025-07-05T12:00:00",
    "eventList": ["333", "444", "555"],
    "attended": false
  }
]
```

### 응답
- 성공: `200 OK`+`List<CompetitorDTO>`

## 6. 참가자 출석 처리
**POST** `/api/competitor/{compIdx}/{uid}`  
특정 대회의 참가자 출석 여부를 갱신합니다.

### 요청 파라미터
| 이름      | 타입      | 설명        |
|---------|---------|-----------|
| compIdx | Integer | 대회 ID     |
| uid     | String  | 선수 카드 UID |

### 응답
- 성공: `200 OK`
- 선수 없음: `204 No Content`