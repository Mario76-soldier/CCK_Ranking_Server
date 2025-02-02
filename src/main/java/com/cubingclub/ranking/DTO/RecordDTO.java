package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.Account;
import com.cubingclub.ranking.Entity.CubeEvent;
import com.cubingclub.ranking.Entity.Record;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class RecordDTO {
    private Integer idx;
    private Integer partIdx;
    private String userName;
    private String enUserName;
    private String email;
    private CubeEvent cubeEvent;
    private BigDecimal single;
    private BigDecimal average;
    private Long singleRanking;
    private Long averageRanking;
    private Long stateSingleRanking;
    private Long stateAverageRanking;
    public RecordDTO(Record entity){
        this.idx=entity.getIdx();
        this.partIdx=entity.getPartIdx();
        this.userName=entity.getUserName();
        this.enUserName=entity.getEnUserName();
        this.email=entity.getEmail();
        this.cubeEvent=entity.getCubeEvent();
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.singleRanking=entity.getSingleRanking();
        this.averageRanking=entity.getAverageRanking();
        this.stateSingleRanking=entity.getStateSingleRanking();
        this.stateAverageRanking=entity.getStateAverageRanking();
    }
    public Record toEntity(){
        return Record.builder()
                .idx(idx)
                .partIdx(partIdx)
                .userName(userName)
                .enUserName(enUserName)
                .email(email)
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
