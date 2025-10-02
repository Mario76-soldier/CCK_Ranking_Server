package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Account.DTO.MaskedAccountDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.BestType;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Repository.AttemptRepository;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import com.cubingclub.ranking.RFID.Repository.RFIDRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RecordService {
    private final RoundRepository roundRepository;
    private final RecordRepository recordRepository;
    private final AttemptRepository attemptRepository;
    private final AccountRepository accountRepository;
    private final EntityManager em;

    private static final BigDecimal DNF = BigDecimal.valueOf(-1);
    private static final BigDecimal DNS = BigDecimal.valueOf(-2);

    public AttemptDTO getAttempt(Integer attemptIdx){
        return new AttemptDTO(attemptRepository.findByIdx(attemptIdx));
    }

    public List<RecordDTO> getParticipate(Integer roundIdx) {
        List<Record> list=recordRepository.findByRound_Idx(roundIdx);
        List<RecordDTO> recordDTOList=new ArrayList<>();
        for(Record record:list){
            recordDTOList.add(new RecordDTO(record));
        }
        return recordDTOList;
    }
    public RecordDTO getParticipateOne(Integer idx) {
        return new RecordDTO(recordRepository.findByIdx(idx));
    }

    public RecorderResponseDTO getForRecorder(String email, Integer compIdx) {
        if(accountRepository.findByEmail(email).isPresent()) {
            List<Record> list = recordRepository.findByAccount_EmailAndRound_Comp_IdxOrderByRound_EventStart(email, compIdx);
            for (Record p : list) {
                String name = p.getAccount().getEnName();
                String roundName = p.getRound().getCubeEvent().getEventName() + " " + p.getRound().getRoundName();
                String state=p.getAccount().getState();
                List<Attempt> attemptList=p.getAttempts();

                for(Attempt attempt: attemptList){
                    if(attempt.getTime()==null || attempt.getTime().compareTo(BigDecimal.ZERO)==0){
                        return new RecorderResponseDTO(attempt.getIdx(), attempt.getAttemptTry(), name, state, roundName, attempt.getExtra());
                    }
                }
            }
        }
        return null;
    }
    public void updateRecord(ParticipateRecordDTO p) {
        Record record=recordRepository.findByIdx(p.getIdx());
        List<Attempt> attemptList=record.getAttempts();
        for(int i=0; i<attemptList.size(); i++){
            AttemptDTO attemptDTO=new AttemptDTO(attemptList.get(i));
            attemptDTO.setTime(p.getTimes().get(i));
            attemptRepository.save(attemptDTO.toEntity(recordRepository, accountRepository));
        }
        record=recordRepository.findByIdx(p.getIdx());
        applyRecordUpdate(record);
    }

    public void updateRecordOne(Integer idx, BigDecimal record, String judgeEmail) {
        AttemptDTO lastAttempt=new AttemptDTO(attemptRepository.findByIdx(idx));
        lastAttempt.setTime(record);
        lastAttempt.setJudge(new MaskedAccountDTO(accountRepository.findByEmail(judgeEmail).orElseThrow()));
        attemptRepository.save(lastAttempt.toEntity(recordRepository, accountRepository));
        Record record1 = recordRepository.findByIdx(lastAttempt.getRecordIdx());
        applyRecordUpdate(record1);
    }

    public void addExtra(ExtraDTO extraDTO, MaskedAccountDTO delegate){
        RecordDTO recordDTO =new RecordDTO(recordRepository.findByIdx(extraDTO.getPartIdx()));
        AttemptDTO attemptDTO=recordDTO.getAttempts().get(extraDTO.getExtraTry()-1);
        attemptDTO.setExtra(extraDTO.getExtraNum());
        attemptDTO.setExReason(extraDTO.getExtraReason());
        attemptDTO.setDelegate(delegate);
        attemptRepository.save(attemptDTO.toEntity(recordRepository, accountRepository));
    }
    public void deleteExtra(Integer partIdx, Integer extraNum){
        Attempt attempt=attemptRepository.findByRecord_IdxAndExtra(partIdx, extraNum);
        if(attempt==null){
            return;
        }
        AttemptDTO attemptDTO=new AttemptDTO(attempt);
        attemptDTO.setExtra(null);
        attemptDTO.setExReason(null);
        attemptDTO.setDelegate(null);
        attemptDTO.setScrambler(null);
        attemptRepository.save(attemptDTO.toEntity(recordRepository, accountRepository));
    }

    public SingleAverageDTO calculateExpectation(Integer idx) {
        Record record = recordRepository.findByIdx(idx);
        String avgCalc = record.getRound().getCubeEvent().getAvgCalc();

        // "ao" 로 시작하지 않으면 null 리턴
        if (!avgCalc.startsWith("ao")) {
            return null;
        }

        // "ao" 뒤 숫자 추출
        int aoCount = Integer.parseInt(avgCalc.substring(2)); // "ao12" → 12

        List<Attempt> attemptList = record.getAttempts();
        if (attemptList.size() < aoCount) {
            return null; // 시도 개수가 부족하면 계산 불가
        }

        // 아직 시도 중이면 기대값 계산
        // 마지막 시도가 비어 있거나 0보다 크면
        Attempt lastAttempt = attemptList.get(Math.min(aoCount - 1, attemptList.size() - 1));
        if (lastAttempt.getTime() == null || lastAttempt.getTime().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        // Best/Worst 가정용 리스트 구성
        List<BigDecimal> bestList = new ArrayList<>();
        List<BigDecimal> worstList = new ArrayList<>();

        for (int i = 0; i < aoCount - 1; i++) {
            bestList.add(attemptList.get(i).getTime());
            worstList.add(attemptList.get(i).getTime());
        }

        // 남은 한 시도를 최소/최대 값으로 가정
        bestList.add(BigDecimal.valueOf(0.01));  // 매우 좋은 기록
        worstList.add(BigDecimal.valueOf(-1));  // 매우 나쁜 기록

        // 평균 계산
        SingleAverageDTO expectedDTO = new SingleAverageDTO();
        expectedDTO.setSingle(formatSeconds(calculateAoXAverage(bestList)));
        expectedDTO.setAverage(formatSeconds(calculateAoXAverage(worstList)));

        return expectedDTO;
    }

    public String formatSeconds(BigDecimal seconds) {
        if(seconds.equals(BigDecimal.valueOf(-1))){
            return "DNF";
        }
        else if(seconds.equals(BigDecimal.valueOf(-2))){
            return "DNS";
        }
        int totalMillis = seconds.multiply(BigDecimal.valueOf(100)).intValue();

        int minutes = totalMillis / 6000;
        int secs = (totalMillis % 6000) / 100;
        int millis = totalMillis % 100;

        return String.format("%d:%02d.%02d", minutes, secs, millis);
    }

    public int updateScrambler(Integer idx, String email){
        Attempt attempt=attemptRepository.findByIdx(idx);
        if(attempt.getRecord().getAccount().getEmail().equals(email)){
            return -1;
        }
        AttemptDTO attemptDTO=new AttemptDTO(attempt);
        MaskedAccountDTO accountDTO=new MaskedAccountDTO(accountRepository.findByEmail(email).orElseThrow(null));
        if(attemptDTO.getExtra()==null || attemptDTO.getExtra().equals(0)){
            attemptDTO.setScrambler(accountDTO);
        }
        else {
            attemptDTO.setExScrambler(accountDTO);
        }
        attemptRepository.save(attemptDTO.toEntity(recordRepository, accountRepository));
        return 0;
    }

    private void applyRecordUpdate(Record record) {
        RecordDTO recordDTO=new RecordDTO(record);
        String calcType = record.getRound().getCubeEvent().getAvgCalc();
        List<Attempt> attemptList=record.getAttempts();
        List<BigDecimal> times=new ArrayList<>();
        boolean isZero=false;
        for(Attempt attempt:attemptList){
            times.add(attempt.getTime());
            if(attempt.getTime()==null || attempt.getTime().compareTo(BigDecimal.ZERO)==0){
                isZero=true;
            }
        }
        switch (calcType.substring(0,2)) {
            case "ao" -> {
                if(attemptList.size()==Integer.parseInt(calcType.substring(2)) && !isZero) {
                    recordDTO.setAverage(calculateAoXAverage(times));
                    recordDTO.setSingle(calculateSingle(times));
                }
                else{
                    recordDTO.setAverage(BigDecimal.ZERO);
                    recordDTO.setSingle(BigDecimal.ZERO);
                    recordDTO.setRanking(null);
                }
            }
            case "mo" -> {
                if(attemptList.size()==3 && !isZero) {
                    recordDTO.setAverage(calculateMo3Average(times.subList(0, 3)));
                    recordDTO.setSingle(calculateSingle(times.subList(0, 3)));
                }
                else{
                    recordDTO.setAverage(BigDecimal.ZERO);
                    recordDTO.setSingle(BigDecimal.ZERO);
                    recordDTO.setRanking(null);
                }
            }
            case "bo" -> {
                if(attemptList.size()==Integer.parseInt(calcType.substring(2)) && !isZero) {
                    recordDTO.setSingle(calculateSingle(times));
                }
                else{
                    recordDTO.setSingle(BigDecimal.ZERO);
                    recordDTO.setRanking(null);
                }
            }
            case "si" -> {
                if(attemptList.size()==1 && !isZero) {
                    recordDTO.setSingle(times.get(0));
                    recordDTO.setAverage(times.get(0));
                }
                else{
                    recordDTO.setSingle(BigDecimal.ZERO);
                    recordDTO.setRanking(null);
                }
            }
        }
        recordRepository.save(recordDTO.toEntityFull(accountRepository, attemptList));
        recordRepository.setRankOnRound(recordDTO.getRound().getIdx());
        em.clear();
        List<Record> recordList = recordRepository.findByRound_IdxForBestType(recordDTO.getRound().getIdx());
        for(Record r : recordList) {
            if(!r.getIdx().equals(recordDTO.getIdx()) && (r.getSingleBestType()==null || r.getSingleBestType()==BestType.PR) && (r.getAverageBestType()==null || r.getAverageBestType()==BestType.PR)){
                continue;
            }
            RecordDTO eachRecordDTO =new RecordDTO(r);
            Map<String, BestType> bestTypeMap = calculateBestType(
                    r.getIdx(),
                    r.getAccount().getEmail(),
                    r.getAccount().getState(),
                    r.getRound().getCubeEvent().getEventName(),
                    r.getSingle(),
                    r.getAverage()
            );
            eachRecordDTO.setSingleBestType(bestTypeMap.get("single"));
            eachRecordDTO.setAverageBestType(bestTypeMap.get("average"));
            recordRepository.save(eachRecordDTO.toEntityFull(accountRepository, r.getAttempts()));
        }
        recordRepository.flush();
    }


    public void deleteParticipate(Integer idx) {
        Integer roundIdx=recordRepository.findByIdx(idx).getRound().getIdx();
        recordRepository.deleteByIdx(idx);
        recordRepository.setRankOnRound(roundIdx);
    }

    public void quitParticipate(List<Integer> partList) {
        for(Integer part:partList){
            recordRepository.deleteByIdx(part);
        }
    }

    public Integer addAdvance(Integer pastRoundIdx, Integer advanceRoundIdx){
        Round pastRound=roundRepository.findByIdx(pastRoundIdx);
        List<RecordDTO> participateList=getParticipate(pastRoundIdx);
        List<Record> advanceList=new ArrayList<>();
        for(int i=0; i<pastRound.getAdvance(); i++){
            RecordDTO advanceParticipate=participateList.get(i);
            advanceParticipate.setRound(roundRepository.findByIdx(advanceRoundIdx));
            advanceList.add(advanceParticipate.toEntity(accountRepository));
        }
        return recordRepository.saveAll(advanceList).size();
    }

    public Integer addParticipate(List<RecordRequestDTO> participates) {
        List<Record> recordList =new ArrayList<>();
        for(RecordRequestDTO recordRequestDTO :participates){
            recordList.add(recordRequestDTO.toEntity(accountRepository, roundRepository));
        }
        List<Record> records=recordRepository.saveAll(recordList);
        for(Record record:records){
            if (record.getRound().getCubeEvent().getAvgCalc().equals("single")) {
                attemptRepository.save(new AttemptDTO(record, 1).toInitialEntity(recordRepository));
            } else {
                for (int i = 1; i <= Integer.parseInt(record.getRound().getCubeEvent().getAvgCalc().substring(2)); i++) {
                    attemptRepository.save(new AttemptDTO(record, i).toInitialEntity(recordRepository));
                }
            }
        }
        return records.size();
    }

    private static BigDecimal calculateAoXAverage(List<BigDecimal> times) {
        List<BigDecimal> valid = new ArrayList<>();
        int penaltyCount = 0;

        for (BigDecimal t : times) {
            if (t.compareTo(BigDecimal.ZERO) < 0) penaltyCount++;
            else valid.add(t);
        }

        if (penaltyCount >= 2) return DNF;

        // 하나라도 DNF/DNS 있으면 Integer.MAX_VALUE처럼 처리
        while (valid.size() < times.size()) {
            valid.add(BigDecimal.valueOf(Long.MAX_VALUE));
        }

        valid.sort(Comparator.naturalOrder());
        List<BigDecimal> middleThree = valid.subList(1, times.size()-1);

        BigDecimal sum = middleThree.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(times.size()-2), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateMo3Average(List<BigDecimal> times) {
        if (times.stream().anyMatch(t -> t.compareTo(BigDecimal.ZERO) < 0)) return DNF;

        BigDecimal sum = times.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(times.size()), 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateSingle(List<BigDecimal> times) {
        return times.stream()
                .filter(t -> t.compareTo(BigDecimal.ZERO) >= 0)
                .min(Comparator.naturalOrder())
                .orElse(DNF);
    }

    private Map<String, BestType> calculateBestType(Integer idx, String email, String state, String eventName, BigDecimal single, BigDecimal average){
        Map<String, BestType> best=new HashMap<>();
        BestResult personalSingleBest= recordRepository.findPersonalSingleBest(email, eventName);
        if(single.compareTo(BigDecimal.ZERO)>0) {
            if (personalSingleBest == null || personalSingleBest.getBest().compareTo(single) > 0 || (personalSingleBest.getIdx().equals(idx) && personalSingleBest.getBest().compareTo(single) >= 0)) {
                BestResult stateSingleBest = recordRepository.findStateSingleBest(state, eventName);
                if (stateSingleBest == null || stateSingleBest.getBest().compareTo(single) > 0 || (stateSingleBest.getIdx().equals(idx) && stateSingleBest.getBest().compareTo(single) >= 0)) {
                    BestResult allSingleBest = recordRepository.findAllSingleBest(eventName);
                    if (allSingleBest == null || allSingleBest.getBest().compareTo(single) > 0 || (allSingleBest.getIdx().equals(idx) && allSingleBest.getBest().compareTo(single) >= 0)) {
                        best.put("single", BestType.CCR);
                    } else {
                        best.put("single", BestType.SR);
                    }
                } else {
                    best.put("single", BestType.PR);
                }
            } else {
                best.put("single", null);
            }
        }
        BestResult personalAverageBest= recordRepository.findPersonalAverageBest(email, eventName);
        if(average.compareTo(BigDecimal.ZERO)>0) {
            if (personalAverageBest == null || personalAverageBest.getBest().compareTo(average) > 0 || (personalAverageBest.getIdx().equals(idx) && personalAverageBest.getBest().compareTo(single) >= 0)) {
                BestResult stateAverageBest = recordRepository.findStateAverageBest(state, eventName);
                if (stateAverageBest == null || stateAverageBest.getBest().compareTo(average) > 0 || (stateAverageBest.getIdx().equals(idx) && stateAverageBest.getBest().compareTo(single) >= 0)) {
                    BestResult allAverageBest = recordRepository.findAllAverageBest(eventName);
                    if (allAverageBest == null || allAverageBest.getBest().compareTo(average) > 0 || (allAverageBest.getIdx().equals(idx) && allAverageBest.getBest().compareTo(single) >= 0)) {
                        best.put("average", BestType.CCR);
                    } else {
                        best.put("average", BestType.SR);
                    }
                } else {
                    best.put("average", BestType.PR);
                }
            } else {
                best.put("average", null);
            }
        }
        return best;
    }
    private boolean isEmpty(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }


}
