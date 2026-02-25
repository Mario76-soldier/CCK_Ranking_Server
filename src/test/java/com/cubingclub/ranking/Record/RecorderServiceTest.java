package com.cubingclub.ranking.Record;

import com.cubingclub.ranking.Auth.DTO.PlayerInfo;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Record.DTO.RecorderDTO;
import com.cubingclub.ranking.Record.DTO.RecorderResponseDTO;
import com.cubingclub.ranking.Record.DTO.SingleAverageDTO;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Repository.AttemptRepository;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import com.cubingclub.ranking.Record.Service.RecordCalculator;
import com.cubingclub.ranking.Record.Service.RecordService;
import com.cubingclub.ranking.Record.Service.RecorderService;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recorder Service 테스트")
class RecorderServiceTest {

    @InjectMocks
    private RecorderService recorderService;

    @Mock
    private AuthService authService;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private RecordService recordService;

    @Mock
    private RecordCalculator recordCalculator;

    private Record testRecord;
    private Competition testCompetition;
    private Round testRound;
    private CubeEvent testCubeEvent;
    private List<Attempt> testAttempts;

    @BeforeEach
    void setUp() {
        testCompetition = Competition.builder()
                .Idx(1)
                .CompName("테스트 대회")
                .CompDateStart(LocalDate.now())
                .CompDateEnd(LocalDate.now().plusDays(1))
                .build();

        testCubeEvent = CubeEvent.builder()
                .eventName("3x3x3")
                .avgCalc("ao5")
                .colorType(ColorType.cube)
                .build();

        testRound = Round.builder()
                .idx(1)
                .comp(testCompetition)
                .cubeEvent(testCubeEvent)
                .roundName("Round 1")
                .eventStart(LocalDateTime.now())
                .eventEnd(LocalDateTime.now().plusHours(2))
                .build();

        testRecord = Record.builder()
                .idx(1)
                .round(testRound)
                .cckId("TEST001")
                .name("테스트 사용자")
                .state("서울")
                .roundGroup("A")
                .build();

        testAttempts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Attempt attempt = Attempt.builder()
                    .idx(i)
                    .record(testRecord)
                    .attemptTry(i)
                    .time(i <= 2 ? BigDecimal.valueOf(10.00 + i) : BigDecimal.ZERO)
                    .build();
            testAttempts.add(attempt);
        }
        
        // Record를 attempts와 함께 재생성
        testRecord = Record.builder()
                .idx(1)
                .round(testRound)
                .cckId("TEST001")
                .name("테스트 사용자")
                .state("서울")
                .roundGroup("A")
                .attempts(testAttempts)
                .build();
    }

    @Test
    @DisplayName("기록자용 다음 기록 조회 성공")
    void getForRecorderSuccess() {
        // Given
        String cckId = "TEST001";
        Integer compIdx = 1;

        when(recordRepository.findByCckIdAndRound_Comp_IdxOrderByRound_EventStart(cckId, compIdx))
                .thenReturn(List.of(testRecord));

        // When
        RecorderResponseDTO result = recorderService.getForRecorder(cckId, compIdx);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAttemptIdx()).isEqualTo(3); // 3번째 시도 (1,2는 기록됨, 3은 0)
        assertThat(result.getSeq()).isEqualTo(3);
        assertThat(result.getName()).isEqualTo("테스트 사용자");
        verify(recordRepository, times(1))
                .findByCckIdAndRound_Comp_IdxOrderByRound_EventStart(cckId, compIdx);
    }

    @Test
    @DisplayName("기록자용 조회 - 모든 시도 완료")
    void getForRecorderAllCompleted() {
        // Given
        String cckId = "TEST001";
        Integer compIdx = 1;

        // 모든 시도에 기록 입력
        for (Attempt attempt : testAttempts) {
            attempt.updateTime(BigDecimal.valueOf(10.00));
        }

        when(recordRepository.findByCckIdAndRound_Comp_IdxOrderByRound_EventStart(cckId, compIdx))
                .thenReturn(List.of(testRecord));

        // When & Then
        assertThatThrownBy(() -> recorderService.getForRecorder(cckId, compIdx))
                .isInstanceOf(CustomizedException.class);
    }

    @Test
    @DisplayName("시도 업데이트 성공 - 4번째 시도")
    void updateAttemptFourthTry() {
        // Given
        RecorderDTO recorderDTO = new RecorderDTO();
        recorderDTO.setAttemptIdx(4);
        recorderDTO.setRecord(BigDecimal.valueOf(12.50));
        recorderDTO.setJudgeCckId("JUDGE01");

        PlayerInfo judgeInfo = new PlayerInfo();
        judgeInfo.setName("심판");
        judgeInfo.setCckId("JUDGE01");

        // 4번째 시도용 Record 생성 (1,2,3번은 완료, 4번째만 남음)
        Record fourthRecord = Record.builder()
                .idx(2)
                .round(testRound)
                .cckId("TEST002")
                .name("테스트 사용자2")
                .state("서울")
                .roundGroup("A")
                .build();

        List<Attempt> fourthAttempts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Attempt attempt = Attempt.builder()
                    .idx(i)
                    .record(fourthRecord)
                    .attemptTry(i)
                    .time(BigDecimal.valueOf(10.00 + i))
                    .build();
            fourthAttempts.add(attempt);
        }

        fourthRecord = Record.builder()
                .idx(2)
                .round(testRound)
                .cckId("TEST002")
                .name("테스트 사용자2")
                .state("서울")
                .roundGroup("A")
                .attempts(fourthAttempts)
                .build();

        // fourthAttempts를 fourthRecord 참조로 다시 생성
        fourthAttempts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Attempt attempt = Attempt.builder()
                    .idx(i)
                    .record(fourthRecord)
                    .attemptTry(i)
                    .time(BigDecimal.valueOf(10.00 + i))
                    .build();
            fourthAttempts.add(attempt);
        }

        fourthRecord = Record.builder()
                .idx(2)
                .round(testRound)
                .cckId("TEST002")
                .name("테스트 사용자2")
                .state("서울")
                .roundGroup("A")
                .attempts(fourthAttempts)
                .build();

        Attempt fourthAttempt = fourthAttempts.get(3);

        when(authService.getPlayerInfo("JUDGE01")).thenReturn(judgeInfo);
        when(attemptRepository.findById(4)).thenReturn(Optional.of(fourthAttempt));
        when(attemptRepository.findByIdx(4)).thenReturn(fourthAttempt);
        when(recordRepository.findByIdx(2)).thenReturn(fourthRecord);
        when(recordCalculator.calculateAoXAverage(any())).thenReturn(BigDecimal.valueOf(12.50));
        when(recordCalculator.formatTime(any())).thenReturn("12.50");

        // When
        SingleAverageDTO result = recorderService.updateAttempt(recorderDTO);

        // Then
        assertThat(result).isNotNull(); // 4번째 시도에서는 예상값 리턴
        assertThat(result.getSingle()).isEqualTo("12.50");
        assertThat(result.getAverage()).isEqualTo("12.50");
        verify(authService, times(1)).getPlayerInfo("JUDGE01");
        verify(attemptRepository, times(1)).findById(4);
    }

    @Test
    @DisplayName("시도 업데이트 성공 - 5번째 시도 (완료)")
    void updateAttemptFifthTry() {
        // Given
        RecorderDTO recorderDTO = new RecorderDTO();
        recorderDTO.setAttemptIdx(5);
        recorderDTO.setRecord(BigDecimal.valueOf(13.00));
        recorderDTO.setJudgeCckId("JUDGE01");

        PlayerInfo judgeInfo = new PlayerInfo();
        judgeInfo.setName("심판");
        judgeInfo.setCckId("JUDGE01");

        Attempt fifthAttempt = Attempt.builder()
                .idx(5)
                .record(testRecord)
                .attemptTry(5)
                .time(BigDecimal.ZERO)
                .build();
        testRecord.updateRecord(BigDecimal.valueOf(11.00), BigDecimal.valueOf(12.00));

        when(authService.getPlayerInfo("JUDGE01")).thenReturn(judgeInfo);
        when(attemptRepository.findById(5)).thenReturn(Optional.of(fifthAttempt));
        when(attemptRepository.findByIdx(5)).thenReturn(fifthAttempt);
        when(recordRepository.findByIdx(1)).thenReturn(testRecord);
        when(recordCalculator.formatTime(BigDecimal.valueOf(11.00))).thenReturn("11.00");
        when(recordCalculator.formatTime(BigDecimal.valueOf(12.00))).thenReturn("12.00");

        // When
        SingleAverageDTO result = recorderService.updateAttempt(recorderDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSingle()).isEqualTo("11.00");
        assertThat(result.getAverage()).isEqualTo("12.00");
        verify(recordCalculator, times(1)).formatTime(BigDecimal.valueOf(11.00));
        verify(recordCalculator, times(1)).formatTime(BigDecimal.valueOf(12.00));
    }

    @Test
    @DisplayName("시도 업데이트 - mo3의 경우 3번째 시도에서 완료")
    void updateAttemptMo3ThirdTry() {
        // Given - mo3 이벤트를 위한 새로운 Round 생성
        CubeEvent mo3CubeEvent = CubeEvent.builder()
                .eventName("3x3x3 OH")
                .avgCalc("mo3")
                .colorType(ColorType.cube)
                .build();
        
        Round mo3Round = Round.builder()
                .idx(2)
                .comp(testCompetition)
                .cubeEvent(mo3CubeEvent)
                .roundName("Round 1")
                .eventStart(LocalDateTime.now())
                .eventEnd(LocalDateTime.now().plusHours(2))
                .build();
        
        Record mo3Record = Record.builder()
                .idx(2)
                .round(mo3Round)
                .cckId("TEST001")
                .name("테스트 사용자")
                .state("서울")
                .roundGroup("A")
                .single(BigDecimal.valueOf(11.00))
                .average(BigDecimal.valueOf(12.00))
                .build();

        RecorderDTO recorderDTO = new RecorderDTO();
        recorderDTO.setAttemptIdx(3);
        recorderDTO.setRecord(BigDecimal.valueOf(14.00));
        recorderDTO.setJudgeCckId("JUDGE01");

        PlayerInfo judgeInfo = new PlayerInfo();
        judgeInfo.setName("심판");
        judgeInfo.setCckId("JUDGE01");

        Attempt thirdAttempt = Attempt.builder()
                .idx(3)
                .record(mo3Record)
                .attemptTry(3)
                .time(BigDecimal.ZERO)
                .build();

        when(authService.getPlayerInfo("JUDGE01")).thenReturn(judgeInfo);
        when(attemptRepository.findById(3)).thenReturn(Optional.of(thirdAttempt));
        when(attemptRepository.findByIdx(3)).thenReturn(thirdAttempt);
        when(recordRepository.findByIdx(2)).thenReturn(mo3Record);
        when(recordCalculator.formatTime(any())).thenReturn("11.00", "12.00");

        // When
        SingleAverageDTO result = recorderService.updateAttempt(recorderDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSingle()).isEqualTo("11.00");
        assertThat(result.getAverage()).isEqualTo("12.00");
    }

    @Test
    @DisplayName("시도 업데이트 - single의 경우 1번째 시도에서 완료")
    void updateAttemptSingleFirstTry() {
        // Given - single 이벤트를 위한 새로운 Round 생성
        CubeEvent singleCubeEvent = CubeEvent.builder()
                .eventName("3x3x3 Blindfolded")
                .avgCalc("single")
                .colorType(ColorType.cube)
                .build();
        
        Round singleRound = Round.builder()
                .idx(3)
                .comp(testCompetition)
                .cubeEvent(singleCubeEvent)
                .roundName("Round 1")
                .eventStart(LocalDateTime.now())
                .eventEnd(LocalDateTime.now().plusHours(2))
                .build();
        
        Record singleRecord = Record.builder()
                .idx(3)
                .round(singleRound)
                .cckId("TEST001")
                .name("테스트 사용자")
                .state("서울")
                .roundGroup("A")
                .single(BigDecimal.valueOf(45.00))
                .average(BigDecimal.valueOf(45.00))
                .build();

        RecorderDTO recorderDTO = new RecorderDTO();
        recorderDTO.setAttemptIdx(1);
        recorderDTO.setRecord(BigDecimal.valueOf(45.00));
        recorderDTO.setJudgeCckId("JUDGE01");

        PlayerInfo judgeInfo = new PlayerInfo();
        judgeInfo.setName("심판");
        judgeInfo.setCckId("JUDGE01");

        Attempt firstAttempt = Attempt.builder()
                .idx(1)
                .record(singleRecord)
                .attemptTry(1)
                .time(BigDecimal.ZERO)
                .build();

        when(authService.getPlayerInfo("JUDGE01")).thenReturn(judgeInfo);
        when(attemptRepository.findById(1)).thenReturn(Optional.of(firstAttempt));
        when(attemptRepository.findByIdx(1)).thenReturn(firstAttempt);
        when(recordRepository.findByIdx(3)).thenReturn(singleRecord);
        when(recordCalculator.formatTime(BigDecimal.valueOf(45.00))).thenReturn("45.00");

        // When
        SingleAverageDTO result = recorderService.updateAttempt(recorderDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSingle()).isEqualTo("45.00");
        assertThat(result.getAverage()).isNull(); // single의 경우 average null
    }
}
