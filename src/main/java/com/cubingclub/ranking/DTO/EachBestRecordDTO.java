package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.BestRecord;
import com.cubingclub.ranking.Entity.CubeEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EachBestRecordDTO {
    private CubeEvent cubeEvent;
    private BigDecimal single;
    private BigDecimal average;
    private Long singleRanking;
    private Long averageRanking;
    private Long stateSingleRanking;
    private Long stateAverageRanking;
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
