package com.cubingclub.ranking.Statistic;

import com.cubingclub.ranking.Record.DTO.AttemptDTO;
import com.cubingclub.ranking.Record.Repository.AttemptRepository;
import com.cubingclub.ranking.Statistic.DTO.StateRecordDTO;
import com.cubingclub.ranking.Statistic.DTO.StateRecordFlatDTO;
import com.cubingclub.ranking.Statistic.DTO.StateStatDTO;
import com.cubingclub.ranking.Statistic.Repository.StateStatRepository;
import com.cubingclub.ranking.Statistic.Service.StateStatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StateStat Service 테스트")
class StateStatServiceTest {

    @InjectMocks
    private StateStatService stateStatService;

    @Mock
    private AttemptRepository attemptRepository;

    @Mock
    private StateStatRepository stateStatRepository;

    @Test
    @DisplayName("지역별 상위 3개 Single 기록 조회 성공")
    void getStateSingleTop3Success() {
        // Given
        String eventName = "3x3x3";

        StateRecordFlatDTO flatDTO = mock(StateRecordFlatDTO.class);
        when(flatDTO.getRecordIdx()).thenReturn(1);
        when(flatDTO.getState()).thenReturn("서울");
        when(flatDTO.getName()).thenReturn("테스트 사용자");
        when(flatDTO.getCckId()).thenReturn("TEST001");
        when(flatDTO.getRoundName()).thenReturn("Final");
        when(flatDTO.getCompName()).thenReturn("테스트 대회");
        when(flatDTO.getEventName()).thenReturn(eventName);
        when(flatDTO.getSingle()).thenReturn(BigDecimal.valueOf(8.50));
        when(flatDTO.getAverage()).thenReturn(BigDecimal.valueOf(10.20));

        AttemptDTO attemptDTO = new AttemptDTO();
        attemptDTO.setRecordIdx(1);
        attemptDTO.setAttemptTry(1);
        attemptDTO.setTime(BigDecimal.valueOf(8.50));

        when(stateStatRepository.findStateSingleTop3Flat(eventName))
                .thenReturn(List.of(flatDTO));
        when(attemptRepository.findAttemptDTOByRecordIdxList(List.of(1)))
                .thenReturn(List.of(attemptDTO));

        // When
        Map<String, List<StateRecordDTO>> result = stateStatService.getStateSingleTop3(eventName);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).containsKey("서울");
        assertThat(result.get("서울")).hasSize(1);
        assertThat(result.get("서울").get(0).getName()).isEqualTo("테스트 사용자");
        assertThat(result.get("서울").get(0).getSingle()).isEqualTo(BigDecimal.valueOf(8.50));
        verify(stateStatRepository, times(1)).findStateSingleTop3Flat(eventName);
        verify(attemptRepository, times(1)).findAttemptDTOByRecordIdxList(List.of(1));
    }

    @Test
    @DisplayName("지역별 상위 3개 Average 기록 조회 성공")
    void getStateAverageTop3Success() {
        // Given
        String eventName = "3x3x3";

        StateRecordFlatDTO flatDTO = mock(StateRecordFlatDTO.class);
        when(flatDTO.getRecordIdx()).thenReturn(1);
        when(flatDTO.getState()).thenReturn("경기");
        when(flatDTO.getName()).thenReturn("테스트 사용자2");
        when(flatDTO.getCckId()).thenReturn("TEST002");
        when(flatDTO.getRoundName()).thenReturn("Final");
        when(flatDTO.getCompName()).thenReturn("테스트 대회");
        when(flatDTO.getEventName()).thenReturn(eventName);
        when(flatDTO.getSingle()).thenReturn(BigDecimal.valueOf(9.00));
        when(flatDTO.getAverage()).thenReturn(BigDecimal.valueOf(11.00));

        AttemptDTO attemptDTO = new AttemptDTO();
        attemptDTO.setRecordIdx(1);
        attemptDTO.setAttemptTry(1);
        attemptDTO.setTime(BigDecimal.valueOf(11.00));

        when(stateStatRepository.findStateAverageTop3Flat(eventName))
                .thenReturn(List.of(flatDTO));
        when(attemptRepository.findAttemptDTOByRecordIdxList(List.of(1)))
                .thenReturn(List.of(attemptDTO));

        // When
        Map<String, List<StateRecordDTO>> result = stateStatService.getStateAverageTop3(eventName);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).containsKey("경기");
        assertThat(result.get("경기")).hasSize(1);
        assertThat(result.get("경기").get(0).getName()).isEqualTo("테스트 사용자2");
        assertThat(result.get("경기").get(0).getAverage()).isEqualTo(BigDecimal.valueOf(11.00));
        verify(stateStatRepository, times(1)).findStateAverageTop3Flat(eventName);
        verify(attemptRepository, times(1)).findAttemptDTOByRecordIdxList(List.of(1));
    }

    @Test
    @DisplayName("지역별 통계 조회 성공")
    void getStateStatSuccess() {
        // Given
        String eventName = "3x3x3";
        StateStatDTO stateStatDTO = mock(StateStatDTO.class);
        when(stateStatDTO.getState()).thenReturn("서울");
        when(stateStatDTO.getBestSingle()).thenReturn(BigDecimal.valueOf(8.50));
        when(stateStatDTO.getBestAverage()).thenReturn(BigDecimal.valueOf(10.20));

        when(stateStatRepository.findStateStat(eventName))
                .thenReturn(List.of(stateStatDTO));

        // When
        List<StateStatDTO> result = stateStatService.getStateStat(eventName);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getState()).isEqualTo("서울");
        assertThat(result.get(0).getBestSingle()).isEqualTo(BigDecimal.valueOf(8.50));
        assertThat(result.get(0).getBestAverage()).isEqualTo(BigDecimal.valueOf(10.20));
        verify(stateStatRepository, times(1)).findStateStat(eventName);
    }

    @Test
    @DisplayName("지역별 기록 조회 - 빈 결과")
    void getStateSingleTop3EmptyResult() {
        // Given
        String eventName = "Megaminx";
        when(stateStatRepository.findStateSingleTop3Flat(eventName))
                .thenReturn(List.of());

        // When
        Map<String, List<StateRecordDTO>> result = stateStatService.getStateSingleTop3(eventName);

        // Then
        assertThat(result).isEmpty();
        verify(stateStatRepository, times(1)).findStateSingleTop3Flat(eventName);
        verify(attemptRepository, never()).findAttemptDTOByRecordIdxList(anyList());
    }

    @Test
    @DisplayName("지역별 기록 조회 - 여러 지역")
    void getStateSingleTop3MultipleStates() {
        // Given
        String eventName = "3x3x3";

        StateRecordFlatDTO flatDTO1 = mock(StateRecordFlatDTO.class);
        when(flatDTO1.getRecordIdx()).thenReturn(1);
        when(flatDTO1.getState()).thenReturn("서울");
        when(flatDTO1.getName()).thenReturn("사용자1");
        when(flatDTO1.getCckId()).thenReturn("TEST001");
        when(flatDTO1.getRoundName()).thenReturn("Final");
        when(flatDTO1.getCompName()).thenReturn("대회1");
        when(flatDTO1.getEventName()).thenReturn(eventName);
        when(flatDTO1.getSingle()).thenReturn(BigDecimal.valueOf(8.50));
        when(flatDTO1.getAverage()).thenReturn(BigDecimal.valueOf(10.20));

        StateRecordFlatDTO flatDTO2 = mock(StateRecordFlatDTO.class);
        when(flatDTO2.getRecordIdx()).thenReturn(2);
        when(flatDTO2.getState()).thenReturn("경기");
        when(flatDTO2.getName()).thenReturn("사용자2");
        when(flatDTO2.getCckId()).thenReturn("TEST002");
        when(flatDTO2.getRoundName()).thenReturn("Final");
        when(flatDTO2.getCompName()).thenReturn("대회1");
        when(flatDTO2.getEventName()).thenReturn(eventName);
        when(flatDTO2.getSingle()).thenReturn(BigDecimal.valueOf(9.00));
        when(flatDTO2.getAverage()).thenReturn(BigDecimal.valueOf(11.00));

        when(stateStatRepository.findStateSingleTop3Flat(eventName))
                .thenReturn(List.of(flatDTO1, flatDTO2));
        when(attemptRepository.findAttemptDTOByRecordIdxList(List.of(1, 2)))
                .thenReturn(List.of());

        // When
        Map<String, List<StateRecordDTO>> result = stateStatService.getStateSingleTop3(eventName);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).containsKeys("서울", "경기");
        assertThat(result.get("서울")).hasSize(1);
        assertThat(result.get("경기")).hasSize(1);
        verify(stateStatRepository, times(1)).findStateSingleTop3Flat(eventName);
    }
}
