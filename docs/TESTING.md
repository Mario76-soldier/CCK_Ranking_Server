# 테스트 코드 작성 가이드

## 개요

이 문서는 CompRanking 프로젝트의 테스트 코드 작성 가이드입니다. 
JUnit 5와 Mockito를 사용하여 단위 테스트 및 통합 테스트를 작성합니다.

## 테스트 환경 설정

### 필요한 의존성

```gradle
dependencies {
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.mockito:mockito-core'
    testImplementation 'org.mockito:mockito-junit-jupiter'
    testImplementation 'org.assertj:assertj-core'
}
```

## 테스트 종류

### 1. 단위 테스트 (Unit Test)

#### Service 레이어 테스트

**목적:** 비즈니스 로직을 검증합니다.

**특징:**
- `@ExtendWith(MockitoExtension.class)` 사용
- 의존성은 `@Mock`으로 주입
- 테스트 대상은 `@InjectMocks`로 주입

**예시:**
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Competition Service 테스트")
class CompetitionServiceTest {

    @InjectMocks
    private CompetitionService competitionService;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CubeEventRepository cubeEventRepository;

    private Competition testCompetition;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        testCompetition = Competition.builder()
                .Idx(1)
                .CompName("테스트 대회")
                .build();
    }

    @Test
    @DisplayName("대회 생성 성공")
    void createCompetitionSuccess() {
        // Given
        CompetitionCreateDTO createDTO = CompetitionCreateDTO.builder()
                .compName("새 대회")
                .build();

        when(competitionRepository.save(any(Competition.class)))
                .thenReturn(testCompetition);

        // When
        competitionService.create(createDTO);

        // Then
        verify(competitionRepository, times(1)).save(any(Competition.class));
    }
}
```

### 2. Controller 테스트

**목적:** HTTP 요청/응답을 검증합니다.

**특징:**
- `@WebMvcTest` 사용
- `MockMvc`로 HTTP 요청 시뮬레이션
- Service는 `@MockBean`으로 주입

**예시:**
```java
@WebMvcTest(CompetitionController.class)
@DisplayName("Competition Controller 테스트")
class CompetitionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompetitionService competitionService;

    @Test
    @DisplayName("GET /comp/{compIdx} - 단일 대회 조회")
    void getComp() throws Exception {
        // Given
        CompetitionDTO dto = CompetitionDTO.builder()
                .idx(1)
                .compName("테스트 대회")
                .build();

        when(competitionService.getOne(1)).thenReturn(dto);

        // When & Then
        mockMvc.perform(get("/comp/{compIdx}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.compName").value("테스트 대회"));
    }
}
```

## 테스트 작성 패턴

### Given-When-Then 패턴

모든 테스트는 Given-When-Then 패턴을 따릅니다.

```java
@Test
@DisplayName("라운드 수정 성공")
void updateRoundSuccess() {
    // Given - 테스트 준비 (데이터 설정, Mock 동작 정의)
    RoundUpdateDTO updateDTO = RoundUpdateDTO.builder()
            .idx(1)
            .roundName("Updated Round")
            .build();

    when(roundRepository.findById(1)).thenReturn(Optional.of(testRound));
    when(cubeEventRepository.findById("3x3x3")).thenReturn(Optional.of(testCubeEvent));

    // When - 테스트 실행 (메서드 호출)
    roundService.updateRoundList(List.of(updateDTO));

    // Then - 결과 검증 (Assertion, Verify)
    verify(roundRepository, times(1)).findById(1);
    verify(cubeEventRepository, times(1)).findById("3x3x3");
}
```

## Mockito 사용법

### 1. Mock 객체 생성

```java
@Mock
private CompetitionRepository competitionRepository;

@Mock
private CubeEventRepository cubeEventRepository;
```

### 2. Mock 동작 정의

#### 기본 사용
```java
when(competitionRepository.findById(1))
    .thenReturn(Optional.of(testCompetition));
```

#### 예외 발생
```java
when(competitionRepository.findById(999))
    .thenReturn(Optional.empty());
```

#### 여러 번 호출 시 다른 결과
```java
when(repository.findById(any()))
    .thenReturn(Optional.of(entity1))
    .thenReturn(Optional.of(entity2));
```

#### void 메서드
```java
doNothing().when(competitionRepository).deleteById(1);
```

### 3. 메서드 호출 검증

#### 호출 횟수 검증
```java
verify(competitionRepository, times(1)).save(any(Competition.class));
verify(competitionRepository, never()).delete(any());
verify(competitionRepository, atLeastOnce()).findAll();
```

#### 인자 검증
```java
verify(competitionRepository).save(argThat(comp -> 
    comp.getCompName().equals("테스트 대회")
));
```

## AssertJ 사용법

### 1. 기본 검증

```java
assertThat(result).isNotNull();
assertThat(result.getCompName()).isEqualTo("테스트 대회");
assertThat(result.getIdx()).isEqualTo(1);
```

### 2. 컬렉션 검증

```java
assertThat(result).isNotNull();
assertThat(result).hasSize(2);
assertThat(result).isEmpty();
assertThat(result).contains(element1, element2);
assertThat(result.get(0).getCompName()).isEqualTo("테스트 대회");
```

### 3. 예외 검증

```java
assertThatThrownBy(() -> competitionService.getOne(999))
    .isInstanceOf(CustomizedException.class);

assertThatThrownBy(() -> competitionService.create(null))
    .isInstanceOf(CustomizedException.class)
    .hasMessage("대회를 찾을 수 없습니다.");
```

### 4. Boolean 검증

```java
assertThat(result.isActive()).isTrue();
assertThat(result.isEmpty()).isFalse();
```

## MockMvc 사용법

### 1. GET 요청

```java
mockMvc.perform(get("/comp/{compIdx}", 1))
    .andDo(print())
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.compName").value("테스트 대회"))
    .andExpect(jsonPath("$.location").value("서울"));
```

### 2. POST 요청

```java
mockMvc.perform(post("/comp")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createDTO)))
    .andDo(print())
    .andExpect(status().isOk());
```

### 3. PUT 요청

```java
mockMvc.perform(put("/round")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(List.of(updateDTO))))
    .andDo(print())
    .andExpect(status().isOk());
```

### 4. DELETE 요청

```java
mockMvc.perform(delete("/comp/comp/{compIdx}", 1))
    .andDo(print())
    .andExpect(status().isOk());
```

### 5. 쿼리 파라미터

```java
mockMvc.perform(get("/comp/past")
        .param("page", "0")
        .param("size", "10"))
    .andDo(print())
    .andExpect(status().isOk());
```

### 6. 헤더 추가

```java
mockMvc.perform(get("/scramble/check/{uid}", uid)
        .header("Authorization", "Bearer " + token))
    .andExpect(status().isOk());
```

### 7. Multipart 파일 업로드

```java
MockMultipartFile file = new MockMultipartFile(
    "file",
    "test.svg",
    "image/svg+xml",
    "<svg></svg>".getBytes()
);

mockMvc.perform(multipart("/event")
        .file(file)
        .param("eventName", "3x3x3")
        .param("avgCalc", "avg5")
        .param("colorType", "WCA"))
    .andExpect(status().isOk());
```

## 테스트 데이터 준비

### @BeforeEach 사용

```java
@BeforeEach
void setUp() {
    testCubeEvent = CubeEvent.builder()
            .eventName("3x3x3")
            .avgCalc("avg5")
            .colorType(ColorType.WCA)
            .build();

    testCompetition = Competition.builder()
            .Idx(1)
            .CompName("테스트 대회")
            .CompDateStart(LocalDate.now().plusDays(10))
            .CompDateEnd(LocalDate.now().plusDays(11))
            .baseFee(10000)
            .location("서울")
            .MaxParticipate(100)
            .build();
}
```

## 테스트 케이스 작성 가이드

### 1. 성공 케이스

```java
@Test
@DisplayName("대회 생성 성공")
void createCompetitionSuccess() {
    // 정상적인 입력으로 성공하는 경우
}
```

### 2. 실패 케이스

```java
@Test
@DisplayName("대회 수정 - 존재하지 않는 대회")
void updateCompetitionNotFound() {
    // 존재하지 않는 리소스로 실패하는 경우
}
```

### 3. 경계값 테스트

```java
@Test
@DisplayName("빈 그룹 리스트로 스크램블 저장")
void saveScrambleWithEmptyGroups() {
    // 빈 리스트, null 등 경계값 테스트
}
```

### 4. 다양한 조건 테스트

```java
@Test
@DisplayName("다양한 퍼즐 타입으로 스크램블 저장")
void saveScrambleWithDifferentPuzzleTypes() {
    // 다양한 입력값으로 테스트
}
```

## 테스트 명명 규칙

### 클래스명
- `{테스트 대상 클래스}Test`
- 예: `CompetitionServiceTest`, `RoundControllerTest`

### 메서드명
- 한글 사용 가능
- 명확하고 설명적으로 작성
- 예: `createCompetitionSuccess`, `대회_생성_성공`

### @DisplayName
- 항상 사용
- 한글로 작성
- 테스트의 목적을 명확히 표현

```java
@Test
@DisplayName("지난 대회 목록 조회")
void getCompPastSuccess() {
    // 테스트 코드
}
```

## 테스트 실행

### IDE에서 실행
- IntelliJ IDEA: 클래스/메서드 옆 재생 버튼 클릭
- 단축키: `Ctrl+Shift+F10` (Windows/Linux), `Cmd+Shift+R` (Mac)

### Gradle로 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 클래스 테스트
./gradlew test --tests CompetitionServiceTest

# 특정 메서드 테스트
./gradlew test --tests CompetitionServiceTest.createCompetitionSuccess
```

### 테스트 커버리지 확인

```bash
./gradlew test jacocoTestReport
```

리포트 위치: `build/reports/jacoco/test/html/index.html`

## 테스트 작성 체크리스트

- [ ] Given-When-Then 패턴 준수
- [ ] @DisplayName 작성
- [ ] 성공 케이스 작성
- [ ] 실패 케이스 작성
- [ ] 경계값 테스트
- [ ] Mock 검증 (verify)
- [ ] Assertion 작성
- [ ] 테스트 독립성 보장 (각 테스트는 독립적으로 실행 가능)
- [ ] 테스트 데이터 정리 (@BeforeEach, @AfterEach)

## 주의사항

### DO
✅ 테스트는 독립적으로 실행 가능해야 함
✅ 테스트 데이터는 @BeforeEach에서 초기화
✅ 의미 있는 테스트 이름 사용
✅ Given-When-Then 주석 사용
✅ 하나의 테스트는 하나의 케이스만 검증

### DON'T
❌ 실제 데이터베이스 사용
❌ 테스트 간 의존성 생성
❌ 순서에 의존하는 테스트 작성
❌ Thread.sleep() 등 시간 의존적 테스트
❌ 랜덤 값으로 테스트 (재현 불가능)

## 참고 자료

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
