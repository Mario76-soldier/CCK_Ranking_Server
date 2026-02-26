package com.cubingclub.ranking.Statistic.Service;

import com.cubingclub.ranking.Record.DTO.RecordDTO;
import com.cubingclub.ranking.Statistic.DTO.CompRecordStatDTO;
import com.cubingclub.ranking.Statistic.Repository.GlobalStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GlobalStatService {
    private final GlobalStatRepository globalStatRepository;

    @Cacheable(value = "globalStatsSingle", key = "#eventName + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<RecordDTO> getGlobalSingleByEvent(String eventName, Pageable pageable) {
        log.debug("Fetching global top 10 single records for event: {}", eventName);
        return globalStatRepository.findGlobalSingle(eventName, pageable).map(RecordDTO::new);
    }

    @Cacheable(value = "globalStatsAverage", key = "#eventName + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<RecordDTO> getGlobalAverageByEvent(String eventName, Pageable pageable) {
        log.debug("Fetching global top 10 average records for event: {}", eventName);
        return globalStatRepository.findGlobalAverage(eventName, pageable).map(RecordDTO::new);
    }

    public List<CompRecordStatDTO> getGlobalRecordStatByCompetition(String eventName) {
        log.debug("Fetching global record stats by competition");
        return globalStatRepository.findGlobalRecordStatByCompetition(eventName);
    }
}

