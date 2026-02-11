package com.cubingclub.ranking.Record.DTO;

import java.math.BigDecimal;

public interface StateStatDTO {
    String getState();
    BigDecimal getBestSingle();
    BigDecimal getBestAverage();
    BigDecimal getAvgSingle();
    BigDecimal getAvgAverage();
}
