package com.cubingclub.ranking.Round;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Round.DTO.RoundCreateDTO;
import com.cubingclub.ranking.Round.DTO.RoundDTO;
import com.cubingclub.ranking.Round.DTO.RoundUpdateDTO;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import com.cubingclub.ranking.Round.Service.RoundService;
import com.cubingclub.ranking.Scrambler.Service.ScramblerService;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Round Service 테스트")
class RoundServiceTest {

    @InjectMocks
    private RoundService roundService;

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CubeEventRepository cubeEventRepository;

    @Mock
    private ScramblerService scramblerService;

    private Round testRound;
    private Competition testCompetition;
    private CubeEvent testCubeEvent;

    @BeforeEach
    void setUp() {
        testCompetition = Competition.builder()
                .Idx(1)
                .CompName("테스트 대회")
                .CompDateStart(LocalDate.now().plusDays(10))
                .CompDateEnd(LocalDate.now().plusDays(11))
                .JoinDateStart(LocalDateTime.now())
                .JoinDateEnd(LocalDateTime.now().plusDays(5))
                .baseFee(10000)
                .location("서울")
                .MaxParticipate(100)
                .build();

        testCubeEvent = CubeEvent.builder()
                .eventName("3x3x3")
                .avgCalc("avg5")
                .colorType(ColorType.cube)
                .build();

        testRound = Round.builder()
                .idx(1)
                .comp(testCompetition)
                .cubeEvent(testCubeEvent)
                .roundName("Round 1")
                .eventStart(LocalDateTime.now().plusDays(10))
                .eventEnd(LocalDateTime.now().plusDays(10).plusHours(2))
                .cutoff(BigDecimal.valueOf(120.00))
                .timeLimit(BigDecimal.valueOf(600.00))
                .advance(75)
                .build();
    }

    @Test
    @DisplayName("라운드 생성 성공")
    void createRoundSuccess() {
        // Given
        RoundCreateDTO createDTO = new RoundCreateDTO();
        createDTO.setComp(1);
        createDTO.setCubeEvent("3x3x3");
        createDTO.setRoundName("Round 1");
        createDTO.setEventStart(LocalDateTime.now().plusDays(10));
        createDTO.setEventEnd(LocalDateTime.now().plusDays(10).plusHours(2));
        createDTO.setCutoff(BigDecimal.valueOf(120.00));
        createDTO.setTimeLimit(BigDecimal.valueOf(600.00));
        createDTO.setAdvance(75);
        createDTO.setGroupList(List.of("A", "B", "C"));

        when(competitionRepository.findById(1)).thenReturn(Optional.of(testCompetition));
        when(cubeEventRepository.findById("3x3x3")).thenReturn(Optional.of(testCubeEvent));
        when(roundRepository.save(any(Round.class))).thenReturn(testRound);
        doNothing().when(scramblerService).saveScramble(anyInt(), anyList());

        // When
        roundService.createRoundList(List.of(createDTO));

        // Then
        verify(roundRepository, times(1)).save(any(Round.class));
        verify(scramblerService, times(1)).saveScramble(anyInt(), anyList());
    }

    @Test
    @DisplayName("라운드 생성 - 존재하지 않는 대회")
    void createRoundWithNonExistentCompetition() {
        // Given
        RoundCreateDTO createDTO = new RoundCreateDTO();
        createDTO.setComp(999);
        createDTO.setCubeEvent("3x3x3");
        createDTO.setRoundName("Round 1");

        when(competitionRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roundService.createRoundList(List.of(createDTO)))
                .isInstanceOf(CustomizedException.class);
    }

    @Test
    @DisplayName("라운드 수정 성공")
    void updateRoundSuccess() {
        // Given
        RoundUpdateDTO updateDTO = new RoundUpdateDTO();
        updateDTO.setIdx(1);
        updateDTO.setCubeEvent("3x3x3");
        updateDTO.setRoundName("Updated Round");
        updateDTO.setEventStart(LocalDateTime.now().plusDays(11));
        updateDTO.setEventEnd(LocalDateTime.now().plusDays(11).plusHours(2));
        updateDTO.setCutoff(BigDecimal.valueOf(100.00));
        updateDTO.setTimeLimit(BigDecimal.valueOf(500.00));
        updateDTO.setAdvance(50);
        updateDTO.setGroupList(List.of("A", "B"));

        when(roundRepository.findById(1)).thenReturn(Optional.of(testRound));
        when(cubeEventRepository.findById("3x3x3")).thenReturn(Optional.of(testCubeEvent));

        // When
        roundService.updateRoundList(List.of(updateDTO));

        // Then
        verify(roundRepository, times(1)).findById(1);
        verify(cubeEventRepository, times(1)).findById("3x3x3");
    }

    @Test
    @DisplayName("라운드 수정 - 존재하지 않는 라운드")
    void updateRoundNotFound() {
        // Given
        RoundUpdateDTO updateDTO = new RoundUpdateDTO();
        updateDTO.setIdx(999);
        updateDTO.setCubeEvent("3x3x3");
        updateDTO.setRoundName("Updated Round");

        when(roundRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roundService.updateRoundList(List.of(updateDTO)))
                .isInstanceOf(CustomizedException.class);
    }

    @Test
    @DisplayName("단일 라운드 조회 성공")
    void getOneRoundSuccess() {
        // Given
        when(roundRepository.findByIdx(1)).thenReturn(Optional.of(testRound));

        // When
        RoundDTO result = roundService.getOne(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoundName()).isEqualTo("Round 1");
        verify(roundRepository, times(1)).findByIdx(1);
    }

    @Test
    @DisplayName("단일 라운드 조회 - 존재하지 않는 라운드")
    void getOneRoundNotFound() {
        // Given
        when(roundRepository.findByIdx(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roundService.getOne(999))
                .isInstanceOf(CustomizedException.class);
    }

    @Test
    @DisplayName("라운드 삭제 성공")
    void deleteRoundSuccess() {
        // Given
        Integer roundIdx = 1;
        doNothing().when(roundRepository).deleteById(roundIdx);

        // When
        roundService.deleteByRoundIdx(roundIdx);

        // Then
        verify(roundRepository, times(1)).deleteById(roundIdx);
    }

    @Test
    @DisplayName("어드밴스 인원 수정 성공")
    void updateAdvanceSuccess() {
        // Given
        when(roundRepository.findById(1)).thenReturn(Optional.of(testRound));

        // When
        roundService.updateAdvance(1, 50);

        // Then
        verify(roundRepository, times(1)).findById(1);
        assertThat(testRound.getAdvance()).isEqualTo(50);
    }

    @Test
    @DisplayName("어드밴스 인원 수정 - 존재하지 않는 라운드")
    void updateAdvanceNotFound() {
        // Given
        when(roundRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> roundService.updateAdvance(999, 50))
                .isInstanceOf(CustomizedException.class);
    }
}
