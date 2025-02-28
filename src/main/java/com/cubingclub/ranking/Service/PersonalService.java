package com.cubingclub.ranking.Service;

import com.cubingclub.ranking.DTO.BestRecordDTO;
import com.cubingclub.ranking.DTO.BestRecordListDTO;
import com.cubingclub.ranking.DTO.EachBestRecordDTO;
import com.cubingclub.ranking.DTO.PersonalRecordDTO;
import com.cubingclub.ranking.Entity.BestRecord;
import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Repository.AccountRepository;
import com.cubingclub.ranking.Repository.BestRecordRepository;
import com.cubingclub.ranking.Repository.EventRepository;
import com.cubingclub.ranking.Repository.ParticipateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PersonalService {
    private final AccountRepository accountRepository;
    private final ParticipateRepository participateRepository;
    private final BestRecordRepository bestRecordRepository;
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
    public BestRecordListDTO getBestRecord(String email){
        BestRecordListDTO bestRecordListDTO =new BestRecordListDTO();
        bestRecordListDTO.setAccount(accountRepository.findByEmail(email).get());
        List<EachBestRecordDTO> eachRecordList=new ArrayList<>();
        List<BestRecord> records= bestRecordRepository.findByEmailOrderByRound_CubeEvent_EventName(email);
        for(BestRecord record:records){
            eachRecordList.add(new EachBestRecordDTO(record));
        }
        bestRecordListDTO.setEachRecordList(eachRecordList);
        return bestRecordListDTO;
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
    public void updateBest(Integer partIdx, String email, String eventName, BigDecimal single, BigDecimal average) {
        boolean updatedSingle = false;
        boolean updatedAverage = false;
        Optional<BestRecord> optionalRecord = bestRecordRepository.findByEmailAndEventName(email, eventName);
        if (single==null || single.compareTo(new BigDecimal(BigInteger.ZERO))==0 || average==null || average.compareTo(new BigDecimal(BigInteger.ZERO))==0 ) {
            return;
        }
        BestRecordDTO bestRecordDTO;
        if(optionalRecord.isEmpty()){
            bestRecordDTO=new BestRecordDTO(accountRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Account not found")), partIdx, partIdx, eventRepository.findByEventName(eventName), single, average);
            updatedSingle = true;
            updatedAverage = true;
        }
        else {
            bestRecordDTO = new BestRecordDTO(optionalRecord.get());
            if(bestRecordDTO.getPartSingle().equals(partIdx)){
                bestRecordDTO.setSingle(single);
                updatedSingle = true;
            }
            if(bestRecordDTO.getPartAverage().equals(partIdx)){
                bestRecordDTO.setAverage(average);
                updatedAverage = true;
            }
        }
        if (shouldUpdate(bestRecordDTO.getSingle(), single)) {
            bestRecordDTO.setSingle(single);
            bestRecordDTO.setPartSingle(partIdx);
            updatedSingle = true;
        }

        if (shouldUpdate(bestRecordDTO.getAverage(), average)) {
            bestRecordDTO.setAverage(average);
            bestRecordDTO.setPartAverage(partIdx);
            updatedAverage = true;
        }

        if (!updatedSingle && !updatedAverage) {
            return; // 업데이트가 필요하지 않으면 빠르게 종료
        }
        log.info(bestRecordRepository.save(bestRecordDTO.toEntity()).getAccount().getEmail()); // 변경된 값 저장
        // 순위 갱신
        updateRanking(bestRecordDTO.getAccount().getState(), eventName, updatedSingle, updatedAverage);
    }

    private void updateRanking(String state, String eventName, boolean updatedSingle, boolean updatedAverage) {
        if(updatedSingle){
            List<BestRecord> singleRecords = bestRecordRepository.findByEventNameOrderBySingle(eventName);
            List<BestRecord> stateSingleRecords = bestRecordRepository.findByEventNameAndStateOrderBySingle(eventName, state);
            updateRankings(singleRecords, true, false);
            updateRankings(stateSingleRecords, true, true);
        }
        if(updatedAverage) {
            List<BestRecord> averageRecords = bestRecordRepository.findByEventNameOrderByAverage(eventName);
            List<BestRecord> stateAverageRecords = bestRecordRepository.findByEventNameAndStateOrderByAverage(eventName, state);
            updateRankings(averageRecords, false, false);
            updateRankings(stateAverageRecords, false, true);
        }
    }

    private void updateRankings(List<BestRecord> records, boolean isSingle, boolean isState) {
        List<BestRecord> updatedRecords = new ArrayList<>();
        long ranking = 1L;
        for (BestRecord record : records) {
            BestRecordDTO bestRecordDTO = new BestRecordDTO(record);
            if(!isState) {
                if (isSingle) {
                    bestRecordDTO.setSingleRanking(ranking++);
                } else {
                    bestRecordDTO.setAverageRanking(ranking++);
                }
            }
            else{
                if (isSingle) {
                    bestRecordDTO.setStateSingleRanking(ranking++);
                } else {
                    bestRecordDTO.setStateAverageRanking(ranking++);
                }
            }
            updatedRecords.add(bestRecordDTO.toEntity());
        }

        bestRecordRepository.saveAll(updatedRecords); // 한 번에 저장
    }
}
