# 코딩 컨벤션 규칙

## 1. 네이밍 규칙

### 1.1 클래스명
- **PascalCase** 사용
- 명사 형태로 작성
- 용도가 명확하게 드러나야 함

```java
// Good
public class CompetitionService { }
public class RoundController { }
public class CubeEventDTO { }

// Bad
public class competition_service { }
public class roundcontroller { }
```

### 1.2 메서드명
- **camelCase** 사용
- 동사로 시작
- 메서드의 동작을 명확히 표현

```java
// Good
public void createCompetition() { }
public List<RoundDTO> getRoundPast() { }
public void updateAdvance() { }

// Bad
public void Competition() { }
public List<RoundDTO> past() { }
public void advance() { }
```

### 1.3 변수명
- **camelCase** 사용
- 의미 있는 이름 사용
- 약어는 최소화

```java
// Good
private Competition testCompetition;
private Integer roundIdx;
private String eventName;

// Bad
private Competition tc;
private Integer idx;
private String n;
```

### 1.4 상수명
- **UPPER_SNAKE_CASE** 사용
- `static final`로 선언

```java
// Good
private static final int MAX_PARTICIPANTS = 100;
private static final String DEFAULT_COLOR = "white";

// Bad
private static final int maxParticipants = 100;
private static final String defaultColor = "white";
```

## 2. 패키지 구조

```
com.cubingclub.ranking
├── Auth/           # 인증 관련
├── Competition/    # 대회 관련
│   ├── Controller/
│   ├── Service/
│   ├── Repository/
│   ├── Entity/
│   └── DTO/
├── Round/          # 라운드 관련
├── CubeEvent/      # 큐브 이벤트 관련
├── Scrambler/      # 스크램블러 관련
├── Record/         # 기록 관련
├── Exception/      # 예외 처리
└── Config/         # 설정
```

## 3. 코드 작성 규칙

### 3.1 주석
- JavaDoc 스타일 사용
- 복잡한 로직에는 설명 주석 추가
- 불필요한 주석은 작성하지 않음

```java
/**
 * 대회를 생성합니다.
 * 
 * @param competitionCreateDTO 대회 생성 정보
 * @throws CustomizedException 큐브 이벤트를 찾을 수 없는 경우
 */
@Transactional
public void create(CompetitionCreateDTO competitionCreateDTO) {
    // 구현
}
```

### 3.2 메서드 길이
- 한 메서드는 가능한 한 하나의 역할만 수행
- 너무 긴 메서드는 분리
- 일반적으로 50줄을 넘지 않도록 권장

### 3.3 들여쓰기
- **4 스페이스** 사용
- 탭 문자 사용 금지

### 3.4 중괄호
- K&R 스타일 사용 (여는 중괄호는 같은 줄에)

```java
// Good
public void method() {
    if (condition) {
        // code
    } else {
        // code
    }
}

// Bad
public void method()
{
    if (condition)
    {
        // code
    }
}
```

## 4. 레이어별 규칙

### 4.1 Controller
- `@RestController` 사용
- RESTful API 설계 원칙 준수
- `ResponseEntity`로 응답 반환
- 비즈니스 로직은 Service로 위임

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/comp")
public class CompetitionController {
    private final CompetitionService competitionService;
    
    @GetMapping("/{compIdx}")
    public ResponseEntity<CompetitionDTO> getComp(@PathVariable Integer compIdx) {
        return ResponseEntity.ok(competitionService.getOne(compIdx));
    }
}
```

### 4.2 Service
- `@Service` 사용
- `@Transactional` 적절히 활용
- 읽기 전용 트랜잭션은 `@Transactional(readOnly=true)` 사용
- 비즈니스 로직 구현

```java
@Service
@RequiredArgsConstructor
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    
    @Transactional(readOnly = true)
    public CompetitionDTO getOne(Integer idx) {
        // 구현
    }
    
    @Transactional
    public void create(CompetitionCreateDTO dto) {
        // 구현
    }
}
```

### 4.3 Repository
- `JpaRepository` 상속
- 메서드명으로 쿼리 생성
- 복잡한 쿼리는 `@Query` 사용

```java
public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
    Page<Competition> findByCompDateEndBeforeOrderByCompDateStartDesc(
        Pageable pageable, 
        LocalDate currentTime
    );
}
```

### 4.4 Entity
- `@Entity` 사용
- 불변성 고려 (setter 최소화)
- Builder 패턴 활용
- 연관관계 설정 시 fetch 전략 고려

```java
@Entity
@Getter
@NoArgsConstructor
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;
    
    @Column(nullable = false)
    private String compName;
    
    @Builder
    public Competition(String compName, ...) {
        this.compName = compName;
    }
}
```

### 4.5 DTO
- 계층 간 데이터 전송용
- 불변 객체로 설계
- `@Builder` 사용
- Validation 어노테이션 활용

```java
@Getter
@Builder
public class CompetitionDTO {
    private Integer idx;
    
    @NotBlank
    private String compName;
    
    @NotNull
    private LocalDate compDateStart;
}
```

## 5. 예외 처리

### 5.1 커스텀 예외
- `CustomizedException` 사용
- ErrorCode enum으로 오류 코드 관리

```java
throw new CustomizedException(ErrorCode.COMPETITION_NOT_FOUND);
```

### 5.2 예외 처리 위치
- Controller에서 예외를 잡지 않음
- GlobalExceptionHandler에서 통합 처리

## 6. 테스트 코드

### 6.1 테스트 클래스명
- `{테스트 대상}Test` 형식

```java
CompetitionServiceTest
RoundControllerTest
```

### 6.2 테스트 메서드명
- 한글 사용 가능
- `@DisplayName` 활용
- Given-When-Then 패턴 사용

```java
@Test
@DisplayName("대회 생성 성공")
void createCompetitionSuccess() {
    // Given
    CompetitionCreateDTO dto = ...;
    
    // When
    competitionService.create(dto);
    
    // Then
    verify(competitionRepository, times(1)).save(any());
}
```

## 7. 로깅

### 7.1 로깅 레벨
- `ERROR`: 즉시 조치가 필요한 오류
- `WARN`: 잠재적 문제
- `INFO`: 중요한 비즈니스 로직 실행
- `DEBUG`: 디버깅용 상세 정보

### 7.2 로깅 사용
- `@Slf4j` 사용
- 민감한 정보는 로깅하지 않음

```java
@Slf4j
@Service
public class CompetitionService {
    public void create(CompetitionCreateDTO dto) {
        log.info("Creating competition: {}", dto.getCompName());
        // 구현
    }
}
```

## 8. Import 정리

- 사용하지 않는 import 제거
- 와일드카드(*) import 최소화
- 그룹별로 정리 (java, javax, org, com)

## 9. 코드 포맷팅

- IntelliJ IDEA 기본 포맷터 사용
- `Ctrl+Alt+L` (Windows/Linux) 또는 `Cmd+Option+L` (Mac)로 자동 포맷팅
- 저장 시 자동 포맷팅 설정 권장
