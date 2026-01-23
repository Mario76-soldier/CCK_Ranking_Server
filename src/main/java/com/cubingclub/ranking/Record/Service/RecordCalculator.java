package com.cubingclub.ranking.Record.Service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 큐브 기록 계산을 담당하는 클래스
 * Average of X, Best of X, Mean of 3, Single 등의 계산 로직을 캡슐화
 */
@Component
public class RecordCalculator {
    private static final BigDecimal DNF = BigDecimal.valueOf(-1);
    private static final BigDecimal DNS = BigDecimal.valueOf(-2);
    
    /**
     * Average of X (ao5, ao12 등) 계산
     * 최고, 최저 기록을 제외한 평균
     * DNF/DNS가 2개 이상이면 DNF 처리
     */
    public BigDecimal calculateAoXAverage(List<BigDecimal> times) {
        List<BigDecimal> valid = new ArrayList<>();
        int penaltyCount = 0;

        for (BigDecimal t : times) {
            if (isPenalty(t)) {
                penaltyCount++;
            } else {
                valid.add(t);
            }
        }

        if (penaltyCount >= 2) {
            return DNF;
        }

        // DNF/DNS를 최대값으로 간주
        while (valid.size() < times.size()) {
            valid.add(BigDecimal.valueOf(Long.MAX_VALUE));
        }

        valid.sort(Comparator.naturalOrder());
        List<BigDecimal> middle = valid.subList(1, times.size() - 1);

        BigDecimal sum = middle.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(times.size() - 2), 2, RoundingMode.HALF_UP);
    }

    /**
     * Best Average of X (ba3 등) 계산
     * 최저 기록 2개를 제외한 평균
     * DNF/DNS가 3개 이상이면 DNF 처리
     */
    public BigDecimal calculateBAoXAverage(List<BigDecimal> times) {
        List<BigDecimal> valid = new ArrayList<>();
        int penaltyCount = 0;

        for (BigDecimal t : times) {
            if (isPenalty(t)) {
                penaltyCount++;
            } else {
                valid.add(t);
            }
        }

        if (penaltyCount >= 3) {
            return DNF;
        }

        while (valid.size() < times.size()) {
            valid.add(BigDecimal.valueOf(Long.MAX_VALUE));
        }

        valid.sort(Comparator.naturalOrder());
        List<BigDecimal> best = valid.subList(0, times.size() - 2);

        BigDecimal sum = best.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(times.size() - 2), 2, RoundingMode.HALF_UP);
    }

    /**
     * Mean of 3 계산
     * 모든 기록의 평균
     * DNF/DNS가 하나라도 있으면 DNF 처리
     */
    public BigDecimal calculateMo3Average(List<BigDecimal> times) {
        if (times.stream().anyMatch(this::isPenalty)) {
            return DNF;
        }

        BigDecimal sum = times.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(times.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Single (최고 기록) 계산
     * 유효한 기록 중 최소값
     */
    public BigDecimal calculateSingle(List<BigDecimal> times) {
        return times.stream()
                .filter(t -> !isPenalty(t))
                .min(Comparator.naturalOrder())
                .orElse(DNF);
    }

    /**
     * 시간이 페널티(DNF 또는 DNS)인지 확인
     */
    public boolean isPenalty(BigDecimal time) {
        return time != null && time.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * 시간이 비어있거나 0인지 확인
     */
    public boolean isEmpty(BigDecimal time) {
        return time == null || time.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 시간 포맷팅 (mm:ss.SS)
     */
    public String formatTime(BigDecimal seconds) {
        if (seconds == null) {
            return "N/A";
        }
        if (seconds.equals(DNF)) {
            return "DNF";
        }
        if (seconds.equals(DNS)) {
            return "DNS";
        }
        
        int totalMillis = seconds.multiply(BigDecimal.valueOf(100)).intValue();
        int minutes = totalMillis / 6000;
        int secs = (totalMillis % 6000) / 100;
        int millis = totalMillis % 100;

        return String.format("%d:%02d.%02d", minutes, secs, millis);
    }

    public BigDecimal getDNF() {
        return DNF;
    }

    public BigDecimal getDNS() {
        return DNS;
    }
}
