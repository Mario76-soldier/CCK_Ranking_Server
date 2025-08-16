# Round API 상세 문서

## 개요
대회 라운드 생성, 조회, 삭제, 수정 및 그룹 관리 기능을 제공합니다.  
이 문서에서는 **라운드 관리 API**의 엔드포인트를 설명합니다.

---

## 1. 대회 라운드 목록 조회
**GET** `/api/rounds/{compIdx}`  
특정 대회의 라운드 목록을 조회합니다.

### 요청 파라미터
| 이름      | 타입      | 설명     |
|---------|---------|--------|
| compIdx | Integer | 대회 식별자 |

### 응답 예시
```json
{
  "past": [
    {
      "idx": 1,
      "comp": { ... },
      "cubeEvent": "3x3x3",
      "roundName": "1st Round",
      "eventStart": "2025-08-15T10:00:00",
      "eventEnd": "2025-08-15T12:00:00",
      "roundGroup": [ ... ],
      "advance": 16
    }
  ],
  "now": [
    {
      "idx": 2,
      "comp": { ... },
      "cubeEvent": "4x4x4",
      "roundName": "Final",
      "eventStart": "2025-08-15T12:00:00",
      "eventEnd": "2025-08-15T14:00:00",
      "roundGroup": [ ... ],
      "advance": 3
    }
  ],
  "future": [
    {
      "idx": 3,
      "comp": { ... },
      "cubeEvent": "3x3x3",
      "roundName": "Final",
      "eventStart": "2025-08-15T14:00:00",
      "eventEnd": "2025-08-15T15:00:00",
      "roundGroup": [ ... ],
      "advance": 3
    }
  ]
}
```
### 응답
- 성공: `200 OK` + `List<RoundDTO>`
---

## 2. 특정 라운드 조회
**GET** `/api/round/{roundIdx}`

### 요청 파라미터
| 이름       | 타입      | 설명     |
|----------|---------|--------|
| roundIdx | Integer | 라운드 ID |

### 응답 예시
```json
{
  "idx": 1,
  "comp": { ... },
  "cubeEvent": "333",
  "roundName": "1st Round",
  "eventStart": "2025-08-15T10:00:00",
  "eventEnd": "2025-08-15T12:00:00",
  "roundGroup": [ ... ],
  "advance": 16
}
```
### 응답
- 성공: `200 OK` + `RoundDTO`
- 라운드 없음: `204 No Content`

---

## 3. 라운드 생성/수정
**POST** `/api/round`  
여러 개의 라운드를 한 번에 생성합니다.

### 요청 파라미터
| 필드         | 타입            | 설명                |
|------------|---------------|-------------------|
| idx        | Integer       | 라운드 ID(없으면 신규 생성) |
| comp       | Integer       | 대회 ID             |
| cubeEvent  | CubeEvent     | 종목 (예: 333)       |
| roundName  | String        | 라운드 이름            |
| eventStart | LocalDateTime | 시작 시간             |
| eventEnd   | LocalDateTime | 종료 시간             |
| groupList  | List<String>  | 그룹명 리스트           |
| advance    | Integer       | 진출 인원 수           |

### 요청 본문 (라운드 생성)
```json
[
  {
    "comp": 1,
    "cubeEvent": "3x3x3",
    "roundName": "1st Round",
    "eventStart": "2025-08-15T10:00:00",
    "eventEnd": "2025-08-15T12:00:00",
    "groupList": ["A", "B"],
    "advance": 16
  },
  {
    "comp": 1,
    "cubeEvent": "4x4x4",
    "roundName": "Final",
    "eventStart": "2025-08-15T12:00:00",
    "eventEnd": "2025-08-15T14:00:00",
    "groupList": ["A", "B"],
    "advance": 3
  },
  {
    "comp": 1,
    "cubeEvent": "3x3x3",
    "roundName": "Final",
    "eventStart": "2025-08-15T14:00:00",
    "eventEnd": "2025-08-15T15:00:00",
    "groupList": ["Final"],
    "advance": 3
  }
]
```

### 요청 본문 (라운드 수정)
```json
[
  {
    "idx": 1,//인덱스 명시 시 라운드 수정으로 간주
    "comp": 1,
    "cubeEvent": "3x3x3",
    "roundName": "1st Round",
    "eventStart": "2025-08-15T10:00:00",
    "eventEnd": "2025-08-15T12:00:00",
    "groupList": ["A", "B"],
    "advance": 16
  },
  {
    "idx": 2,
    "comp": 1,
    "cubeEvent": "4x4x4",
    "roundName": "Final",
    "eventStart": "2025-08-15T12:00:00",
    "eventEnd": "2025-08-15T14:00:00",
    "groupList": ["A", "B"],
    "advance": 3
  },
  { 
    "idx": 3,
    "comp": 1,
    "cubeEvent": "3x3x3",
    "roundName": "Final",
    "eventStart": "2025-08-15T14:00:00",
    "eventEnd": "2025-08-15T15:00:00",
    "groupList": ["Final"],
    "advance": 3
  }
]
```

### 응답 예시
```json
[1, 2, 3] // 생성된 라운드 ID 목록
```
### 응답
- 성공: `200 OK` + `List<roundIdx>`
---

## 4. 라운드 삭제
**DELETE** `/api/round/{roundIdx}`

### 요청 파라미터
| 이름       | 타입      | 설명     |
|----------|---------|--------|
| roundIdx | Integer | 라운드 ID |

### 응답
- 성공: `200 OK`

---

## 5. 어드밴스 인원 수정
**PUT** `/api/advance/{roundIdx}/{advance}`  
라운드의 다음 라운드 진출 인원을 수정합니다.

### 요청 파라미터
| 이름       | 타입      | 설명      |
|----------|---------|---------|
| roundIdx | Integer | 라운드 ID  |
| advance  | Integer | 진출 인원 수 |

### 응답 예시
```json
16 // 수정된 advance 값
```

### 응답
- 성공: `200 OK` + `advance`
---

## 6. 라운드 그룹 목록 조회
**GET** `/api/group/{roundIdx}`

### 요청 파라미터
| 이름       | 타입      | 설명     |
|----------|---------|--------|
| roundIdx | Integer | 라운드 ID |

### 응답 예시
```json
["A", "B", "C"]
```
### 응답
- 성공: `200 OK` + `List<group>`
---
