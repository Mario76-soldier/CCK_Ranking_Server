package com.cubingclub.ranking.Scrambler;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import com.cubingclub.ranking.Scrambler.Component.PuzzleConfigResolver;
import com.cubingclub.ranking.Scrambler.DTO.ScrambleResponseDTO;
import com.cubingclub.ranking.Scrambler.DTO.ScramblerUpdateDTO;
import com.cubingclub.ranking.Scrambler.Repository.ScramblerRepository;
import com.cubingclub.ranking.Scrambler.Service.ScrambleCreationService;
import com.cubingclub.ranking.Scrambler.Service.ScrambleQueryService;
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
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Scrambler Service 테스트")
class ScramblerServiceTest {

    @InjectMocks
    private ScramblerService scramblerService;

    @Mock
    private RoundRepository roundRepository;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private ScramblerRepository scramblerRepository;

    @Mock
    private PuzzleConfigResolver puzzleConfigResolver;

    @Mock
    private ScrambleCreationService scrambleCreationService;

    @Mock
    private ScrambleQueryService scrambleQueryService;

    private Round testRound;

    @BeforeEach
    void setUp() {
        Competition testCompetition = Competition.builder()
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

        CubeEvent testCubeEvent = CubeEvent.builder()
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
    @DisplayName("스크램블 저장 성공")
    void saveScrambleSuccess() {
        // Given
        List<String> groups = List.of("A", "B", "C");
        String puzzleId = "333";
        int count = 5;
        Map<String, String> colorScheme = Map.of("U", "white", "D", "yellow");

        when(roundRepository.findByIdx(1)).thenReturn(Optional.of(testRound));
        when(puzzleConfigResolver.resolvePuzzleId(testRound)).thenReturn(puzzleId);
        when(puzzleConfigResolver.resolveScrambleCount(testRound)).thenReturn(count);
        when(puzzleConfigResolver.resolveColorScheme(testRound)).thenReturn(colorScheme);
        when(scrambleCreationService.scrambleExistsForGroup(1, "A")).thenReturn(false);
        when(scrambleCreationService.scrambleExistsForGroup(1, "B")).thenReturn(false);
        when(scrambleCreationService.scrambleExistsForGroup(1, "C")).thenReturn(false);

        // When
        scramblerService.saveScramble(1, groups);

        // Then
        verify(scramblerRepository, times(1)).deleteByRound_Idx(1);
        verify(roundRepository, times(1)).findByIdx(1);
        verify(puzzleConfigResolver, times(1)).resolvePuzzleId(testRound);
        verify(puzzleConfigResolver, times(1)).resolveScrambleCount(testRound);
        verify(puzzleConfigResolver, times(1)).resolveColorScheme(testRound);
        verify(scrambleCreationService, times(3)).generateNormalScrambles(
                eq(testRound), anyString(), eq(count), eq(puzzleId), eq(colorScheme));
        verify(scrambleCreationService, times(3)).generateExtraScrambles(
                eq(testRound), anyString(), eq(puzzleId), eq(colorScheme));
    }

    @Test
    @DisplayName("스크램블 저장 - 이미 존재하는 그룹은 스킵")
    void saveScrambleWithExistingGroup() {
        // Given
        List<String> groups = List.of("A", "B");
        String puzzleId = "333";
        int count = 5;
        Map<String, String> colorScheme = Map.of("U", "white", "D", "yellow");

        when(roundRepository.findByIdx(1)).thenReturn(Optional.of(testRound));
        when(puzzleConfigResolver.resolvePuzzleId(testRound)).thenReturn(puzzleId);
        when(puzzleConfigResolver.resolveScrambleCount(testRound)).thenReturn(count);
        when(puzzleConfigResolver.resolveColorScheme(testRound)).thenReturn(colorScheme);
        when(scrambleCreationService.scrambleExistsForGroup(1, "A")).thenReturn(true); // 이미 존재
        when(scrambleCreationService.scrambleExistsForGroup(1, "B")).thenReturn(false);

        // When
        scramblerService.saveScramble(1, groups);

        // Then
        // A 그룹은 스킵되어 B 그룹만 생성
        verify(scrambleCreationService, times(1)).generateNormalScrambles(
                eq(testRound), eq("B"), eq(count), eq(puzzleId), eq(colorScheme));
        verify(scrambleCreationService, times(1)).generateExtraScrambles(
                eq(testRound), eq("B"), eq(puzzleId), eq(colorScheme));
    }

    @Test
    @DisplayName("플레이어 스크램블 조회 성공")
    void getScrambleSuccess() {
        // Given
        ScramblerUpdateDTO updateDTO = new ScramblerUpdateDTO();
        updateDTO.setCompIdx(1);
        updateDTO.setPlayerUid("player-uid");
        updateDTO.setScramblerUid("scrambler-uid");
        String token = "test-token";

        ScrambleResponseDTO expectedResponse = new ScrambleResponseDTO();
        expectedResponse.setRoundIdx(1);
        expectedResponse.setRoundName("Round 1");
        expectedResponse.setEventName("3x3x3");
        expectedResponse.setRoundGroup("A");

        when(scrambleQueryService.getScrambleForPlayer(1, "player-uid", "scrambler-uid", token))
                .thenReturn(expectedResponse);

        // When
        ScrambleResponseDTO result = scramblerService.getScramble(updateDTO, token);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getRoundIdx()).isEqualTo(1);
        assertThat(result.getRoundName()).isEqualTo("Round 1");
        assertThat(result.getEventName()).isEqualTo("3x3x3");
        assertThat(result.getRoundGroup()).isEqualTo("A");
        verify(scrambleQueryService, times(1)).getScrambleForPlayer(1, "player-uid", "scrambler-uid", token);
    }

    @Test
    @DisplayName("빈 그룹 리스트로 스크램블 저장")
    void saveScrambleWithEmptyGroups() {
        // Given
        List<String> groups = List.of();
        String puzzleId = "333";
        int count = 5;
        Map<String, String> colorScheme = Map.of("U", "white", "D", "yellow");

        when(roundRepository.findByIdx(1)).thenReturn(Optional.of(testRound));
        when(puzzleConfigResolver.resolvePuzzleId(testRound)).thenReturn(puzzleId);
        when(puzzleConfigResolver.resolveScrambleCount(testRound)).thenReturn(count);
        when(puzzleConfigResolver.resolveColorScheme(testRound)).thenReturn(colorScheme);

        // When
        scramblerService.saveScramble(1, groups);

        // Then
        verify(scramblerRepository, times(1)).deleteByRound_Idx(1);
        verify(scrambleCreationService, never()).generateNormalScrambles(
                any(), anyString(), anyInt(), anyString(), any());
        verify(scrambleCreationService, never()).generateExtraScrambles(
                any(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("다양한 퍼즐 타입으로 스크램블 저장")
    void saveScrambleWithDifferentPuzzleTypes() {
        // Given
        List<String> groups = List.of("A");
        String puzzleId = "222"; // 2x2x2
        int count = 5;
        Map<String, String> colorScheme = Map.of("U", "white", "D", "yellow");

        when(roundRepository.findByIdx(1)).thenReturn(Optional.of(testRound));
        when(puzzleConfigResolver.resolvePuzzleId(testRound)).thenReturn(puzzleId);
        when(puzzleConfigResolver.resolveScrambleCount(testRound)).thenReturn(count);
        when(puzzleConfigResolver.resolveColorScheme(testRound)).thenReturn(colorScheme);
        when(scrambleCreationService.scrambleExistsForGroup(1, "A")).thenReturn(false);

        // When
        scramblerService.saveScramble(1, groups);

        // Then
        verify(scrambleCreationService, times(1)).generateNormalScrambles(
                eq(testRound), eq("A"), eq(count), eq(puzzleId), eq(colorScheme));
    }
}
