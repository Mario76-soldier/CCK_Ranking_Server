package com.cubingclub.ranking.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecorderDTO {
    Integer idx;
    String judgeEmail;
    Integer seq;
    BigDecimal record;
}
