package com.cubingclub.ranking.Participate.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SingleAverageDTO {
    BigDecimal single;
    BigDecimal average;
}
