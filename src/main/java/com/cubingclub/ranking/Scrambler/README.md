# Scrambler API 상세 문서

## 개요

선수 별 스크램블 조회 및 스크램블러 사인 기능을 제공합니다.
이 문서에서는 **Scrambler API**의 엔드포인트에 대해 설명합니다.

------------------------------------------------------------------------

## 1. 특정 선수 스크램블 조회

**GET** `/api/scramble/{compIdx}/{uid}`\
특정 선수의 특정 대회에서의 스크램블을 조회합니다.  
선수의 진행상황에 맞추어서 스크램블 조회 내용이 달라집니다. 

### 요청 파라미터
| 이름      | 타입      | 설명        |
|---------|---------|-----------|
| compIdx | Integer | 대회 ID     |
| uid     | String  | 선수 카드 UID |

### 응답 예시

``` json
{
    "idx": 1,
    "attemptIdx": 10,
    "account": { ... },
    "round": { ... },
    "roundGroup": "A",
    "seq": "3", 
    "scramble": "R U R' U' F R2 ...",
    "svg": "<svg>...</svg>"
  }
```

### 응답

-   성공: `200 OK` + `ScramblerResponseDTO`
-   실패: `204 NO CONTENT`
------------------------------------------------------------------------

## 2. 스크램블 리스트 조회

**GET** `/api/scrambles/{roundIdx}`  
라운드 내의 모든 조에 대한 스크램블 리스트를 조회합니다.

### 요청 파라미터
| 이름       | 타입               | 설명     |
|----------|------------------|--------|
| roundIdx | Integer          | 라운드 ID |

### 요청 본문 (ParticipateRecordDTO)

``` json
{
    "A": [
        {
            "idx": 1,
            "round": { ... },
            "roundGroup": "A",
            "seq": "1",
            "scramble": "R U R' U' F R2 U' R' U' R U R U R'",
            "svg": "<svg>...</svg>"
        },
        {
            "idx": 2,
            "round": { ... },
            "roundGroup": "A",
            "seq": "1",
            "scramble": "R U R' U' F R2 U' R' U' R U R U R'",
            "svg": "<svg>...</svg>"
        },
        ...
    ],
    "B": [...]
}
```

### 응답

-   성공: `200 OK`

------------------------------------------------------------------------

## 3. 스크램블러 사인

**POST** `/api/scrambler`\
특정 스크램블에 대해 스크램블러가 확인하는 절차입니다.

### 요청 파라미터
| 이름         | 타입      | 설명        |
|------------|---------|-----------|
| attemptIdx | Integer | 시도 ID     |
| uid        | String  | 선수 카드 UID |
| exNumber   | Integer | 엑스트라 번호   |

### 응답
- 성공: `200 OK`
- 실패-선수와 스크램블러 동일: `409 CONFLICT`
- 실패-스크램블러 미존재: `204 NO CONTENT`