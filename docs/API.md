# API Documentation

## Competition API

### 1. 지난 대회 목록 조회
- **Endpoint**: `GET /comp/past`
- **Description**: 종료된 대회 목록을 페이징하여 조회합니다.
- **Parameters**:
  - `page` (query, optional): 페이지 번호 (default: 0)
  - `size` (query, optional): 페이지 크기 (default: 20)
- **Response**: `Page<CompetitionDTO>`
```json
{
  "content": [
    {
      "idx": 1,
      "compName": "2026 서울 오픈",
      "compDateStart": "2026-01-10",
      "compDateEnd": "2026-01-11",
      "joinDateStart": "2025-12-01T00:00:00",
      "joinDateEnd": "2026-01-05T23:59:59",
      "baseFee": 10000,
      "location": "서울",
      "maxParticipate": 100,
      "eventFees": [
        {
          "eventName": "3x3x3",
          "fee": 5000
        }
      ]
    }
  ],
  "pageable": {...},
  "totalElements": 10,
  "totalPages": 1
}
```

### 2. 진행 중인 대회 목록 조회
- **Endpoint**: `GET /comp/now`
- **Description**: 현재 진행 중인 대회 목록을 페이징하여 조회합니다.
- **Parameters**:
  - `page` (query, optional): 페이지 번호
  - `size` (query, optional): 페이지 크기
- **Response**: `Page<CompetitionDTO>`

### 3. 예정된 대회 목록 조회
- **Endpoint**: `GET /comp/future`
- **Description**: 앞으로 열릴 예정인 대회 목록을 페이징하여 조회합니다.
- **Parameters**:
  - `page` (query, optional): 페이지 번호
  - `size` (query, optional): 페이지 크기
- **Response**: `Page<CompetitionDTO>`

### 4. 단일 대회 조회
- **Endpoint**: `GET /comp/{compIdx}`
- **Description**: 특정 대회의 상세 정보를 조회합니다.
- **Parameters**:
  - `compIdx` (path, required): 대회 ID
- **Response**: `CompetitionDTO`

### 5. 대회 일정 조회
- **Endpoint**: `GET /comp/schedule/{compIdx}`
- **Description**: 특정 대회의 일정(라운드 포함)을 조회합니다.
- **Parameters**:
  - `compIdx` (path, required): 대회 ID
- **Response**: `CompetitionScheduleDTO`
```json
{
  "idx": 1,
  "compName": "2026 서울 오픈",
  "compDateStart": "2026-01-10",
  "compDateEnd": "2026-01-11",
  "location": "서울",
  "rounds": [
    {
      "idx": 1,
      "roundName": "Round 1",
      "cubeEvent": "3x3x3",
      "eventStart": "2026-01-10T10:00:00",
      "eventEnd": "2026-01-10T12:00:00"
    }
  ]
}
```

### 6. 대회 생성
- **Endpoint**: `POST /comp`
- **Description**: 새로운 대회를 생성합니다.
- **Request Body**: `CompetitionCreateDTO`
```json
{
  "compName": "2026 서울 오픈",
  "compDateStart": "2026-01-10",
  "compDateEnd": "2026-01-11",
  "joinDateStart": "2025-12-01T00:00:00",
  "joinDateEnd": "2026-01-05T23:59:59",
  "baseFee": 10000,
  "location": "서울",
  "maxParticipate": 100,
  "eventFeeList": [
    {
      "eventName": "3x3x3",
      "fee": 5000
    }
  ]
}
```
- **Response**: `200 OK`

### 7. 대회 수정
- **Endpoint**: `PUT /comp`
- **Description**: 기존 대회 정보를 수정합니다.
- **Request Body**: `CompetitionUpdateDTO`
```json
{
  "idx": 1,
  "compName": "2026 서울 오픈 (수정)",
  "compDateStart": "2026-01-10",
  "compDateEnd": "2026-01-11",
  "joinDateStart": "2025-12-01T00:00:00",
  "joinDateEnd": "2026-01-05T23:59:59",
  "baseFee": 15000,
  "location": "부산",
  "maxParticipate": 150
}
```
- **Response**: `200 OK`

### 8. 대회 삭제
- **Endpoint**: `DELETE /comp/comp/{compIdx}`
- **Description**: 특정 대회를 삭제합니다.
- **Parameters**:
  - `compIdx` (path, required): 대회 ID
- **Response**: `200 OK`

---

## Round API

### 1. 지난 라운드 목록 조회
- **Endpoint**: `GET /round/past/{compIdx}`
- **Description**: 특정 대회의 종료된 라운드 목록을 페이징하여 조회합니다.
- **Parameters**:
  - `compIdx` (path, required): 대회 ID
  - `page` (query, optional): 페이지 번호
  - `size` (query, optional): 페이지 크기
- **Response**: `Page<RoundDTO>`
```json
{
  "content": [
    {
      "idx": 1,
      "comp": 1,
      "cubeEvent": "3x3x3",
      "roundName": "Round 1",
      "eventStart": "2026-01-10T10:00:00",
      "eventEnd": "2026-01-10T12:00:00",
      "groupList": ["A", "B", "C"],
      "cutoff": 120.00,
      "timeLimit": 600.00,
      "advance": 75
    }
  ],
  "pageable": {...},
  "totalElements": 5,
  "totalPages": 1
}
```

### 2. 진행 중인 라운드 목록 조회
- **Endpoint**: `GET /round/now/{compIdx}`
- **Description**: 특정 대회의 현재 진행 중인 라운드 목록을 페이징하여 조회합니다.
- **Parameters**:
  - `compIdx` (path, required): 대회 ID
  - `page` (query, optional): 페이지 번호
  - `size` (query, optional): 페이지 크기
- **Response**: `Page<RoundDTO>`

### 3. 예정된 라운드 목록 조회
- **Endpoint**: `GET /round/future/{compIdx}`
- **Description**: 특정 대회의 앞으로 진행될 라운드 목록을 페이징하여 조회합니다.
- **Parameters**:
  - `compIdx` (path, required): 대회 ID
  - `page` (query, optional): 페이지 번호
  - `size` (query, optional): 페이지 크기
- **Response**: `Page<RoundDTO>`

### 4. 단일 라운드 조회
- **Endpoint**: `GET /round/{roundIdx}`
- **Description**: 특정 라운드의 상세 정보를 조회합니다.
- **Parameters**:
  - `roundIdx` (path, required): 라운드 ID
- **Response**: `RoundDTO`

### 5. 라운드 생성
- **Endpoint**: `POST /round`
- **Description**: 새로운 라운드를 생성합니다 (리스트 형태로 여러 개 가능).
- **Request Body**: `List<RoundCreateDTO>`
```json
[
  {
    "comp": 1,
    "cubeEvent": "3x3x3",
    "roundName": "Round 1",
    "eventStart": "2026-01-10T10:00:00",
    "eventEnd": "2026-01-10T12:00:00",
    "groupList": ["A", "B", "C"],
    "cutoff": 120.00,
    "timeLimit": 600.00,
    "advance": 75
  }
]
```
- **Response**: `200 OK`

### 6. 라운드 수정
- **Endpoint**: `PUT /round`
- **Description**: 기존 라운드 정보를 수정합니다 (리스트 형태로 여러 개 가능).
- **Request Body**: `List<RoundUpdateDTO>`
```json
[
  {
    "idx": 1,
    "cubeEvent": "3x3x3",
    "roundName": "Round 1 (수정)",
    "eventStart": "2026-01-10T10:00:00",
    "eventEnd": "2026-01-10T12:00:00",
    "groupList": ["A", "B"],
    "cutoff": 100.00,
    "timeLimit": 500.00,
    "advance": 50
  }
]
```
- **Response**: `200 OK`

### 7. 라운드 삭제
- **Endpoint**: `DELETE /round/{roundIdx}`
- **Description**: 특정 라운드를 삭제합니다.
- **Parameters**:
  - `roundIdx` (path, required): 라운드 ID
- **Response**: `200 OK`

### 8. 어드밴스 인원 수정
- **Endpoint**: `PUT /round/advance/{roundIdx}/{advance}`
- **Description**: 라운드의 어드밴스(다음 라운드 진출) 인원을 수정합니다.
- **Parameters**:
  - `roundIdx` (path, required): 라운드 ID
  - `advance` (path, required): 어드밴스 인원 수
- **Response**: `200 OK`

---

## CubeEvent API

### 1. 이벤트 목록 조회
- **Endpoint**: `GET /event`
- **Description**: 모든 큐브 이벤트 목록을 조회합니다.
- **Response**: `List<CubeEventDTO>`
```json
[
  {
    "eventName": "3x3x3",
    "avgCalc": "avg5",
    "colorType": "WCA"
  },
  {
    "eventName": "2x2x2",
    "avgCalc": "avg5",
    "colorType": "WCA"
  }
]
```

### 2. 이벤트 추가
- **Endpoint**: `POST /event`
- **Description**: 새로운 큐브 이벤트를 추가합니다.
- **Content-Type**: `multipart/form-data`
- **Parameters**:
  - `eventName` (form-data, required): 이벤트 이름
  - `avgCalc` (form-data, required): 평균 계산 방식 (예: "avg5", "avg3", "best")
  - `colorType` (form-data, required): 색상 타입 (예: "WCA", "PYRAMINX", "SKEWB")
  - `file` (form-data, optional): 이벤트 이미지 파일 (SVG)
- **Response**: `200 OK`

---

## Scrambler API

### 1. 사용자 확인
- **Endpoint**: `GET /scramble/check/{uid}`
- **Description**: RFID UID로 사용자를 확인합니다.
- **Parameters**:
  - `uid` (path, required): 사용자 UID
- **Headers**:
  - `Authorization` (required): Bearer {token}
- **Response**: `200 OK` (사용자 존재) / `404 Not Found` (사용자 없음)

### 2. 스크램블 조회
- **Endpoint**: `PATCH /scramble/scrambler`
- **Description**: 플레이어의 스크램블을 조회합니다.
- **Headers**:
  - `Authorization` (required): Bearer {token}
- **Request Body**: `ScramblerUpdateDTO`
```json
{
  "compIdx": 1,
  "playerUid": "test-uid"
}
```
- **Response**: `ScrambleResponseDTO`
```json
{
  "roundIdx": 1,
  "roundName": "Round 1",
  "cubeEvent": "3x3x3",
  "group": "A",
  "scrambles": [
    "R U R' U' R' F R2 U' R' U' R U R' F'",
    "F R U' R' U' R U R' F' R U R' U' R' F R F'",
    "R' U' F' U F R",
    "U R U' R' U' R' F R F'",
    "R U R' U R U2 R'"
  ]
}
```

---

## Error Responses

모든 API는 오류 발생 시 다음과 같은 형태의 응답을 반환합니다:

```json
{
  "timestamp": "2026-01-13T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "대회를 찾을 수 없습니다.",
  "path": "/comp/999"
}
```

### Common Error Codes
- `400 Bad Request`: 잘못된 요청 형식
- `404 Not Found`: 리소스를 찾을 수 없음
- `500 Internal Server Error`: 서버 내부 오류
