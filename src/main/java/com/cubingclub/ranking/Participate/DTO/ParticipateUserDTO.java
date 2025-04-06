package com.cubingclub.ranking.Participate.DTO;

import com.cubingclub.ranking.Participate.Entity.BestType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipateUserDTO {
    private Integer idx;
    private Integer pdx;
    private String email;
    private String name;
    private String enName;
    private String state;
    private BigDecimal first;
    private BigDecimal second;
    private BigDecimal third;
    private BigDecimal fourth;
    private BigDecimal fifth;
    private BigDecimal single;
    private BigDecimal average;
    private Integer ranking;
    private Integer stateRanking;
    private BestType singleBestType;
    private BestType averageBestType;
}
