package com.cubingclub.ranking.Record.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecorderDTO {
    Integer idx;
    String judgeEmail;
    BigDecimal record;
}