package com.cubingclub.ranking.Statistic.Service;

import com.cubingclub.ranking.Record.DTO.RecordDTO;
import com.cubingclub.ranking.Statistic.DTO.CompRecordStatDTO;
import com.cubingclub.ranking.Statistic.Repository.GlobalStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GlobalStatService {
    private final GlobalStatRepository globalStatRepository;

    @Cacheable(value = "globalStatsSingle", key = "#eventName")
    public List<RecordDTO> getGlobalTop10BySingleByEvent(String eventName) {
        log.debug("Fetching global top 10 single records for event: {}", eventName);
        return globalStatRepository.findGlobalSingleTop10(eventName).stream().map(RecordDTO::new).toList();
    }

    @Cacheable(value = "globalStatsAverage", key = "#eventName")
    public List<RecordDTO> getGlobalTop10ByAverageByEvent(String eventName) {
        log.debug("Fetching global top 10 average records for event: {}", eventName);
        return globalStatRepository.findGlobalAverageTop10(eventName).stream().map(RecordDTO::new).toList();
    }

    public List<CompRecordStatDTO> getGlobalRecordStatByCompetition(String eventName) {
        log.debug("Fetching global record stats by competition");
        return globalStatRepository.findGlobalRecordStatByCompetition(eventName);
    }
}

