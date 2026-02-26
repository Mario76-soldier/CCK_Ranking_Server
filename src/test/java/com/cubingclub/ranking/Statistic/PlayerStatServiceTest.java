package com.cubingclub.ranking.Statistic;

import com.cubingclub.ranking.Record.DTO.RecordDTO;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Statistic.DTO.PlayerCountStatDTO;
import com.cubingclub.ranking.Statistic.DTO.PlayerRankingDTO;
import com.cubingclub.ranking.Statistic.DTO.StaffCountStatDTO;
import com.cubingclub.ranking.Statistic.Repository.PlayerRankingRepository;
import com.cubingclub.ranking.Statistic.Repository.PlayerStatRepository;
import com.cubingclub.ranking.Statistic.Service.PlayerStatService;
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
@DisplayName("PlayerStat Service 테스트")
class PlayerStatServiceTest {

    @InjectMocks
    private PlayerStatService playerStatService;

    @Mock
    private PlayerStatRepository playerStatRepository;

    @Mock
    private PlayerRankingRepository playerRankingRepository;

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
    @DisplayName("플레이어 랭킹 조회 성공")
    void getPlayerRankingByCckIdSuccess() {
        // Given
        String cckId = "TEST001";
        PlayerRankingDTO rankingDTO = mock(PlayerRankingDTO.class);
        when(rankingDTO.getEventName()).thenReturn("3x3x3");
        when(rankingDTO.getRankSingle()).thenReturn(5L);
        when(rankingDTO.getRankAverage()).thenReturn(7L);

        when(playerRankingRepository.findPlayerRankingByCckId(cckId))
                .thenReturn(List.of(rankingDTO));

        // When
        List<PlayerRankingDTO> result = playerStatService.getPlayerRankingByCckId(cckId);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventName()).isEqualTo("3x3x3");
        assertThat(result.get(0).getRankSingle()).isEqualTo(5L);
        assertThat(result.get(0).getRankAverage()).isEqualTo(7L);
        verify(playerRankingRepository, times(1)).findPlayerRankingByCckId(cckId);
    }

    @Test
    @DisplayName("플레이어 이벤트별 히스토리 조회 성공")
    void getPlayerHistoriesByCckIdAndEventNameSuccess() {
        // Given
        String cckId = "TEST001";
        String eventName = "3x3x3";

        when(playerStatRepository.findHistoryByCckIdAndEventName(cckId, eventName))
                .thenReturn(List.of(testRecord));

        // When
        List<RecordDTO> result = playerStatService.getPlayerHistoriesByCckIdAndEventName(cckId, eventName);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCckId()).isEqualTo("TEST001");
        assertThat(result.get(0).getSingle()).isEqualTo(BigDecimal.valueOf(8.50));
        verify(playerStatRepository, times(1)).findHistoryByCckIdAndEventName(cckId, eventName);
    }

    @Test
    @DisplayName("플레이어 기록 갱신 목록 조회 성공")
    void getBreakRecordsByCckIdSuccess() {
        // Given
        String cckId = "TEST001";

        when(playerStatRepository.findBreakRecordByCckId(cckId))
                .thenReturn(List.of(testRecord));

        // When
        List<RecordDTO> result = playerStatService.getBreakRecordsByCckId(cckId);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCckId()).isEqualTo("TEST001");
        verify(playerStatRepository, times(1)).findBreakRecordByCckId(cckId);
    }

    @Test
    @DisplayName("플레이어 참가 통계 조회 성공")
    void getPlayerCountByCckIdSuccess() {
        // Given
        String cckId = "TEST001";
        PlayerCountStatDTO countDTO = new PlayerCountStatDTO(10L, 8L);

        when(playerStatRepository.findPlayerCountByCckId(cckId))
                .thenReturn(countDTO);

        // When
        PlayerCountStatDTO result = playerStatService.getPlayerCountByCckId(cckId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCompetitionCount()).isEqualTo(10L);
        assertThat(result.getCompleteCount()).isEqualTo(8L);
        verify(playerStatRepository, times(1)).findPlayerCountByCckId(cckId);
    }

    @Test
    @DisplayName("플레이어 스태프 활동 통계 조회 성공")
    void getStaffCountByCckIdSuccess() {
        // Given
        String cckId = "TEST001";
        StaffCountStatDTO staffDTO = new StaffCountStatDTO(15L, 12L);

        when(playerStatRepository.findStaffCountByCckId(cckId))
                .thenReturn(staffDTO);

        // When
        StaffCountStatDTO result = playerStatService.getStaffCountByCckId(cckId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getJudgeCount()).isEqualTo(15L);
        assertThat(result.getScramblerCount()).isEqualTo(12L);
        verify(playerStatRepository, times(1)).findStaffCountByCckId(cckId);
    }

    @Test
    @DisplayName("플레이어 랭킹 조회 - 빈 결과")
    void getPlayerRankingEmptyResult() {
        // Given
        String cckId = "NEWBIE";
        when(playerRankingRepository.findPlayerRankingByCckId(cckId))
                .thenReturn(List.of());

        // When
        List<PlayerRankingDTO> result = playerStatService.getPlayerRankingByCckId(cckId);

        // Then
        assertThat(result).isEmpty();
        verify(playerRankingRepository, times(1)).findPlayerRankingByCckId(cckId);
    }
}
