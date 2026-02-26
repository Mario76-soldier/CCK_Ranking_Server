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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    void getGlobalSingleByEventSuccess() {
        // Given
        String eventName = "3x3x3";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> recordPage = new PageImpl<>(List.of(testRecord), pageable, 1);
        when(globalStatRepository.findGlobalSingle(eventName, pageable))
                .thenReturn(recordPage);

        // When
        Page<RecordDTO> result = globalStatService.getGlobalSingleByEvent(eventName, pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getCckId()).isEqualTo("TEST001");
        assertThat(result.getContent().get(0).getSingle()).isEqualTo(BigDecimal.valueOf(8.50));
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(globalStatRepository, times(1)).findGlobalSingle(eventName, pageable);
    }

    @Test
    @DisplayName("글로벌 상위 10개 Average 기록 조회 성공")
    void getGlobalAverageByEventSuccess() {
        // Given
        String eventName = "3x3x3";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> recordPage = new PageImpl<>(List.of(testRecord), pageable, 1);
        when(globalStatRepository.findGlobalAverage(eventName, pageable))
                .thenReturn(recordPage);

        // When
        Page<RecordDTO> result = globalStatService.getGlobalAverageByEvent(eventName, pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getCckId()).isEqualTo("TEST001");
        assertThat(result.getContent().get(0).getAverage()).isEqualTo(BigDecimal.valueOf(10.20));
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(globalStatRepository, times(1)).findGlobalAverage(eventName, pageable);
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
        Pageable pageable = PageRequest.of(0, 10);
        Page<Record> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(globalStatRepository.findGlobalSingle(eventName, pageable))
                .thenReturn(emptyPage);

        // When
        Page<RecordDTO> result = globalStatService.getGlobalSingleByEvent(eventName, pageable);

        // Then
        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(globalStatRepository, times(1)).findGlobalSingle(eventName, pageable);
    }

    @Test
    @DisplayName("글로벌 기록 조회 - 페이지네이션 테스트")
    void getGlobalSingleByEventWithPagination() {
        // Given
        String eventName = "3x3x3";
        Pageable pageable = PageRequest.of(1, 10); // 두 번째 페이지
        Page<Record> recordPage = new PageImpl<>(List.of(testRecord), pageable, 25);
        when(globalStatRepository.findGlobalSingle(eventName, pageable))
                .thenReturn(recordPage);

        // When
        Page<RecordDTO> result = globalStatService.getGlobalSingleByEvent(eventName, pageable);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result.getNumber()).isEqualTo(1); // 현재 페이지
        assertThat(result.getSize()).isEqualTo(10); // 페이지 크기
        assertThat(result.getTotalElements()).isEqualTo(25); // 전체 요소 수
        assertThat(result.getTotalPages()).isEqualTo(3); // 전체 페이지 수
        verify(globalStatRepository, times(1)).findGlobalSingle(eventName, pageable);
    }
}
