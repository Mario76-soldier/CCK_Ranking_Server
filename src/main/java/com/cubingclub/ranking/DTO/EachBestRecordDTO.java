package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.BestRecord;
import com.cubingclub.ranking.Entity.CubeEvent;
import com.cubingclub.ranking.Entity.Participate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EachBestRecordDTO {
    private CubeEvent cubeEvent;
    private Participate single;
    private Participate average;
    private Long singleRanking;
    private Long averageRanking;
    private Long allPart;
    private Long stateSingleRanking;
    private Long stateAverageRanking;
    private Long stateAllPart;
    public EachBestRecordDTO(BestRecord entity){
        this.cubeEvent=entity.getCubeEvent();
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.singleRanking=entity.getSingleRanking();
        this.averageRanking=entity.getAverageRanking();
        this.stateSingleRanking=entity.getStateSingleRanking();
        this.stateAverageRanking=entity.getStateAverageRanking();
    }
}
