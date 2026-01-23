package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Record.DTO.BestResult;
import com.cubingclub.ranking.Record.Entity.BestType;
import com.cubingclub.ranking.Record.Entity.RecordType;
import com.cubingclub.ranking.Record.Repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/**
 * 베스트 타입(PR, SR, CCR) 계산을 담당하는 서비스
 * - PR: Personal Record (개인 기록)
 * - SR: State Record (지역 기록)
 * - CCR: Club Record (클럽 기록)
 */
@Service
@RequiredArgsConstructor
public class BestTypeService {
    private final RecordRepository recordRepository;

    /**
     * Single과 Average에 대한 베스트 타입 계산
     */
    public Map<RecordType, BestType> calculateBestTypes(
            Integer recordIdx,
            String cckId,
            String state,
            String eventName,
            BigDecimal single,
            BigDecimal average
    ) {
        Map<RecordType, BestType> result = new EnumMap<>(RecordType.class);

        // Single 베스트 타입 계산
        if (isValidRecord(single)) {
            BestType singleBestType = determineBestType(
                    recordIdx, 
                    single,
                    cckId, 
                    state, 
                    eventName, 
                    "SINGLE"
            );
            result.put(RecordType.SINGLE, singleBestType);
        }

        // Average 베스트 타입 계산
        if (isValidRecord(average)) {
            BestType averageBestType = determineBestType(
                    recordIdx, 
                    average,
                    cckId, 
                    state, 
                    eventName, 
                    "AVERAGE"
            );
            result.put(RecordType.AVERAGE, averageBestType);
        }

        return result;
    }

    /**
     * 특정 기록의 베스트 타입 결정
     */
    private BestType determineBestType(
            Integer recordIdx,
            BigDecimal value,
            String cckId,
            String state,
            String eventName,
            String type
    ) {
        BestResult personalBest = findBest(cckId, null, eventName, type);
        BestResult stateBest = findBest(null, state, eventName, type);
        BestResult clubBest = findBest(null, null, eventName, type);

        // 개인 기록보다 좋지 않으면 null
        if (!isBetterThan(recordIdx, value, personalBest)) {
            return null;
        }

        // 지역 기록보다 좋지 않으면 PR
        if (!isBetterThan(recordIdx, value, stateBest)) {
            return BestType.PR;
        }

        // 클럽 기록보다 좋지 않으면 SR
        if (!isBetterThan(recordIdx, value, clubBest)) {
            return BestType.SR;
        }

        // 모든 기록을 갱신하면 CCR
        return BestType.CCR;
    }

    /**
     * 특정 조건의 최고 기록 조회
     */
    private BestResult findBest(String cckId, String state, String eventName, String type) {
        return recordRepository
                .findBest(cckId, state, eventName, type, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * 현재 기록이 기존 최고 기록보다 좋은지 확인
     */
    private Boolean isBetterThan(Integer recordIdx, BigDecimal value, BestResult currentBest) {
        if (currentBest == null) {
            return true;
        }

        int comparison = currentBest.getBest().compareTo(value);

        // 기존 기록이 더 크면(느리면) 현재가 더 좋음
        if (comparison > 0) {
            return true;
        }

        // 같은 기록이고 같은 레코드면 갱신으로 간주
        return comparison == 0 && currentBest.getIdx().equals(recordIdx);
    }

    /**
     * 유효한 기록인지 확인 (0보다 큰 값)
     */
    private Boolean isValidRecord(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }
}
