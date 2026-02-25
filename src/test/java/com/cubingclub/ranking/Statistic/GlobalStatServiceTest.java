package com.cubingclub.ranking.Statistic;

import com.cubingclub.ranking.Record.DTO.RecordDTO;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Statistic.DTO.CompRecordStatDTO;
import com.cubingclub.ranking.Statistic.Repository.GlobalStatRepository;
import com.cubingclub.ranking.Statistic.Service.GlobalStatService;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalStat Service 테스트")
class GlobalStatServiceTest {

    @InjectMocks
    private GlobalStatService globalStatService;

    @Mock
    private GlobalStatRepository globalStatRepository;

    private Record testRecord;
    private CubeEvent testCubeEvent;
    private Round testRound;
    private Competition testCompetition;

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
                .single(BigDecimal.valueOf(8.50))
                .average(BigDecimal.valueOf(10.20))
                .build();

        List<Attempt> testAttempts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Attempt attempt = Attempt.builder()
                    .idx(i)
                    .record(testRecord)
                    .attemptTry(i)
                    .time(BigDecimal.valueOf(i <= 2 ? 8.50 + i : 10.00 + i))
                    .build();
            testAttempts.add(attempt);
        }

        testRecord = Record.builder()
                .idx(1)
                .round(testRound)
                .cckId("TEST001")
                .name("테스트 사용자")
                .state("서울")
                .roundGroup("A")
                .single(BigDecimal.valueOf(8.50))
                .average(BigDecimal.valueOf(10.20))
                .attempts(testAttempts)
                .build();
    }

    @Test
    @DisplayName("글로벌 상위 10개 Single 기록 조회 성공")
    void getGlobalTop10BySingleByEventSuccess() {
        // Given
        String eventName = "3x3x3";
        when(globalStatRepository.findGlobalSingleTop10(eventName))
                .thenReturn(List.of(testRecord));

        // When
        List<RecordDTO> result = globalStatService.getGlobalTop10BySingleByEvent(eventName);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCckId()).isEqualTo("TEST001");
        assertThat(result.get(0).getSingle()).isEqualTo(BigDecimal.valueOf(8.50));
        verify(globalStatRepository, times(1)).findGlobalSingleTop10(eventName);
    }

    @Test
    @DisplayName("글로벌 상위 10개 Average 기록 조회 성공")
    void getGlobalTop10ByAverageByEventSuccess() {
        // Given
        String eventName = "3x3x3";
        when(globalStatRepository.findGlobalAverageTop10(eventName))
                .thenReturn(List.of(testRecord));

        // When
        List<RecordDTO> result = globalStatService.getGlobalTop10ByAverageByEvent(eventName);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCckId()).isEqualTo("TEST001");
        assertThat(result.get(0).getAverage()).isEqualTo(BigDecimal.valueOf(10.20));
        verify(globalStatRepository, times(1)).findGlobalAverageTop10(eventName);
    }

    @Test
    @DisplayName("글로벌 기록 통계 대회별 조회 성공")
    void getGlobalRecordStatByCompetitionSuccess() {
        // Given
        String eventName = "3x3x3";
        CompRecordStatDTO statDTO = mock(CompRecordStatDTO.class);
        when(statDTO.getCompName()).thenReturn("테스트 대회");
        when(statDTO.getBestSingle()).thenReturn(BigDecimal.valueOf(7.00));
        when(statDTO.getBestAverage()).thenReturn(BigDecimal.valueOf(9.00));

        when(globalStatRepository.findGlobalRecordStatByCompetition(eventName))
                .thenReturn(List.of(statDTO));

        // When
        List<CompRecordStatDTO> result = globalStatService.getGlobalRecordStatByCompetition(eventName);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCompName()).isEqualTo("테스트 대회");
        assertThat(result.get(0).getBestSingle()).isEqualTo(BigDecimal.valueOf(7.00));
        assertThat(result.get(0).getBestAverage()).isEqualTo(BigDecimal.valueOf(9.00));
        verify(globalStatRepository, times(1)).findGlobalRecordStatByCompetition(eventName);
    }

    @Test
    @DisplayName("글로벌 기록 조회 - 빈 결과")
    void getGlobalTop10EmptyResult() {
        // Given
        String eventName = "Megaminx";
        when(globalStatRepository.findGlobalSingleTop10(eventName))
                .thenReturn(List.of());

        // When
        List<RecordDTO> result = globalStatService.getGlobalTop10BySingleByEvent(eventName);

        // Then
        assertThat(result).isEmpty();
        verify(globalStatRepository, times(1)).findGlobalSingleTop10(eventName);
    }
}
