package com.cubingclub.ranking.Record.DTO;

import com.cubingclub.ranking.Record.Entity.BestType;
import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Round.Entity.Round;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class RecordDTO {
    private Integer idx;
    private Integer pdx;
    private String name;
    private String state;
    private String cckId;
    private Integer roundIdx;
    private String roundName;
    private String roundGroup;
    private Integer roundAdvance;
    private Integer compIdx;
    private String compName;
    private String eventName;
    private String avgCalc;
    private List<AttemptDTO> attempts;
    private BigDecimal single;
    private BigDecimal average;
    private Integer ranking;
    private Integer stateRanking;
    private BestType singleBestType;
    private BestType averageBestType;
    private String note;

    public RecordDTO(Record entity){
        this.idx=entity.getIdx();
        this.pdx=entity.getPdx();
        this.name=entity.getName();
        this.state=entity.getState();
        this.cckId=entity.getCckId();
        this.roundIdx=entity.getRound().getIdx();
        this.roundName=entity.getRound().getRoundName();
        this.roundGroup=entity.getRoundGroup();
        this.roundAdvance=entity.getRound().getAdvance();
        this.compIdx=entity.getRound().getComp().getIdx();
        this.compName=entity.getRound().getComp().getCompName();
        this.eventName=entity.getRound().getCubeEvent().getEventName();
        this.avgCalc=entity.getRound().getCubeEvent().getAvgCalc();
        this.attempts=entity.getAttempts().stream().map(AttemptDTO::new).toList();
        this.single=entity.getSingle();
        this.average=entity.getAverage();
        this.ranking=entity.getRanking();
        this.stateRanking=entity.getStateRanking();
        this.singleBestType=entity.getSingleBestType();
        this.averageBestType=entity.getAverageBestType();
        this.note=entity.getNote();
    }

    public Record toEntity(Round round){
        return Record.builder()
                .pdx(pdx)
                .name(name)
                .state(state)
                .cckId(cckId)
                .round(round)
                .roundGroup(roundGroup)
                .note(note)
                .build();
    }
}
