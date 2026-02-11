package com.cubingclub.ranking.Record.Entity;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 큐브 기록 계산 타입
 * - AO: Average of X (최고/최저 제외 평균)
 * - BA: Best Average of X (최저 2개 제외 평균)
 * - MO: Mean of 3 (모든 기록 평균)
 * - BO: Best of X (최고 기록)
 * - SINGLE: 단일 시도
 */
@Getter
public enum CalcType {
    AO("ao", true),      // Average of X (ao3, ao5, ao12)
    BA("ba", true),      // Best Average of X
    MO("mo", true),      // Mean of 3
    BO("bo", false),     // Best of X (최고 기록 = Single과 동일)
    SINGLE("single", false);  // Single

    private final String prefix;
    private final boolean hasAverage;  // Average 계산 여부

    CalcType(String prefix, boolean hasAverage) {
        this.prefix = prefix;
        this.hasAverage = hasAverage;
    }

    /**
     * avgCalc 문자열로부터 CalcType과 시도 횟수 파싱
     * 예: "ao5" -> CalcType.AO, 5
     *     "single" -> CalcType.SINGLE, 1
     */
    public static CalcTypeInfo parse(String avgCalc) {
        if (avgCalc == null || avgCalc.isEmpty()) {
            throw new IllegalArgumentException("avgCalc cannot be null or empty");
        }

        // "single" 특수 케이스
        if (avgCalc.equals("single")) {
            return new CalcTypeInfo(SINGLE, 1);
        }

        // 최소 3자 이상이어야 함 (예: ao3, bo1)
        if (avgCalc.length() < 3) {
            throw new IllegalArgumentException("Invalid avgCalc format: " + avgCalc);
        }

        String prefix = avgCalc.substring(0, 2).toLowerCase();
        String countStr = avgCalc.substring(2);

        CalcType type = Arrays.stream(values())
                .filter(ct -> ct.prefix.equals(prefix))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown calc type: " + prefix));

        try {
            int count = Integer.parseInt(countStr);
            return new CalcTypeInfo(type, count);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid attempt count in avgCalc: " + avgCalc);
        }
    }

    /**
         * CalcType 정보를 담는 VO
         */
        public record CalcTypeInfo(CalcType type, int attemptCount) {

        /**
             * 실제 시도 횟수가 예상과 일치하는지 확인
             */
            public boolean isValidAttemptCount(int actualCount) {
                return actualCount == attemptCount;
            }

            /**
             * 시도 리스트에 비어있는 값이 있는지 확인
             */
            public boolean hasEmptyAttempt(List<BigDecimal> times) {
                return times.stream()
                        .anyMatch(t -> t == null || t.compareTo(BigDecimal.ZERO) == 0);
            }

            /**
             * 기록 계산이 가능한 상태인지 확인
             */
            public boolean canCalculate(List<BigDecimal> times) {
                return isValidAttemptCount(times.size()) && !hasEmptyAttempt(times);
            }
        }
}
