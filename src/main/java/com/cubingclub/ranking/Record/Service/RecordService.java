package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
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
        lastAttempt.setJudge(new AccountDTO(accountRepository.findByEmail(judgeEmail).orElseThrow()));
        attemptRepository.save(lastAttempt.toEntity(recordRepository, accountRepository));
        Record record1 = recordRepository.findByIdx(lastAttempt.getRecordIdx());
        applyRecordUpdate(record1);
    }

    public void addExtra(ExtraDTO extraDTO, AccountDTO delegate){
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

    public SingleAverageDTO calculateExpectation(Integer idx){
        Record record = recordRepository.findByIdx(idx);
        if(!record.getRound().getCubeEvent().getAvgCalc().equals("ao5")){
            return null;
        }
        List<Attempt> attemptList = record.getAttempts();
            if(attemptList.get(3).getTime()!=null || attemptList.get(3).getTime().compareTo(BigDecimal.ZERO)!=0) {
                List<BigDecimal> best = Arrays.asList(
                        attemptList.get(0).getTime(), attemptList.get(1).getTime(), attemptList.get(2).getTime(),
                        attemptList.get(3).getTime(), BigDecimal.valueOf(0.01)
                );
                List<BigDecimal> worst = Arrays.asList(
                        attemptList.get(0).getTime(), attemptList.get(1).getTime(), attemptList.get(2).getTime(),
                        attemptList.get(3).getTime(), BigDecimal.valueOf(-1)
                );
                SingleAverageDTO expectedBestWorstDTO = new SingleAverageDTO();
                expectedBestWorstDTO.setSingle(formatSeconds(calculateAo5Average(best)));
                expectedBestWorstDTO.setAverage(formatSeconds(calculateAo5Average(worst)));
                return expectedBestWorstDTO;
            }
        return null;
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

    public int updateScrambler(Integer idx, String email, Integer exNumber){
        Attempt attempt=attemptRepository.findByIdx(idx);
        if(attempt.getRecord().getAccount().getEmail().equals(email)){
            return -1;
        }
        AttemptDTO attemptDTO=new AttemptDTO(attempt);
        AccountDTO accountDTO=new AccountDTO(accountRepository.findByEmail(email).orElseThrow(null));
        if(exNumber==null || exNumber.equals(0)){
            attemptDTO.setScrambler(accountDTO);
        }
        else {
            attemptDTO.setScrambler(accountDTO);
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
        switch (calcType) {
            case "ao5" -> {
                if(attemptList.size()==5 && !isZero) {
                    recordDTO.setAverage(calculateAo5Average(times));
                    recordDTO.setSingle(calculateSingle(times));
                }
                else{
                    recordDTO.setAverage(BigDecimal.ZERO);
                    recordDTO.setSingle(BigDecimal.ZERO);
                    recordDTO.setRanking(null);
                }
            }
            case "mo3" -> {
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
            case "single" -> {
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

    /**public Integer addAdvance(Integer pastRoundIdx, Integer advanceRoundIdx){
        Scrambler pastRound=roundRepository.findByIdx(pastRoundIdx);
        List<RecordForUserDTO> participateList=getParticipateForUser(pastRoundIdx);
        List<Record> advanceList=new ArrayList<>();
        for(int i=0; i<pastRound.getAdvance(); i++){
            RecordDTO advanceParticipate=participateList.get(i);
            advanceParticipate.setRound(roundRepository.findByIdx(advanceRoundIdx));
            advanceList.add(advanceParticipate.toEntity(accountRepository));
        }
        return recordRepository.saveAll(advanceList).size();
    }**/

    public Integer addParticipate(List<RecordRequestDTO> participates) {
        List<Record> recordList =new ArrayList<>();
        for(RecordRequestDTO recordRequestDTO :participates){
            recordList.add(recordRequestDTO.toEntity(accountRepository, roundRepository));
        }
        List<Record> records=recordRepository.saveAll(recordList);
        for(Record record:records){
            log.info(record.getRound().getCubeEvent().getAvgCalc());
            switch (record.getRound().getCubeEvent().getAvgCalc()){
                case "ao5"->{
                    for(int i=1; i<=5; i++){
                        attemptRepository.save(new AttemptDTO(record, i).toInitialEntity(recordRepository));
                    }
                }
                case "mo3"->{
                    for(int i=1; i<=3; i++){
                        attemptRepository.save(new AttemptDTO(record, i).toInitialEntity(recordRepository));
                    }
                }
                case "single"->{
                    attemptRepository.save(new AttemptDTO(record, 1).toInitialEntity(recordRepository));
                }
            }
        }
        return records.size();
    }

    private static BigDecimal calculateAo5Average(List<BigDecimal> times) {
        List<BigDecimal> valid = new ArrayList<>();
        int penaltyCount = 0;

        for (BigDecimal t : times) {
            if (t.compareTo(BigDecimal.ZERO) < 0) penaltyCount++;
            else valid.add(t);
        }

        if (penaltyCount >= 2) return DNF;

        // 하나라도 DNF/DNS 있으면 Integer.MAX_VALUE처럼 처리
        while (valid.size() < 5) {
            valid.add(BigDecimal.valueOf(Long.MAX_VALUE));
        }

        valid.sort(Comparator.naturalOrder());
        List<BigDecimal> middleThree = valid.subList(1, 4);

        BigDecimal sum = middleThree.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
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
