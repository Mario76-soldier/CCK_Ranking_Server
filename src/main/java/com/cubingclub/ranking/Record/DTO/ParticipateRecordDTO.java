package com.cubingclub.ranking.Record.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ParticipateRecordDTO {
    Integer idx;
    List<BigDecimal> times;
}
