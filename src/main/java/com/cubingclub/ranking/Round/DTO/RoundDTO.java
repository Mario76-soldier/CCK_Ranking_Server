package com.cubingclub.ranking.Round.DTO;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Entity.RoundGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<RoundGroup> roundGroup;
    private Integer advance;
    public RoundDTO(Round entity){
        this.idx=entity.getIdx();
        this.comp=entity.getComp();
        this.cubeEvent=entity.getCubeEvent();
        this.roundName=entity.getRoundName();
        this.eventStart=entity.getEventStart();
        this.eventEnd=entity.getEventEnd();
        this.roundGroup=entity.getRoundGroup();
        this.advance=entity.getAdvance();
    }
    public Round toEntity(List<RoundGroup> roundGroup){
        return Round.builder()
                .Idx(idx)
                .Comp(comp)
                .cubeEvent(cubeEvent)
                .RoundName(roundName)
                .EventStart(eventStart)
                .EventEnd(eventEnd)
                .roundGroup(roundGroup)
                .Advance(advance)
                .build();
    }
}
