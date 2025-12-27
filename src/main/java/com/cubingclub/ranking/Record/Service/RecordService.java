package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Auth.DTO.JudgeInfo;
import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.BestType;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Repository.AttemptRepository;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
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

    public RecorderResponseDTO getForRecorder(String cckId, Integer compIdx) {
            List<Record> list = recordRepository.findByCckIdAndRound_Comp_IdxOrderByRound_EventStart(cckId, compIdx);
            for (Record p : list) {
                String name = p.getName();
                String roundName = p.getRound().getCubeEvent().getEventName() + " " + p.getRound().getRoundName();
                String state=p.getState();
                List<Attempt> attemptList=p.getAttempts();

                for(Attempt attempt: attemptList){
                    if(attempt.getTime()==null || attempt.getTime().compareTo(BigDecimal.ZERO)==0){
                        return new RecorderResponseDTO(attempt.getIdx(), attempt.getAttemptTry(), name, state, roundName, attempt.getExtra());
                    }
                }
            }
        return null;
    }
    public void updateRecord(ParticipateRecordDTO p) {
        Record record = recordRepository.findByIdx(p.getIdx());
        List<Attempt> attemptList = record.getAttempts();
        int attemptCount = attemptList.size();
        BigDecimal timeLimit = record.getRound().getTimeLimit();
        BigDecimal cutoff = record.getRound().getCutoff();
        boolean isCut = false;
        int cutoffFailCount = 0;
        // 컷오프 기준 시도 인덱스 계산
        int cutoffIndex = -1;
        if (cutoff != null) {
            if (attemptCount == 3) cutoffIndex = 0; // 1번째 시도
            else if (attemptCount >= 5) cutoffIndex = 1; // 2번째 시도
        }

        for (int i = 0; i < attemptCount; i++) {
            AttemptDTO attemptDTO = new AttemptDTO(attemptList.get(i));
            BigDecimal time = p.getTimes().get(i);

            // 이미 컷오프가 발동된 경우 → 이후 시도 모두 DNS 처리
            if (isCut) {
                attemptDTO.setTime(DNS);
            }
            // 타임리밋 초과 → DNF 처리
            else if (timeLimit != null && time.compareTo(timeLimit) > 0) {
                attemptDTO.setTime(DNF);
            }
            // 컷오프 조건 확인
            else {
                attemptDTO.setTime(time);
                if (cutoff != null && i <= cutoffIndex && time.compareTo(cutoff) > 0) {
                    cutoffFailCount++;
                    // 컷오프가 5시도 기준이라면 2회 모두 초과해야 발동
                    if (attemptCount == 3 || cutoffFailCount >= 2) {
                        isCut = true;
                    }
                }
            }

            attemptRepository.save(attemptDTO.toEntity(recordRepository));
        }
        record = recordRepository.findByIdx(p.getIdx());
        applyRecordUpdate(record);
    }

    public void updateRecordOne(Integer idx, BigDecimal record, JudgeInfo judgeInfo) {
        AttemptDTO lastAttempt=new AttemptDTO(attemptRepository.findByIdx(idx));
        lastAttempt.setTime(record);
        lastAttempt.setJudgeName(judgeInfo.getName());
        lastAttempt.setJudgeCckId(judgeInfo.getCckId());
        attemptRepository.save(lastAttempt.toEntity(recordRepository));
        Record record1 = recordRepository.findByIdx(lastAttempt.getRecordIdx());
        applyRecordUpdate(record1);
    }

    public void addExtra(ExtraDTO extraDTO, JudgeInfo delegateInfo){
        RecordDTO recordDTO =new RecordDTO(recordRepository.findByIdx(extraDTO.getPartIdx()));
        AttemptDTO attemptDTO=recordDTO.getAttempts().get(extraDTO.getExtraTry()-1);
        attemptDTO.setExtra(extraDTO.getExtraNum());
        attemptDTO.setExReason(extraDTO.getExtraReason());
        attemptDTO.setDelegateName(delegateInfo.getName());
        attemptDTO.setDelegateCckId(delegateInfo.getCckId());
        attemptRepository.save(attemptDTO.toEntity(recordRepository));
    }
    public void deleteExtra(Integer partIdx, Integer extraNum){
        Attempt attempt=attemptRepository.findByRecord_IdxAndExtra(partIdx, extraNum);
        if(attempt==null){
            return;
        }
        AttemptDTO attemptDTO=new AttemptDTO(attempt);
        attemptDTO.setExtra(null);
        attemptDTO.setExReason(null);
        attemptDTO.setDelegateName(null);
        attemptDTO.setDelegateCckId(null);
        attemptDTO.setScramblerName(null);
        attemptDTO.setScramblerCckId(null);
        attemptRepository.save(attemptDTO.toEntity(recordRepository));
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

    public int updateScrambler(Integer idx, JudgeInfo scramblerInfo){
        Attempt attempt=attemptRepository.findByIdx(idx);
        if(attempt.getRecord().getCckId().equals(scramblerInfo.getCckId())){
            return -1;
        }
        AttemptDTO attemptDTO=new AttemptDTO(attempt);
        if(attemptDTO.getExtra()==null || attemptDTO.getExtra().equals(0)){
            attemptDTO.setScramblerName(scramblerInfo.getName());
            attemptDTO.setScramblerCckId(scramblerInfo.getCckId());
        }
        else {
            attemptDTO.setExScramblerName(scramblerInfo.getName());
            attemptDTO.setExScramblerCckId(scramblerInfo.getCckId());
        }
        attemptRepository.save(attemptDTO.toEntity(recordRepository));
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
                    recordDTO.setSingleBestType(null);
                    recordDTO.setAverageBestType(null);
                    recordDTO.setRanking(null);
                }
            }
            case "ba" -> {
                if(attemptList.size()==Integer.parseInt(calcType.substring(2)) && !isZero) {
                    recordDTO.setAverage(calculateBAoXAverage(times));
                    recordDTO.setSingle(calculateSingle(times));
                }
                else{
                    recordDTO.setAverage(BigDecimal.ZERO);
                    recordDTO.setSingle(BigDecimal.ZERO);
                    recordDTO.setSingleBestType(null);
                    recordDTO.setAverageBestType(null);
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
                    recordDTO.setSingleBestType(null);
                    recordDTO.setAverageBestType(null);
                    recordDTO.setRanking(null);
                }
            }
            case "bo" -> {
                if(attemptList.size()==Integer.parseInt(calcType.substring(2)) && !isZero) {
                    recordDTO.setSingle(calculateSingle(times));
                    recordDTO.setAverage(calculateSingle(times));
                }
                else{
                    recordDTO.setSingle(BigDecimal.ZERO);
                    recordDTO.setSingleBestType(null);
                    recordDTO.setAverageBestType(null);
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
                    recordDTO.setSingleBestType(null);
                    recordDTO.setAverageBestType(null);
                    recordDTO.setRanking(null);
                }
            }
        }
        recordRepository.save(recordDTO.toEntityFull(attemptList));
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
                    r.getCckId(),
                    r.getState(),
                    r.getRound().getCubeEvent().getEventName(),
                    r.getSingle(),
                    r.getAverage()
            );
            eachRecordDTO.setSingleBestType(bestTypeMap.get("single"));
            eachRecordDTO.setAverageBestType(bestTypeMap.get("average"));
            recordRepository.save(eachRecordDTO.toEntityFull(r.getAttempts()));
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
            advanceList.add(advanceParticipate.toEntity());
        }
        return addAttempts(recordRepository.saveAll(advanceList));
    }

    public Integer addParticipate(List<RecordRequestDTO> participates) {
        List<Record> recordList =new ArrayList<>();
        for(RecordRequestDTO recordRequestDTO:participates){
            recordList.add(recordRequestDTO.toEntity(roundRepository));
        }
        return addAttempts(recordRepository.saveAll(recordList));
    }

    public Integer addAttempts(List<Record> records){
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

    private static BigDecimal calculateBAoXAverage(List<BigDecimal> times) {
        List<BigDecimal> valid = new ArrayList<>();
        int penaltyCount = 0;

        for (BigDecimal t : times) {
            if (t.compareTo(BigDecimal.ZERO) < 0) penaltyCount++;
            else valid.add(t);
        }

        if (penaltyCount >= 3) return DNF;

        // 하나라도 DNF/DNS 있으면 Integer.MAX_VALUE처럼 처리
        while (valid.size() < times.size()) {
            valid.add(BigDecimal.valueOf(Long.MAX_VALUE));
        }

        valid.sort(Comparator.naturalOrder());
        List<BigDecimal> middleThree = valid.subList(0, times.size()-2);

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

    private Map<String, BestType> calculateBestType(Integer idx, String cckId, String state, String eventName, BigDecimal single, BigDecimal average){
        Map<String, BestType> best=new HashMap<>();
        BestResult personalSingleBest= recordRepository.findPersonalSingleBest(cckId, eventName);
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
        BestResult personalAverageBest= recordRepository.findPersonalAverageBest(cckId, eventName);
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
