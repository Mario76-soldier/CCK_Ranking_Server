package com.cubingclub.ranking.PersonalRecord.Service;

import com.cubingclub.ranking.PersonalRecord.DTO.PersonalRecordDTO;
import com.cubingclub.ranking.PersonalRecord.Repository.PersonalRecordRepository;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.DTO.RecordDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonalRecordService {
    private final PersonalRecordRepository personalRecordRepository;

    public PersonalRecordDTO getPersonalRecord(String cckId) {
        List<Record> records = fetchRecords(cckId);
        List<RecordDTO> recordDTOs = convertToDTOs(records);
        Map<String, List<RecordDTO>> grouped = groupByEvent(recordDTOs);
        Map<String, RecordDTO> bestSingles = extractBestSingle(grouped);
        Map<String, RecordDTO> bestAverages = extractBestAverage(grouped);

        PersonalRecordDTO result = new PersonalRecordDTO();
        result.setRecordList(grouped);
        result.setBestSingle(bestSingles);
        result.setBestAverage(bestAverages);
        return result;
    }
    private List<RecordDTO> convertToDTOs(List<Record> records) {
        return records.stream()
                .map(RecordDTO::new)
                .collect(Collectors.toList());
    }
    private List<Record> fetchRecords(String cckId) {
        return personalRecordRepository.findByCCKId(cckId);
    }

    private Map<String, List<RecordDTO>> groupByEvent(List<RecordDTO> records) {
        return records.stream()
                .collect(Collectors.groupingBy(r -> r.getRound().getCubeEvent().getEventName()));
    }

    private Map<String, RecordDTO> extractBestSingle(Map<String, List<RecordDTO>> groupedRecords) {
        Map<String, RecordDTO> bestSingles = new HashMap<>();
        for (Map.Entry<String, List<RecordDTO>> entry : groupedRecords.entrySet()) {
            entry.getValue().stream()
                    .filter(r -> r.getSingle() != null)
                    .min(Comparator.comparing(RecordDTO::getSingle)).ifPresent(best -> bestSingles.put(entry.getKey(), best));
        }
        return bestSingles;
    }

    private Map<String, RecordDTO> extractBestAverage(Map<String, List<RecordDTO>> groupedRecords) {
        Map<String, RecordDTO> bestAverages = new HashMap<>();
        for (Map.Entry<String, List<RecordDTO>> entry : groupedRecords.entrySet()) {
            entry.getValue().stream()
                    .filter(r -> r.getAverage() != null)
                    .min(Comparator.comparing(RecordDTO::getAverage)).ifPresent(best -> bestAverages.put(entry.getKey(), best));
        }
        return bestAverages;
    }
}
