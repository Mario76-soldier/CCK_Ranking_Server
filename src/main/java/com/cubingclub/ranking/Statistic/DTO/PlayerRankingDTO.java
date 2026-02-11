package com.cubingclub.ranking.Statistic.DTO;

import java.math.BigDecimal;

public interface PlayerRankingDTO {
    String getEventName();
    BigDecimal getBestSingle();
    Long getRankSingle();
    BigDecimal getBestAverage();
    Long getRankAverage();
    Long getTotal();
}
