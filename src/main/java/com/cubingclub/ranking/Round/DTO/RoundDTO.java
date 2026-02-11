package com.cubingclub.ranking.Round.DTO;

import com.cubingclub.ranking.Round.Entity.Round;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class RoundDTO {
    private Integer idx;
    private Integer compIdx;
    private String compName;
    private String cubeEventName;
    private String roundName;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private BigDecimal cutoff;
    private BigDecimal timeLimit;
    private Integer advance;
    private List<String> roundGroupList;


    public RoundDTO(Round entity){
        this.idx=entity.getIdx();
        this.compIdx=entity.getComp().getIdx();
        this.compName=entity.getComp().getCompName();
        this.cubeEventName=entity.getCubeEvent().getEventName();
        this.roundName=entity.getRoundName();
        this.eventStart=entity.getEventStart();
        this.eventEnd=entity.getEventEnd();
        this.cutoff=entity.getCutoff();
        this.timeLimit=entity.getTimeLimit();
        this.advance=entity.getAdvance();
        this.roundGroupList=new ArrayList<>(entity.getGroupList());
    }
}

