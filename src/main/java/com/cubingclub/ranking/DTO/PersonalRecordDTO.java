package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Entity.Round;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
public class PersonalRecordDTO {
    private Round round;
    private BigDecimal first;
    private BigDecimal second;
    private BigDecimal third;
    private BigDecimal fourth;
    private BigDecimal fifth;
    private BigDecimal single;
    private BigDecimal average;
    private Integer ranking;
    private Integer rankingAll;
    private Integer stateRanking;
    private Integer stateRankingAll;
    public PersonalRecordDTO(Participate entity){
        this.round=entity.getRound();
        this.first=entity.getFirst();
        this.second=entity.getSecond();
        this.third=entity.getThird();
        this.fourth=entity.getFourth();
        this.fifth=entity.getFifth();
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.ranking=entity.getRanking();
        this.stateRanking=entity.getStateRanking();
    }
}
