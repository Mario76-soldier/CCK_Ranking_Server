package com.cubingclub.ranking.Service;

import com.cubingclub.ranking.DTO.*;
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
        bestRecordListDTO.setAccount(new MaskedAccountDTO(accountRepository.findByEmail(email).orElseThrow()));
        List<EachBestRecordDTO> eachRecordList=new ArrayList<>();
        List<BestRecord> records= bestRecordRepository.findByEmailOrderByRound_CubeEvent_EventName(email);

        Map<String, Long> totalParticipantsMap = new HashMap<>();
        List<Object[]> totalParticipants = bestRecordRepository.findTotalParticipantsByEvent();
        for (Object[] row : totalParticipants) {
            totalParticipantsMap.put((String) row[0], (Long) row[1]);
        }

        // 지역별 참가자 수 (eventName + state -> 참가자 수)
        Map<String, Long> stateParticipantsMap = new HashMap<>();
        List<Object[]> stateParticipants = bestRecordRepository.findTotalParticipantsByEventAndState();
        for (Object[] row : stateParticipants) {
            String key = row[0] + ":" + row[1];  // eventName:state 형식으로 키 생성
            stateParticipantsMap.put(key, (Long) row[2]);
        }

        for(BestRecord record:records){
            EachBestRecordDTO dto = new EachBestRecordDTO(record);

            String eventName = record.getCubeEvent().getEventName();
            String state = record.getAccount().getState();

            // 전체 참가자 수 설정
            dto.setAllPart(totalParticipantsMap.getOrDefault(eventName, 0L));

            // 지역별 참가자 수 설정
            dto.setStateAllPart(stateParticipantsMap.getOrDefault(eventName + ":" + state, 0L));

            eachRecordList.add(dto);
        }
        bestRecordListDTO.setEachRecordList(eachRecordList);
        return bestRecordListDTO;
    }
    public BestRecordDTO getBestRecordByEvent(String email, String eventName){
        BestRecord bestRecord=bestRecordRepository.findByEmailAndEventName(email, eventName).orElse(null);
        if(bestRecord!=null){
            return new BestRecordDTO(bestRecord);
        }
        return null;
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
        Participate participate=participateRepository.findByIdx(partIdx);
        Optional<BestRecord> optionalRecord = bestRecordRepository.findByEmailAndEventName(email, eventName);
        if (single==null || single.compareTo(new BigDecimal(BigInteger.ZERO))==0 || average==null || average.compareTo(new BigDecimal(BigInteger.ZERO))==0 ) {
            return;
        }
        BestRecordDTO bestRecordDTO;
        if(optionalRecord.isEmpty()){
            bestRecordDTO=new BestRecordDTO(accountRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Account not found")), participate, participate, eventRepository.findByEventName(eventName));
            updatedSingle = true;
            updatedAverage = true;
        }
        else {
            bestRecordDTO = new BestRecordDTO(optionalRecord.get());
            if(bestRecordDTO.getSingle().equals(participate)){
                bestRecordDTO.setSingle(participate);
                updatedSingle = true;
            }
            if(bestRecordDTO.getAverage().equals(participate)){
                bestRecordDTO.setAverage(participate);
                updatedAverage = true;
            }
        }
        if (shouldUpdate(bestRecordDTO.getSingle().getSingle(), single)) {
            bestRecordDTO.setSingle(participate);
            updatedSingle = true;
        }

        if (shouldUpdate(bestRecordDTO.getAverage().getAverage(), average)) {
            bestRecordDTO.setAverage(participate);
            updatedAverage = true;
        }

        if (!updatedSingle && !updatedAverage) {
            return; // 업데이트가 필요하지 않으면 빠르게 종료
        }
        bestRecordRepository.save(bestRecordDTO.toEntity()); // 변경된 값 저장
        // 순위 갱신
        updateRanking(bestRecordDTO.getAccount().getState(), eventName, updatedSingle, updatedAverage);
    }

    public void updateRanking(String state, String eventName, boolean updatedSingle, boolean updatedAverage) {
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

    public void findPreviousBest(String email, String eventName){
        Participate single=participateRepository.findSingleBest(email, eventName).orElse(null);
        Participate average=participateRepository.findAverageBest(email, eventName).orElse(null);
        BestRecord bestRecord=bestRecordRepository.findByEmailAndEventName(email, eventName).orElseThrow();
        if(single!=null && average!=null) {
            BestRecordDTO bestRecordDTO = new BestRecordDTO(bestRecord);
            bestRecordDTO.setSingle(single);
            bestRecordDTO.setAverage(average);
            bestRecordRepository.save(bestRecordDTO.toEntity());
        }
        else{
            bestRecordRepository.delete(bestRecord);
        }
    }
}
