package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.Account;
import com.cubingclub.ranking.Entity.BestRecord;
import com.cubingclub.ranking.Entity.CubeEvent;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BestRecordDTO {
    private Integer idx;
    private Account account;
    private Integer partSingle;
    private Integer partAverage;
    private CubeEvent cubeEvent;
    private BigDecimal single;
    private BigDecimal average;
    private Long singleRanking;
    private Long averageRanking;
    private Long stateSingleRanking;
    private Long stateAverageRanking;

    public BestRecordDTO(Account account, Integer partSingle, Integer partAverage, CubeEvent cubeEvent, BigDecimal single, BigDecimal average) {
        this.account=account;
        this.partSingle=partSingle;
        this.partAverage=partAverage;
        this.cubeEvent=cubeEvent;
        this.single=single;
        this.average=average;
    }
    public BestRecordDTO(BestRecord entity){
        this.idx=entity.getIdx();
        this.account=entity.getAccount();
        this.partSingle=entity.getPartSingle();
        this.partAverage=entity.getPartAverage();
        this.cubeEvent=entity.getCubeEvent();
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.singleRanking=entity.getSingleRanking();
        this.averageRanking=entity.getAverageRanking();
        this.stateSingleRanking=entity.getStateSingleRanking();
        this.stateAverageRanking=entity.getStateAverageRanking();
    }
    public BestRecord toEntity(){
        return BestRecord.builder()
                .idx(idx)
                .account(account)
                .partSingle(partSingle)
                .partAverage(partAverage)
                .cubeEvent(cubeEvent)
                .single(single)
                .average(average)
                .singleRanking(singleRanking)
                .averageRanking(averageRanking)
                .stateSingleRanking(stateSingleRanking)
                .stateAverageRanking(stateAverageRanking)
                .build();

    }
}
