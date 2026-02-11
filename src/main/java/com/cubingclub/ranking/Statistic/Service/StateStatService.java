package com.cubingclub.ranking.Statistic.Service;

import com.cubingclub.ranking.Record.DTO.AttemptDTO;
import com.cubingclub.ranking.Statistic.DTO.StateRecordDTO;
import com.cubingclub.ranking.Statistic.DTO.StateRecordFlatDTO;
import com.cubingclub.ranking.Statistic.DTO.StateStatDTO;
import com.cubingclub.ranking.Record.Repository.AttemptRepository;
import com.cubingclub.ranking.Statistic.Repository.StateStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StateStatService {
    private final AttemptRepository attemptRepository;
    private final StateStatRepository stateStatRepository;

    @Cacheable(value = "stateStatsSingle", key = "#eventName")
    public Map<String, List<StateRecordDTO>> getStateSingleTop3(String eventName) {
        return getStateTop3Internal(() ->
                stateStatRepository.findStateSingleTop3Flat(eventName)
        );
    }

    @Cacheable(value = "stateStatsAverage", key = "#eventName")
    public Map<String, List<StateRecordDTO>> getStateAverageTop3(String eventName) {
        return getStateTop3Internal(() ->
                stateStatRepository.findStateAverageTop3Flat(eventName)
        );
    }

    // ✅ 공통 로직 분리
    private Map<String, List<StateRecordDTO>> getStateTop3Internal(
            Supplier<List<StateRecordFlatDTO>> supplier) {

        List<StateRecordFlatDTO> flats = supplier.get();

        if (flats.isEmpty()) {
            return Map.of();
        }

        List<Integer> recordIds = flats.stream()
                .map(StateRecordFlatDTO::getRecordIdx)
                .toList();

        List<AttemptDTO> attempts =
                attemptRepository.findAttemptDTOByRecordIdxList(recordIds);

        Map<Integer, List<AttemptDTO>> attemptMap =
                attempts.stream()
                        .collect(Collectors.groupingBy(AttemptDTO::getRecordIdx));

        List<StateRecordDTO> merged = flats.stream().map(f -> {
            StateRecordDTO dto = new StateRecordDTO();
            dto.setRecordIdx(f.getRecordIdx());
            dto.setState(f.getState());
            dto.setName(f.getName());
            dto.setCckId(f.getCckId());
            dto.setRoundName(f.getRoundName());
            dto.setCompName(f.getCompName());
            dto.setEventName(f.getEventName());
            dto.setSingle(f.getSingle());
            dto.setAverage(f.getAverage());
            dto.setAttempts(
                    attemptMap.getOrDefault(f.getRecordIdx(), List.of())
            );
            return dto;
        }).toList();

        return merged.stream().collect(
                Collectors.groupingBy(
                        StateRecordDTO::getState,
                        LinkedHashMap::new,
                        Collectors.toList()
                )
        );
    }

    @Cacheable(value = "stateStat", key = "#eventName")
    public List<StateStatDTO> getStateStat(String eventName){
        return stateStatRepository.findStateStat(eventName);
    }
}
