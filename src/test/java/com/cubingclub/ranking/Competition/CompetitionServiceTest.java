package com.cubingclub.ranking.Competition;

import com.cubingclub.ranking.Competition.DTO.*;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Competition.Entity.EventFee;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.Competition.Service.CompetitionService;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
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
@DisplayName("Competition Service 테스트")
class CompetitionServiceTest {

    @InjectMocks
    private CompetitionService competitionService;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CubeEventRepository cubeEventRepository;

    private Competition testCompetition;
    private CubeEvent testCubeEvent;

    @BeforeEach
    void setUp() {
        testCubeEvent = CubeEvent.builder()
                .eventName("3x3x3")
                .avgCalc("avg5")
                .colorType(ColorType.cube)
                .build();

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
    }

    @Test
    @DisplayName("대회 생성 성공")
    void createCompetitionSuccess() {
        // Given
        CompetitionCreateDTO createDTO = new CompetitionCreateDTO();
        createDTO.setCompName("새 대회");
        createDTO.setCompDateStart(LocalDate.now().plusDays(10));
        createDTO.setCompDateEnd(LocalDate.now().plusDays(11));
        createDTO.setJoinDateStart(LocalDateTime.now());
        createDTO.setJoinDateEnd(LocalDateTime.now().plusDays(5));
        createDTO.setBaseFee(10000);
        createDTO.setLocation("서울");
        createDTO.setMaxParticipate(100);

        when(competitionRepository.save(any(Competition.class))).thenReturn(testCompetition);

        // When
        competitionService.create(createDTO);

        // Then
        verify(competitionRepository, times(1)).save(any(Competition.class));
    }

    @Test
    @DisplayName("대회 생성 - EventFee 포함")
    void createCompetitionWithEventFee() {
        // Given
        EventFeeDTO feeDTO = new EventFeeDTO();
        feeDTO.setEventName("3x3x3");
        feeDTO.setEventFee(5000);

        CompetitionCreateDTO createDTO = new CompetitionCreateDTO();
        createDTO.setCompName("새 대회");
        createDTO.setCompDateStart(LocalDate.now().plusDays(10));
        createDTO.setCompDateEnd(LocalDate.now().plusDays(11));
        createDTO.setJoinDateStart(LocalDateTime.now());
        createDTO.setJoinDateEnd(LocalDateTime.now().plusDays(5));
        createDTO.setBaseFee(10000);
        createDTO.setLocation("서울");
        createDTO.setMaxParticipate(100);
        createDTO.setEventFeeList(List.of(feeDTO));

        when(cubeEventRepository.findById("3x3x3")).thenReturn(Optional.of(testCubeEvent));
        when(competitionRepository.save(any(Competition.class))).thenReturn(testCompetition);

        // When
        competitionService.create(createDTO);

        // Then
        verify(cubeEventRepository, times(1)).findById("3x3x3");
        verify(competitionRepository, times(1)).save(any(Competition.class));
    }

    @Test
    @DisplayName("대회 수정 성공")
    void updateCompetitionSuccess() {
        // Given
        CompetitionUpdateDTO updateDTO = new CompetitionUpdateDTO();
        updateDTO.setIdx(1);
        updateDTO.setCompName("수정된 대회");
        updateDTO.setCompDateStart(LocalDate.now().plusDays(10));
        updateDTO.setCompDateEnd(LocalDate.now().plusDays(11));
        updateDTO.setJoinDateStart(LocalDateTime.now());
        updateDTO.setJoinDateEnd(LocalDateTime.now().plusDays(5));
        updateDTO.setBaseFee(15000);
        updateDTO.setLocation("부산");
        updateDTO.setMaxParticipate(150);

        when(competitionRepository.findById(1)).thenReturn(Optional.of(testCompetition));

        // When
        competitionService.update(updateDTO);

        // Then
        verify(competitionRepository, times(1)).findById(1);
        assertThat(testCompetition.getCompName()).isEqualTo("수정된 대회");
        assertThat(testCompetition.getBaseFee()).isEqualTo(15000);
        assertThat(testCompetition.getLocation()).isEqualTo("부산");
    }

    @Test
    @DisplayName("대회 수정 - 존재하지 않는 대회")
    void updateCompetitionNotFound() {
        // Given
        CompetitionUpdateDTO updateDTO = new CompetitionUpdateDTO();
        updateDTO.setIdx(999);
        updateDTO.setCompName("수정된 대회");

        when(competitionRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> competitionService.update(updateDTO))
                .isInstanceOf(CustomizedException.class);
    }

    @Test
    @DisplayName("단일 대회 조회 성공")
    void getOneCompetitionSuccess() {
        // Given
        when(competitionRepository.findById(1)).thenReturn(Optional.of(testCompetition));

        // When
        CompetitionDTO result = competitionService.getOne(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCompName()).isEqualTo("테스트 대회");
        verify(competitionRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("단일 대회 조회 - 존재하지 않는 대회")
    void getOneCompetitionNotFound() {
        // Given
        when(competitionRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> competitionService.getOne(999))
                .isInstanceOf(CustomizedException.class);
    }

    @Test
    @DisplayName("지난 대회 목록 조회")
    void getCompPastSuccess() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Competition pastComp = Competition.builder()
                .Idx(2)
                .CompName("지난 대회")
                .CompDateStart(LocalDate.now().minusDays(10))
                .CompDateEnd(LocalDate.now().minusDays(9))
                .JoinDateStart(LocalDateTime.now().minusDays(20))
                .JoinDateEnd(LocalDateTime.now().minusDays(15))
                .baseFee(10000)
                .location("서울")
                .MaxParticipate(100)
                .build();

        Page<Competition> page = new PageImpl<>(List.of(pastComp));
        when(competitionRepository.findByCompDateEndBeforeOrderByCompDateStartDesc(any(Pageable.class), any(LocalDate.class)))
                .thenReturn(page);

        // When
        Page<CompetitionDTO> result = competitionService.getCompPast(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCompName()).isEqualTo("지난 대회");
    }

    @Test
    @DisplayName("진행 중인 대회 목록 조회")
    void getCompNowSuccess() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Competition nowComp = Competition.builder()
                .Idx(3)
                .CompName("진행 중 대회")
                .CompDateStart(LocalDate.now().minusDays(1))
                .CompDateEnd(LocalDate.now().plusDays(1))
                .JoinDateStart(LocalDateTime.now().minusDays(10))
                .JoinDateEnd(LocalDateTime.now().minusDays(5))
                .baseFee(10000)
                .location("서울")
                .MaxParticipate(100)
                .build();

        Page<Competition> page = new PageImpl<>(List.of(nowComp));
        when(competitionRepository.findByCompDateStartLessThanEqualAndCompDateEndGreaterThanEqualOrderByCompDateStartDesc(
                any(Pageable.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(page);

        // When
        Page<CompetitionDTO> result = competitionService.getCompNow(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCompName()).isEqualTo("진행 중 대회");
    }

    @Test
    @DisplayName("예정된 대회 목록 조회")
    void getCompFutureSuccess() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Competition> page = new PageImpl<>(List.of(testCompetition));
        when(competitionRepository.findByCompDateStartAfterOrderByCompDateStartAsc(any(Pageable.class), any(LocalDate.class)))
                .thenReturn(page);

        // When
        Page<CompetitionDTO> result = competitionService.getCompFuture(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCompName()).isEqualTo("테스트 대회");
    }

    @Test
    @DisplayName("대회 삭제 성공")
    void deleteCompetitionSuccess() {
        // Given
        Integer compIdx = 1;
        doNothing().when(competitionRepository).deleteById(compIdx);

        // When
        competitionService.deleteByIdx(compIdx);

        // Then
        verify(competitionRepository, times(1)).deleteById(compIdx);
    }
}
