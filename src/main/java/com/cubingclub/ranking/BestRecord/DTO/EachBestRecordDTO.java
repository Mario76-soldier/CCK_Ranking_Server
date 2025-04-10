package com.cubingclub.ranking.BestRecord.DTO;

import com.cubingclub.ranking.BestRecord.Entity.BestRecord;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Participate.Entity.Participate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
