package com.cubingclub.ranking.CubeEvent;

import com.cubingclub.ranking.CubeEvent.DTO.CubeEventDTO;
import com.cubingclub.ranking.CubeEvent.Entity.ColorType;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.CubeEvent.Service.CubeEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CubeEvent Service 테스트")
class CubeEventServiceTest {

    @InjectMocks
    private CubeEventService cubeEventService;

    @Mock
    private CubeEventRepository cubeEventRepository;

    @Mock
    private MultipartFile mockFile;

    private CubeEvent testCubeEvent;

    @BeforeEach
    void setUp() {
        testCubeEvent = CubeEvent.builder()
                .eventName("3x3x3")
                .avgCalc("avg5")
                .colorType(ColorType.cube)
                .build();

        // Set the path field using reflection
        ReflectionTestUtils.setField(cubeEventService, "path", "/tmp/test-images");
    }

    @Test
    @DisplayName("모든 큐브 이벤트 조회 성공")
    void getCubeEventSuccess() {
        // Given
        CubeEvent event1 = CubeEvent.builder()
                .eventName("3x3x3")
                .avgCalc("avg5")
                .colorType(ColorType.cube)
                .build();

        CubeEvent event2 = CubeEvent.builder()
                .eventName("2x2x2")
                .avgCalc("avg5")
                .colorType(ColorType.cube)
                .build();

        when(cubeEventRepository.findAll()).thenReturn(List.of(event1, event2));

        // When
        List<CubeEventDTO> result = cubeEventService.getCubeEvent();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEventName()).isEqualTo("3x3x3");
        assertThat(result.get(1).getEventName()).isEqualTo("2x2x2");
        verify(cubeEventRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("큐브 이벤트 생성 성공 - 파일 없음")
    void createCubeEventWithoutFile() {
        // Given
        when(cubeEventRepository.save(any(CubeEvent.class))).thenReturn(testCubeEvent);

        // When
        cubeEventService.createCubeEvent("3x3x3", "avg5", ColorType.cube, null);

        // Then
        verify(cubeEventRepository, times(1)).save(any(CubeEvent.class));
    }

    @Test
    @DisplayName("큐브 이벤트 생성 성공 - 파일 포함")
    void createCubeEventWithFile() throws IOException {
        // Given
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getInputStream()).thenReturn(mock(InputStream.class));
        when(cubeEventRepository.save(any(CubeEvent.class))).thenReturn(testCubeEvent);

        // When
        cubeEventService.createCubeEvent("3x3x3", "avg5", ColorType.cube, mockFile);

        // Then
        verify(cubeEventRepository, times(1)).save(any(CubeEvent.class));
    }

    @Test
    @DisplayName("빈 이벤트 목록 조회")
    void getEmptyCubeEventList() {
        // Given
        when(cubeEventRepository.findAll()).thenReturn(List.of());

        // When
        List<CubeEventDTO> result = cubeEventService.getCubeEvent();

        // Then
        assertThat(result).isEmpty();
        verify(cubeEventRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("큐브 이벤트 생성 - 다양한 ColorType")
    void createCubeEventWithDifferentColorTypes() {
        // Given
        when(cubeEventRepository.save(any(CubeEvent.class))).thenReturn(testCubeEvent);

        // When
        cubeEventService.createCubeEvent("Pyraminx", "avg5", ColorType.tetra, null);

        // Then
        verify(cubeEventRepository, times(1)).save(any(CubeEvent.class));
    }

    @Test
    @DisplayName("큐브 이벤트 생성 - 다양한 평균 계산 방식")
    void createCubeEventWithDifferentAvgCalc() {
        // Given
        when(cubeEventRepository.save(any(CubeEvent.class))).thenReturn(testCubeEvent);

        // When
        cubeEventService.createCubeEvent("3x3x3 Blindfolded", "best", ColorType.cube, null);

        // Then
        verify(cubeEventRepository, times(1)).save(any(CubeEvent.class));
    }
}
