package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.Account;
import com.cubingclub.ranking.Entity.BestRecord;
import com.cubingclub.ranking.Entity.CubeEvent;
import com.cubingclub.ranking.Entity.Participate;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BestRecordDTO {
    private Integer idx;
    private Account account;
    private CubeEvent cubeEvent;
    private Participate single;
    private Participate average;
    private Long singleRanking;
    private Long averageRanking;
    private Long stateSingleRanking;
    private Long stateAverageRanking;

    public BestRecordDTO(Account account, Participate single, Participate average, CubeEvent cubeEvent) {
        this.account=account;
        this.cubeEvent=cubeEvent;
        this.single=single;
        this.average=average;
    }
    public BestRecordDTO(BestRecord entity){
        this.idx=entity.getIdx();
        this.account=entity.getAccount();
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
