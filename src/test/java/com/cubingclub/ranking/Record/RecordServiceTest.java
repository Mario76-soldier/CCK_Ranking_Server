package com.cubingclub.ranking.Record;

import com.cubingclub.ranking.Auth.DTO.AccountDTO;
import com.cubingclub.ranking.Auth.DTO.PlayerInfo;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Record.DTO.ExtraDTO;
import com.cubingclub.ranking.Record.DTO.RecordDTO;
import com.cubingclub.ranking.Record.DTO.RecordUpdateDTO;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Repository.AttemptRepository;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import com.cubingclub.ranking.Record.Service.*;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.Competition.Entity.Competition;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Record Service 테스트")
class RecordServiceTest {

    @InjectMocks
    private RecordService recordService;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private AuthService authService;

    @Mock
    private RecordCalculator recordCalculator;

    @Mock
    private BestTypeService bestTypeService;

    @Mock
    private CutoffProcessor cutoffProcessor;

    private Record testRecord;
    private Round testRound;
    private CubeEvent testCubeEvent;
    private Competition testCompetition;
    private List<Attempt> testAttempts;

    @BeforeEach
    void setUp() {
        testCompetition = Competition.builder()
                .Idx(1)
                .CompName("테스트 대회")
                .CompDateStart(LocalDate.now())
                .CompDateEnd(LocalDate.now().plusDays(1))
                .JoinDateStart(LocalDateTime.now())
                .JoinDateEnd(LocalDateTime.now().plusDays(5))
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
                .single(BigDecimal.valueOf(10.00))
                .average(BigDecimal.valueOf(12.00))
                .build();

        testAttempts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Attempt attempt = Attempt.builder()
                    .idx(i)
                    .record(testRecord)
                    .attemptTry(i)
                    .time(BigDecimal.valueOf(10.00 + i))
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
                .single(BigDecimal.valueOf(10.00))
                .average(BigDecimal.valueOf(12.00))
                .attempts(testAttempts)
                .build();
    }

    @Test
    @DisplayName("라운드별 기록 목록 조회 성공")
    void getRecordListSuccess() {
        // Given
        when(recordRepository.findByRound_Idx(1)).thenReturn(List.of(testRecord));

        // When
        List<RecordDTO> result = recordService.getRecordList(1);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCckId()).isEqualTo("TEST001");
        verify(recordRepository, times(1)).findByRound_Idx(1);
    }

    @Test
    @DisplayName("대회별 기록 갱신 목록 조회 성공")
    void getBreakRecordListSuccess() {
        // Given
        when(recordRepository.findRecordsWithBestTypeByCompIdx(1)).thenReturn(List.of(testRecord));

        // When
        List<RecordDTO> result = recordService.getBreakRecordList(1);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        verify(recordRepository, times(1)).findRecordsWithBestTypeByCompIdx(1);
    }

    @Test
    @DisplayName("기록 업데이트 성공")
    void updateRecordSuccess() {
        // Given
        RecordUpdateDTO updateDTO = new RecordUpdateDTO();
        updateDTO.setIdx(1);
        updateDTO.setTimes(List.of(
                BigDecimal.valueOf(11.00),
                BigDecimal.valueOf(12.00),
                BigDecimal.valueOf(13.00),
                BigDecimal.valueOf(14.00),
                BigDecimal.valueOf(15.00)
        ));

        when(recordRepository.findByIdx(1)).thenReturn(testRecord);
        doNothing().when(cutoffProcessor).applyRules(any(), any(), any(), any(), any());
        when(recordCalculator.calculateSingle(any())).thenReturn(BigDecimal.valueOf(11.00));
        when(recordCalculator.calculateAoXAverage(any())).thenReturn(BigDecimal.valueOf(13.00));
        doNothing().when(recordRepository).setRankOnRound(anyInt());

        // When
        recordService.updateRecord(updateDTO);

        // Then
        verify(recordRepository, times(1)).findByIdx(1);
        verify(cutoffProcessor, times(1)).applyRules(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("엑스트라 시도 추가 성공")
    void addExtraSuccess() {
        // Given
        ExtraDTO extraDTO = new ExtraDTO();
        extraDTO.setRecordIdx(1);
        extraDTO.setExtraTry(1);
        extraDTO.setExtraNum(1);
        extraDTO.setExtraReason("잘못된 스크램블");

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setName("심판");
        accountDTO.setCckId("JUDGE01");

        String authHeader = "Bearer test-token";
        
        when(recordRepository.findByIdx(1)).thenReturn(testRecord);
        when(authService.getMyInfo("test-token")).thenReturn(accountDTO);

        // When
        recordService.addExtra(extraDTO, authHeader);

        // Then
        verify(recordRepository, times(1)).findByIdx(1);
        verify(authService, times(1)).getMyInfo("test-token");
        assertThat(testAttempts.get(0).getExtra()).isEqualTo(1);
    }

    @Test
    @DisplayName("엑스트라 시도 삭제 성공")
    void deleteExtraSuccess() {
        // Given
        Attempt attempt = testAttempts.get(0);
        attempt.updateExtra(1, "테스트", "심판", "JUDGE01");
        
        when(attemptRepository.findByRecord_IdxAndExtra(1, 1)).thenReturn(attempt);

        // When
        recordService.deleteExtra(1, 1);

        // Then
        verify(attemptRepository, times(1)).findByRecord_IdxAndExtra(1, 1);
        assertThat(attempt.getExtra()).isNull();
        assertThat(attempt.getExReason()).isNull();
    }

    @Test
    @DisplayName("엑스트라 시도 삭제 - 존재하지 않는 시도")
    void deleteExtraNotFound() {
        // Given
        when(attemptRepository.findByRecord_IdxAndExtra(999, 1)).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> recordService.deleteExtra(999, 1))
                .isInstanceOf(CustomizedException.class);
    }

    @Test
    @DisplayName("스크램블러 정보 업데이트 성공")
    void updateScramblerSuccess() {
        // Given
        PlayerInfo scramblerInfo = new PlayerInfo();
        scramblerInfo.setName("스크램블러");
        scramblerInfo.setCckId("SCRAMBLER01");

        Attempt attempt = testAttempts.get(0);
        when(attemptRepository.findByIdx(1)).thenReturn(attempt);

        // When
        recordService.updateScrambler(1, scramblerInfo);

        // Then
        verify(attemptRepository, times(1)).findByIdx(1);
        assertThat(attempt.getScramblerName()).isEqualTo("스크램블러");
        assertThat(attempt.getScramblerCckId()).isEqualTo("SCRAMBLER01");
    }

    @Test
    @DisplayName("스크램블러 업데이트 - 본인이 스크램블러인 경우 실패")
    void updateScramblerConflict() {
        // Given
        PlayerInfo scramblerInfo = new PlayerInfo();
        scramblerInfo.setName("테스트 사용자");
        scramblerInfo.setCckId("TEST001"); // 본인

        Attempt attempt = testAttempts.get(0);
        when(attemptRepository.findByIdx(1)).thenReturn(attempt);

        // When & Then
        assertThatThrownBy(() -> recordService.updateScrambler(1, scramblerInfo))
                .isInstanceOf(CustomizedException.class);
    }

    @Test
    @DisplayName("기록 삭제 성공")
    void deleteRecordSuccess() {
        // Given
        when(recordRepository.findByIdx(1)).thenReturn(testRecord);
        doNothing().when(recordRepository).deleteById(1);
        doNothing().when(recordRepository).setRankOnRound(1);
        when(cacheManager.getCache(any())).thenReturn(null);

        // When
        recordService.deleteRecord(1);

        // Then
        verify(recordRepository, times(1)).findByIdx(1);
        verify(recordRepository, times(1)).deleteById(1);
        verify(recordRepository, times(1)).setRankOnRound(1);
    }
}
