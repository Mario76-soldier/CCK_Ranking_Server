package com.cubingclub.ranking.Round.DTO;

import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Entity.RoundGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoundScheduleDTO {
    private Integer idx;
    private String cubeEvent;
    private String roundName;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private List<RoundGroup> roundGroup;
    private BigDecimal cutoff;
    private BigDecimal timeLimit;
    private Integer advance;

    public RoundScheduleDTO(Round entity){
        this.idx=entity.getIdx();
        this.cubeEvent=entity.getCubeEvent().getEventName();
        this.roundName=entity.getRoundName();
        this.eventStart=entity.getEventStart();
        this.eventEnd=entity.getEventEnd();
        this.roundGroup=entity.getRoundGroup();
        this.cutoff=entity.getCutoff();
        this.timeLimit=entity.getTimeLimit();
        this.advance=entity.getAdvance();
    }
}