package com.cubingclub.ranking.Service;

import com.cubingclub.ranking.DTO.PersonalRecordDTO;
import com.cubingclub.ranking.DTO.RecordDTO;
import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Entity.Record;
import com.cubingclub.ranking.Repository.AccountRepository;
import com.cubingclub.ranking.Repository.EventRepository;
import com.cubingclub.ranking.Repository.ParticipateRepository;
import com.cubingclub.ranking.Repository.RecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalService {
    private final AccountRepository accountRepository;
    private final ParticipateRepository participateRepository;
    private final RecordRepository recordRepository;
    private final EventRepository eventRepository;
    public Map<String, List<PersonalRecordDTO>> getPersonalRecord(String email){
        List<Participate> participates=participateRepository.findByEmailOrderByRound_CubeEvent_EventName(email);
        List<Object[]> roundParticipantCountsList = participateRepository.findAllRoundParticipantCounts();

        // Object[] → Map<Integer, Long> 변환
        Map<Integer, Long> roundParticipantCounts = roundParticipantCountsList.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0], // r.idx
                        row -> ((Number) row[1]).longValue() // COUNT(*)
                ));
        return participates.stream()
                .map(PersonalRecordDTO::new)
                .peek(personalRecordDTO -> {
                            personalRecordDTO.setRankingAll(Math.toIntExact(roundParticipantCounts.get(personalRecordDTO.getRound().getIdx())));
                        }
                )
                .collect(Collectors.groupingBy(personalRecordDTO -> personalRecordDTO.getRound().getCubeEvent().getEventName()));
    }
    public Map<String, List<RecordDTO>> getBestRecord(String email){
        return recordRepository.findByEmailOrderByRound_CubeEvent_EventName(email).stream()
                .map(RecordDTO::new)
                .collect(Collectors.groupingBy(recordDTO -> recordDTO.getCubeEvent().getEventName()));
    }
    private boolean shouldUpdate(BigDecimal current, BigDecimal newValue) {
        if(current.compareTo(BigDecimal.ZERO) < 0){
            // current가 -2이면, 가장 큰 값 선택
            if(current.compareTo(BigDecimal.valueOf(-2))==0){
                return newValue.compareTo(BigDecimal.valueOf(-2))==0 || newValue.compareTo(BigDecimal.valueOf(-1))==0 || newValue.compareTo(BigDecimal.ZERO) > 0;
            }
            else if(current.compareTo(BigDecimal.valueOf(-1))==0){
                return newValue.compareTo(BigDecimal.valueOf(-1))==0 || newValue.compareTo(BigDecimal.ZERO) > 0;
            }
            else return false;
        }
        else if(current.compareTo(BigDecimal.ZERO) > 0){
            return newValue.compareTo(BigDecimal.valueOf(-2))!=0 && newValue.compareTo(BigDecimal.valueOf(-1))!=0 && current.compareTo(newValue) >= 0;
        }
        else return false;
    }

    @Transactional
    public void updateBest(Integer partIdx, String email, String userName, String enUserName, String eventName, BigDecimal single, BigDecimal average) {
        Optional<Record> optionalRecord = recordRepository.findByEmailAndEventName(email, eventName);
        RecordDTO recordDTO;
        if (single==null || single.compareTo(new BigDecimal(BigInteger.ZERO))==0 || average==null || average.compareTo(new BigDecimal(BigInteger.ZERO))==0 ) {
            return;
        }
        if(optionalRecord.isEmpty()){
            recordDTO=new RecordDTO();
            recordDTO.setPartIdx(partIdx);
            recordDTO.setUserName(userName);
            recordDTO.setEnUserName(enUserName);
            recordDTO.setEmail(email);
            recordDTO.setCubeEvent(eventRepository.findByEventName(eventName));
            recordDTO.setSingle(single);
            recordDTO.setAverage(average);
        }
        else {
            recordDTO = new RecordDTO(optionalRecord.get());
            if(recordDTO.getPartIdx().equals(partIdx)){
                recordDTO.setSingle(single);
                recordDTO.setAverage(average);
            }
        }

        boolean updatedSingle = false;
        boolean updatedAverage = false;

        if (shouldUpdate(recordDTO.getSingle(), single)) {
            recordDTO.setSingle(single);
            updatedSingle = true;
        }

        if (shouldUpdate(recordDTO.getAverage(), average)) {
            recordDTO.setAverage(average);
            updatedAverage = true;
        }

        if (!updatedSingle && !updatedAverage) {
            return; // 업데이트가 필요하지 않으면 빠르게 종료
        }
        recordDTO.setPartIdx(partIdx);
        recordRepository.save(recordDTO.toEntity()); // 변경된 값 저장
        // 순위 갱신
        updateRanking(eventName, updatedSingle, updatedAverage);
    }

    private void updateRanking(String eventName, boolean updatedSingle, boolean updatedAverage) {
        if(updatedSingle){
            List<Record> singleRecords = recordRepository.findByEventNameOrderBySingle(eventName);
            //List<Record> stateSingleRecords = recordRepository.findByEventNameAndStateOrderBySingle(eventName, state);
            updateRankings(singleRecords, true);
            //updateRankings(stateSingleRecords, true);
        }
        if(updatedAverage) {
            List<Record> averageRecords = recordRepository.findByEventNameOrderByAverage(eventName);
            //List<Record> stateAverageRecords = recordRepository.findByEventNameAndStateOrderByAverage(eventName, state);
            updateRankings(averageRecords, false);
            //updateRankings(stateAverageRecords, false);
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
