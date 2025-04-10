package com.cubingclub.ranking.Round.DTO;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Round.Entity.Round;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoundDTO {
    private Integer idx;
    private Competition comp;
    private CubeEvent cubeEvent;
    private String roundName;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private Integer advance;
    public RoundDTO(Round entity){
        this.idx=entity.getIdx();
        this.comp=entity.getComp();
        this.cubeEvent=entity.getCubeEvent();
        this.roundName=entity.getRoundName();
        this.eventStart=entity.getEventStart();
        this.eventEnd=entity.getEventEnd();
        this.advance=entity.getAdvance();
    }
    public Round toEntity(){
        return Round.builder()
                .Idx(idx)
                .Comp(comp)
                .cubeEvent(cubeEvent)
                .RoundName(roundName)
                .EventStart(eventStart)
                .EventEnd(eventEnd)
                .Advance(advance)
                .build();
    }
}
