package com.cubingclub.ranking.Statistic.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoundStateStatDTO {
    private String state;
    private Double singleAvg;
    private Double averageAvg;
    private Long count;
}
