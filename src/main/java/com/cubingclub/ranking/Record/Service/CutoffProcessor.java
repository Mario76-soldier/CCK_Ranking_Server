package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Record.Entity.Attempt;
import com.cubingclub.ranking.Record.Entity.CalcType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 컷오프와 타임리밋 처리를 담당하는 클래스
 */
@Component
@RequiredArgsConstructor
public class CutoffProcessor {
    private final RecordCalculator calculator;

    /**
     * 컷오프와 타임리밋을 적용하여 시도 시간 업데이트
     * 
     * @param attempts 시도 리스트
     * @param times 입력된 시간 리스트
     * @param cutoff 컷오프 기준 시간
     * @param timeLimit 타임리밋
     */
    public void applyRules(
            List<Attempt> attempts,
            List<BigDecimal> times,
            BigDecimal cutoff,
            BigDecimal timeLimit,
            CalcType.CalcTypeInfo avgCalc
    ) {
        boolean isCutoffTriggered = false;
        int cutoffFailCount = 0;
        int cutoffCheckIndex = calculateCutoffCheckIndex(avgCalc, cutoff);

        for (int i = 0; i < attempts.size(); i++) {
            Attempt attempt = attempts.get(i);
            BigDecimal time = times.get(i);

            // 컷오프가 이미 발동되었으면 DNS 처리
            if (isCutoffTriggered) {
                attempt.updateTime(calculator.getDNS());
                continue;
            }

            // 타임리밋 초과 시 DNF 처리
            if (isOverTimeLimit(time, timeLimit)) {
                attempt.updateTime(calculator.getDNF());
                continue;
            }

            // 정상 시간 업데이트
            attempt.updateTime(time);

            // 컷오프 체크
            if (shouldCheckCutoff(cutoff, i, cutoffCheckIndex, time)) {
                cutoffFailCount++;
                if (shouldTriggerCutoff(avgCalc, cutoffFailCount)) {
                    isCutoffTriggered = true;
                }
            }
        }
    }

    /**
     * 컷오프 체크 인덱스 계산
     * - mo시도: 1번째 시도 (index 0)
     * - ao시도: 2번째 시도까지 (index 1)
     * - ba시도: 3번째 시도까지 (index 2)
     * - 이외: 컷오프 미적용
     */
    private int calculateCutoffCheckIndex(CalcType.CalcTypeInfo avgCalc, BigDecimal cutoff) {
        if (cutoff == null) {
            return -1;
        }
        if (avgCalc.type()==CalcType.MO) {
            return 0;
        }
        if (avgCalc.type()==CalcType.AO) {
            return 1;
        }
        if (avgCalc.type()==CalcType.BA) {
            return 2;
        }
        return -1;
    }

    /**
     * 타임리밋 초과 여부 확인
     */
    private boolean isOverTimeLimit(BigDecimal time, BigDecimal timeLimit) {
        return timeLimit != null && time.compareTo(timeLimit) > 0;
    }

    /**
     * 컷오프 체크가 필요한지 확인
     */
    private boolean shouldCheckCutoff(
            BigDecimal cutoff,
            int currentIndex,
            int cutoffCheckIndex,
            BigDecimal time
    ) {
        return cutoff != null 
                && currentIndex <= cutoffCheckIndex 
                && time.compareTo(cutoff) > 0;
    }

    /**
     * 컷오프 발동 여부 결정
     * - mo시도: 1회 실패 시 발동
     * - ao시도: 2회 실패 시 발동
     * - ba시도: 3회 실패 시 발동
     * - 이외: 컷오프 미적용
     */
    private boolean shouldTriggerCutoff(CalcType.CalcTypeInfo avgCalc, int failCount) {
        if (avgCalc.type()==CalcType.MO) {
            return failCount >= 1;
        }
        if (avgCalc.type()==CalcType.AO) {
            return failCount >= 2;
        }
        if (avgCalc.type()==CalcType.BA) {
            return failCount >= 3;
        }
        return false;
    }
}
