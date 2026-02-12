package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Auth.DTO.AccountDTO;
import com.cubingclub.ranking.Auth.DTO.PlayerInfo;
import com.cubingclub.ranking.Auth.Service.AuthService;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.BestType;
import com.cubingclub.ranking.Record.Entity.CalcType;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.Entity.RecordType;
import com.cubingclub.ranking.Record.Repository.AttemptRepository;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    private final CacheManager cacheManager;

    private final AuthService authService;
    private final RecordCalculator recordCalculator;
    private final BestTypeService bestTypeService;
    private final CutoffProcessor cutoffProcessor;

    public List<RecordDTO> getRecordList(Integer roundIdx) {
        List<Record> list=recordRepository.findByRound_Idx(roundIdx);
        list.forEach(record -> Hibernate.initialize(record.getAttempts()));
        return list.stream()
                .map(RecordDTO::new)
                .toList();
    }

    public List<RecordDTO> getBreakRecordList(Integer compIdx) {
        List<Record> list=recordRepository.findRecordsWithBestTypeByCompIdx(compIdx);
        list.forEach(record -> Hibernate.initialize(record.getAttempts()));
        return list.stream()
                .map(RecordDTO::new)
                .toList();
    }

    public void updateRecord(RecordUpdateDTO p) {
        Record record = recordRepository.findByIdx(p.getIdx());
        List<Attempt> attemptList=record.getAttempts();

        for(int i=0; i<p.getTimes().size(); i++){
            Attempt attempt=attemptList.get(i);
            attempt.updateTime(p.getTimes().get(i));
        }
        // 기록 계산 및 업데이트
        applyRecordUpdate(record);
    }

    public void addExtra(ExtraDTO extraDTO, String authHeader){
        String token = authHeader.substring(7);
        AccountDTO delegateInfo=authService.getMyInfo(token);
        Record record = recordRepository.findByIdx(extraDTO.getRecordIdx());
        Hibernate.initialize(record.getAttempts());
        Attempt attempt = record.getAttempts().get(extraDTO.getExtraTry()-1);
        attempt.updateExtra(extraDTO.getExtraNum(), extraDTO.getExtraReason(), delegateInfo.getName(), delegateInfo.getCckId());
    }

    public void deleteExtra(Integer partIdx, Integer extraNum){
        Attempt attempt=attemptRepository.findByRecord_IdxAndExtra(partIdx, extraNum);
        if(attempt==null){
            return;
        }
        attempt.updateExtra(null, null, null, null);
        attempt.updateScrambler(null, null);
        attempt.updateExScrambler(null, null);
    }

    public void updateScrambler(Integer idx, PlayerInfo scramblerInfo){
        Attempt attempt=attemptRepository.findByIdx(idx);
        log.info(scramblerInfo.getCckId());
        if(attempt.getRecord().getCckId().equals(scramblerInfo.getCckId())){
            return;
        }
        if(attempt.getExtra()==null || attempt.getExtra().equals(0)){
            attempt.updateScrambler(scramblerInfo.getName(), scramblerInfo.getCckId());
        }
        else {
            attempt.updateExScrambler(scramblerInfo.getName(), scramblerInfo.getCckId());
        }
    }

    protected void applyRecordUpdate(Record record) {
        String avgCalcStr = record.getRound().getCubeEvent().getAvgCalc();
        CalcType.CalcTypeInfo calcInfo = CalcType.parse(avgCalcStr);
        
        List<Attempt> attemptList = record.getAttempts();
        List<BigDecimal> times = attemptList.stream()
                .map(Attempt::getTime)
                .toList();

        cutoffProcessor.applyRules(
                attemptList,
                times,
                record.getRound().getCutoff(),
                record.getRound().getTimeLimit(),
                calcInfo
        );
        
        // 계산 가능한 상태인지 확인
        if (calcInfo.canCalculate(times)) {
            calculateAndUpdateRecord(record, times, calcInfo);
        } else {
            resetRecord(record);
        }
        
        // Ranking은 SQL로 일괄 업데이트 (기존 방식 유지)
        recordRepository.setRankOnRound(record.getRound().getIdx());
        em.clear();
        
        // BestType 업데이트
        updateBestTypesForRound(record);
        recordRepository.flush();
        
        // 글로벌 통계 캐시 삭제
        evictStatsCache(record.getRound().getCubeEvent().getEventName());
    }
    
    /**
     * 기록 계산 및 업데이트
     */
    private void calculateAndUpdateRecord(Record record, List<BigDecimal> times, CalcType.CalcTypeInfo calcInfo) {
        BigDecimal single;
        BigDecimal average;
        
        switch (calcInfo.type()) {
            case AO -> {
                single = recordCalculator.calculateSingle(times);
                average = recordCalculator.calculateAoXAverage(times);
            }
            case BA -> {
                single = recordCalculator.calculateSingle(times);
                average = recordCalculator.calculateBAoXAverage(times);
            }
            case MO -> {
                single = recordCalculator.calculateSingle(times);
                average = recordCalculator.calculateMo3Average(times);
            }
            case BO -> {
                single = recordCalculator.calculateSingle(times);
                average = single;  // Best of X는 single과 average가 동일
            }
            case SINGLE -> {
                single = times.get(0);
                average = times.get(0);
            }
            default -> throw new IllegalStateException("Unknown calc type: " + calcInfo.type());
        }
        
        record.updateRecord(single, average);
    }
    
    /**
     * 기록 초기화 (계산 불가능한 상태)
     */
    private void resetRecord(Record record) {
        record.updateRecord(BigDecimal.ZERO, BigDecimal.ZERO);
        record.updateBestType(null, null);
        record.updateRanking(null, null);
    }
    
    /**
     * 라운드 내 모든 레코드의 BestType 업데이트
     */
    private void updateBestTypesForRound(Record updatedRecord) {
        List<Record> recordList = recordRepository.findByRound_IdxForBestType(
                updatedRecord.getRound().getIdx()
        );
        
        for (Record r : recordList) {
            // 업데이트할 필요가 없는 레코드는 스킵
            if (shouldSkipBestTypeUpdate(r, updatedRecord)) {
                continue;
            }
            
            Map<RecordType, BestType> bestTypeMap = bestTypeService.calculateBestTypes(
                    r.getIdx(),
                    r.getCckId(),
                    r.getState(),
                    r.getRound().getCubeEvent().getEventName(),
                    r.getSingle(),
                    r.getAverage()
            );
            
            r.updateBestType(
                    bestTypeMap.get(RecordType.SINGLE),
                    bestTypeMap.get(RecordType.AVERAGE)
            );
        }
    }
    
    /**
     * BestType 업데이트를 스킵해야 하는지 확인
     */
    private boolean shouldSkipBestTypeUpdate(Record record, Record updatedRecord) {
        // 업데이트된 레코드가 아니고, PR 이하인 경우 스킵
        return !record.getIdx().equals(updatedRecord.getIdx())
                && (record.getSingleBestType() == null || record.getSingleBestType() == BestType.PR)
                && (record.getAverageBestType() == null || record.getAverageBestType() == BestType.PR);
    }


    public void deleteRecord(Integer idx) {
        Record record = recordRepository.findByIdx(idx);
        Integer roundIdx = record.getRound().getIdx();
        String eventName = record.getRound().getCubeEvent().getEventName();
        
        recordRepository.deleteById(idx);
        recordRepository.setRankOnRound(roundIdx);
        
        // 글로벌 통계 캐시 삭제
        evictStatsCache(eventName);
    }

    private void evictStatsCache(String eventName) {
        var globalSingleCache = cacheManager.getCache("globalStatsSingle");
        var globalAverageCache = cacheManager.getCache("globalStatsAverage");
        var stateSingleCache = cacheManager.getCache("stateStatsSingle");
        var stateAverageCache = cacheManager.getCache("stateStatsAverage");
        var stateCache = cacheManager.getCache("stateStat");
        
        if (globalSingleCache != null) {
            globalSingleCache.evict(eventName);
        }
        if (globalAverageCache != null) {
            globalAverageCache.evict(eventName);
        }
        if (stateSingleCache != null) {
            stateSingleCache.evict(eventName);
        }
        if (stateAverageCache != null) {
            stateAverageCache.evict(eventName);
        }
        if (stateCache != null) {
            stateCache.evict(eventName);
        }
    }

    public void quitRecord(List<Integer> partList) {
        for(Integer part:partList){
            recordRepository.deleteById(part);
        }
    }

    public void advancePlayer(AdvanceRequestDTO advanceRequestDTO){
        Round pastRound=roundRepository.findByIdx(advanceRequestDTO.getPastRoundIdx()).orElseThrow(()->new CustomizedException(ErrorCode.ROUND_NOT_FOUND));
        Round nextRound=roundRepository.findByIdx(advanceRequestDTO.getAdvanceRoundIdx()).orElseThrow(()->new CustomizedException(ErrorCode.ROUND_NOT_FOUND));

        List<RecordDTO> recordList= getRecordList(advanceRequestDTO.getPastRoundIdx());
        List<String> groupList = nextRound.getGroupList();
        List<Record> advanceList=new ArrayList<>();

        int advanceCount=pastRound.getAdvance();
        int limit = Math.min(advanceCount, recordList.size());

        if (groupList == null || groupList.isEmpty()) {
            for (int i = 0; i < limit; i++) {
                RecordDTO advancedPlayer = recordList.get(i);
                Record record = advancedPlayer.toEntity(nextRound);
                advanceList.add(record);
            }
        }
        else {
            int groupSize = groupList.size();

            for (int i = 0; i < limit; i++) {
                RecordDTO advancedPlayer = recordList.get(i);
                String group = groupList.get(i % groupSize);
                advancedPlayer.setRoundGroup(group);

                Record record = advancedPlayer.toEntity(nextRound);
                advanceList.add(record);
            }
        }
        addAttempts(recordRepository.saveAll(advanceList));
    }

    public void addPlayer(List<PlayerAddDTO> playerAddDTOList) {
        List<Record> recordList =new ArrayList<>();
        for(PlayerAddDTO playerAddDTO : playerAddDTOList){
            recordList.add(playerAddDTO.toEntity(roundRepository.findByIdx(playerAddDTO.getRoundIdx()).orElseThrow(()->new CustomizedException(ErrorCode.ROUND_NOT_FOUND))));
        }
        addAttempts(recordRepository.saveAll(recordList));
    }

    public void addAttempts(List<Record> records){
        for(Record record:records){
            if (record.getRound().getCubeEvent().getAvgCalc().equals("single")) {
                attemptRepository.save(new AttemptDTO(record, 1).toInitialEntity(recordRepository));
            } else {
                for (int i = 1; i <= Integer.parseInt(record.getRound().getCubeEvent().getAvgCalc().substring(2)); i++) {
                    attemptRepository.save(new AttemptDTO(record, i).toInitialEntity(recordRepository));
                }
            }
        }
    }
}
