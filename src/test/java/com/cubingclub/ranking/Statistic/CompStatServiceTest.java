package com.cubingclub.ranking.Statistic;

import com.cubingclub.ranking.Record.DTO.AttemptDTO;
import com.cubingclub.ranking.Statistic.DTO.DNFStatDTO;
import com.cubingclub.ranking.Statistic.DTO.StaffStatDTO;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Statistic.Repository.CompStatRepository;
import com.cubingclub.ranking.Statistic.Service.CompStatService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompStat Service 테스트")
class CompStatServiceTest {

    @InjectMocks
    private CompStatService compStatService;

    @Mock
    private CompStatRepository compStatRepository;

    private Record testRecord;
    private Round testRound;
    private Competition testCompetition;
    private CubeEvent testCubeEvent;

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
    }

    @Test
    @DisplayName("대회별 엑스트라 시도 조회 성공")
    void getExtraByCompIdxSuccess() {
        // Given
        Integer compIdx = 1;
        Attempt attempt = Attempt.builder()
                .idx(1)
                .record(testRecord)
                .attemptTry(3)
                .extra(1)
                .exReason("잘못된 스크램블")
                .time(BigDecimal.valueOf(12.50))
                .build();

        when(compStatRepository.findExtraByCompIdx(compIdx))
                .thenReturn(List.of(attempt));

        // When
        List<AttemptDTO> result = compStatService.getExtraByCompIdx(compIdx);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getExtra()).isEqualTo(1);
        assertThat(result.get(0).getExReason()).isEqualTo("잘못된 스크램블");
        verify(compStatRepository, times(1)).findExtraByCompIdx(compIdx);
    }

    @Test
    @DisplayName("대회별 심판 통계 조회 성공")
    void getJudgeStatByCompIdxSuccess() {
        // Given
        Integer compIdx = 1;
        StaffStatDTO judgeStatDTO = mock(StaffStatDTO.class);
        when(judgeStatDTO.getStaffName()).thenReturn("심판A");
        when(judgeStatDTO.getStaffCckId()).thenReturn("JUDGE01");
        when(judgeStatDTO.getCount()).thenReturn(25L);

        when(compStatRepository.findJudgeStatByCompIdx(compIdx))
                .thenReturn(List.of(judgeStatDTO));

        // When
        List<StaffStatDTO> result = compStatService.getJudgeStatByCompIdx(compIdx);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStaffName()).isEqualTo("심판A");
        assertThat(result.get(0).getStaffCckId()).isEqualTo("JUDGE01");
        assertThat(result.get(0).getCount()).isEqualTo(25L);
        verify(compStatRepository, times(1)).findJudgeStatByCompIdx(compIdx);
    }

    @Test
    @DisplayName("대회별 스크램블러 통계 조회 성공")
    void getScramblerStatByCompIdxSuccess() {
        // Given
        Integer compIdx = 1;
        StaffStatDTO scramblerStatDTO = mock(StaffStatDTO.class);
        when(scramblerStatDTO.getStaffName()).thenReturn("스크램블러A");
        when(scramblerStatDTO.getStaffCckId()).thenReturn("SCRAMBLER01");
        when(scramblerStatDTO.getCount()).thenReturn(30L);

        when(compStatRepository.findScramblerStatByCompIdx(compIdx))
                .thenReturn(List.of(scramblerStatDTO));

        // When
        List<StaffStatDTO> result = compStatService.getScramblerStatByCompIdx(compIdx);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStaffName()).isEqualTo("스크램블러A");
        assertThat(result.get(0).getStaffCckId()).isEqualTo("SCRAMBLER01");
        assertThat(result.get(0).getCount()).isEqualTo(30L);
        verify(compStatRepository, times(1)).findScramblerStatByCompIdx(compIdx);
    }

    @Test
    @DisplayName("대회별 DNF 통계 조회 성공")
    void getDNFStatByCompIdxSuccess() {
        // Given
        Integer compIdx = 1;
        DNFStatDTO dnfStatDTO = new DNFStatDTO(5L, 95L, 100L);

        when(compStatRepository.findDNFStatByCompIdx(compIdx))
                .thenReturn(dnfStatDTO);

        // When
        DNFStatDTO result = compStatService.getDNFStatByCompIdx(compIdx);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(100L);
        assertThat(result.getDnfCount()).isEqualTo(5L);
        assertThat(result.getSuccessCount()).isEqualTo(95L);
        verify(compStatRepository, times(1)).findDNFStatByCompIdx(compIdx);
    }

    @Test
    @DisplayName("대회별 엑스트라 조회 - 빈 결과")
    void getExtraByCompIdxEmptyResult() {
        // Given
        Integer compIdx = 999;
        when(compStatRepository.findExtraByCompIdx(compIdx))
                .thenReturn(List.of());

        // When
        List<AttemptDTO> result = compStatService.getExtraByCompIdx(compIdx);

        // Then
        assertThat(result).isEmpty();
        verify(compStatRepository, times(1)).findExtraByCompIdx(compIdx);
    }

    @Test
    @DisplayName("대회별 심판 통계 조회 - 빈 결과")
    void getJudgeStatByCompIdxEmptyResult() {
        // Given
        Integer compIdx = 999;
        when(compStatRepository.findJudgeStatByCompIdx(compIdx))
                .thenReturn(List.of());

        // When
        List<StaffStatDTO> result = compStatService.getJudgeStatByCompIdx(compIdx);

        // Then
        assertThat(result).isEmpty();
        verify(compStatRepository, times(1)).findJudgeStatByCompIdx(compIdx);
    }
}
