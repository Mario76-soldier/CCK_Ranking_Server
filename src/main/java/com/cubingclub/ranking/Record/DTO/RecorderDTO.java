package com.cubingclub.ranking.Record.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecorderDTO {
    Integer attemptIdx;
    String judgeCckId;
    BigDecimal record;
}