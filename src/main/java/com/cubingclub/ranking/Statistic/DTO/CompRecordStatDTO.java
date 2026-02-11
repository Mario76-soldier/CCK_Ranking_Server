package com.cubingclub.ranking.Statistic.DTO;

import java.math.BigDecimal;

public interface CompRecordStatDTO {
    String getCompName();
    BigDecimal getBestSingle();
    BigDecimal getBestAverage();
    Double getAvgSingle();
    Double getAvgAverage();
    Long getCount();
}
