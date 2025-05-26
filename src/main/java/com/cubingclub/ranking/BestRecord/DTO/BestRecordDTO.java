package com.cubingclub.ranking.BestRecord.DTO;

import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.BestRecord.Entity.BestRecord;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Record.Entity.Record;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class BestRecordDTO {
    private Integer idx;
    private Account account;
    private CubeEvent cubeEvent;
    private Record single;
    private Record average;
    private Long singleRanking;
    private Long averageRanking;
    private Long stateSingleRanking;
    private Long stateAverageRanking;

    public BestRecordDTO(Account account, Record single, Record average, CubeEvent cubeEvent) {
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
