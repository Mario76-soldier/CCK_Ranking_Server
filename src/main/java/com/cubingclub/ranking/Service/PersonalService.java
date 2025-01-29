package com.cubingclub.ranking.Service;

import com.cubingclub.ranking.DTO.PersonalRecordDTO;
import com.cubingclub.ranking.DTO.RecordDTO;
import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Entity.Record;
import com.cubingclub.ranking.Repository.AccountRepository;
import com.cubingclub.ranking.Repository.ParticipateRepository;
import com.cubingclub.ranking.Repository.RecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalService {
    private final AccountRepository accountRepository;
    private final ParticipateRepository participateRepository;
    private final RecordRepository recordRepository;
    public Map<String, List<PersonalRecordDTO>> getPersonalRecord(String email){
        List<Participate> participates=participateRepository.findByEmailOrderByRound_CubeEvent_EventName(email);
        Map<Integer, Long> roundParticipantCounts = participateRepository.findAllRoundParticipantCounts();
        return participates.stream()
                .map(PersonalRecordDTO::new)
                .peek(personalRecordDTO ->
                        personalRecordDTO.setRankingAll(
                                Math.toIntExact(roundParticipantCounts.get(personalRecordDTO.getRound().getIdx()))
                        )
                )
                .collect(Collectors.groupingBy(personalRecordDTO -> personalRecordDTO.getRound().getCubeEvent().getEventName()));
    }
    public Map<String, List<RecordDTO>> getBestRecord(String email){
        return recordRepository.findByEmailOrderByRound_CubeEvent_EventName(email).stream()
                .map(RecordDTO::new)
                .collect(Collectors.groupingBy(recordDTO -> recordDTO.getCubeEvent().getEventName()));
    }
    @Transactional
    public void updateBest(String email, String eventName, BigDecimal single, BigDecimal average) {
        Optional<Record> optionalRecord = recordRepository.findByEmailAndEventName(email, eventName);
        if (optionalRecord.isEmpty()) {
            return;
        }

        RecordDTO recordDTO = new RecordDTO(optionalRecord.get());
        String state = accountRepository.findByEmail(email).get().getState();

        boolean updatedSingle = false;
        boolean updatedAverage=false;

        if (recordDTO.getSingle().compareTo(single) > 0) {
            recordDTO.setSingle(single);
            updatedSingle = true;
        }

        if (recordDTO.getAverage().compareTo(average) > 0) {
            recordDTO.setAverage(average);
            updatedAverage = true;
        }

        if (!updatedSingle && !updatedAverage) {
            return; // 업데이트가 필요하지 않으면 빠르게 종료
        }

        recordRepository.save(recordDTO.toEntity()); // 변경된 값 저장

        // 순위 갱신
        updateRanking(eventName, state, updatedSingle, updatedAverage);
    }

    private void updateRanking(String eventName, String state, boolean updatedSingle, boolean updatedAverage) {
        if(updatedSingle){
            List<Record> singleRecords = recordRepository.findByEventNameOrderBySingle(eventName);
            List<Record> stateSingleRecords = recordRepository.findByEventNameAndStateOrderBySingle(eventName, state);
            updateRankings(singleRecords, true);
            updateRankings(stateSingleRecords, true);
        }
        if(updatedAverage) {
            List<Record> averageRecords = recordRepository.findByEventNameOrderByAverage(eventName);
            List<Record> stateAverageRecords = recordRepository.findByEventNameAndStateOrderByAverage(eventName, state);
            updateRankings(averageRecords, false);
            updateRankings(stateAverageRecords, false);
        }
    }

    private void updateRankings(List<Record> records, boolean isSingle) {
        List<Record> updatedRecords = new ArrayList<>();
        long ranking = 1L;

        for (Record record : records) {
            RecordDTO recordDTO = new RecordDTO(record);
            if (isSingle) {
                recordDTO.setSingleRanking(ranking++);
            } else {
                recordDTO.setAverageRanking(ranking++);
            }
            updatedRecords.add(recordDTO.toEntity());
        }

        recordRepository.saveAll(updatedRecords); // 한 번에 저장
    }

    /*
    public void updateBest(String email, String eventName, BigDecimal single, BigDecimal average){
        if(recordRepository.findByEmailAndEventName(email, eventName).isPresent()) {
            RecordDTO record = new RecordDTO(recordRepository.findByEmailAndEventName(email, eventName).get());
            String state=accountRepository.findByEmail(email).get().getState();
            if(record.getSingle().compareTo(single)>0){
                record.setSingle(single);
                long ranking=1L;
                List<Record> records=recordRepository.findByEventNameOrderBySingle(eventName);
                for(Record record1:records){
                    RecordDTO recordDTO=new RecordDTO(record1);
                    recordDTO.setSingleRanking(ranking++);
                    recordRepository.save(recordDTO.toEntity());
                }
                ranking=1L;
                records=recordRepository.findByEventNameAndStateOrderBySingle(eventName, state);
                for(Record record1:records){
                    RecordDTO recordDTO=new RecordDTO(record1);
                    recordDTO.setSingleRanking(ranking++);
                    recordRepository.save(recordDTO.toEntity());
                }
            }
            if(record.getAverage().compareTo(average)>0){
                record.setAverage(average);
                long ranking=1L;
                List<Record> records=recordRepository.findByEventNameOrderByAverage(eventName);
                for(Record record1:records){
                    RecordDTO recordDTO=new RecordDTO(record1);
                    recordDTO.setSingleRanking(ranking++);
                    recordRepository.save(recordDTO.toEntity());
                }
                ranking=1L;
                records=recordRepository.findByEventNameAndStateOrderByAverage(eventName, state);
                for(Record record1:records){
                    RecordDTO recordDTO=new RecordDTO(record1);
                    recordDTO.setSingleRanking(ranking++);
                    recordRepository.save(recordDTO.toEntity());
                }
            }
        }
    }
    */
}
