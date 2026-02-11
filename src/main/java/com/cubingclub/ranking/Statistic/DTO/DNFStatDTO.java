package com.cubingclub.ranking.Statistic.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DNFStatDTO {
    private Long dnfCount;
    private Long successCount;
    private Long totalCount;
}
