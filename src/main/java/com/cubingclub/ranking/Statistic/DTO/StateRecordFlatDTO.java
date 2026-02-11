package com.cubingclub.ranking.Statistic.DTO;

import java.math.BigDecimal;

public interface StateRecordFlatDTO {
    Integer getRecordIdx();
    String getState();
    String getName();
    String getCckId();
    String getRoundName();
    String getCompName();
    String getEventName();
    BigDecimal getSingle();
    BigDecimal getAverage();
}
