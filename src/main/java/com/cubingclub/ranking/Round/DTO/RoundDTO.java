package com.cubingclub.ranking.Round.DTO;

import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Entity.RoundGroup;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoundDTO {
    private Integer idx;
    private CompetitionDTO comp; // 엔티티 대신 DTO 사용
    private CubeEvent cubeEvent;
    private String roundName;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private List<RoundGroup> roundGroup;
    private BigDecimal cutoff;
    private BigDecimal timeLimit;
    private Integer advance;

    public RoundDTO(Round entity){
        this.idx = entity.getIdx();
        this.comp = new CompetitionDTO(entity.getComp()); // DTO 변환
        this.cubeEvent = entity.getCubeEvent();
        this.roundName = entity.getRoundName();
        this.eventStart = entity.getEventStart();
        this.eventEnd = entity.getEventEnd();
        this.roundGroup = entity.getRoundGroup();
        this.cutoff=entity.getCutoff();
        this.timeLimit=entity.getTimeLimit();
        this.advance = entity.getAdvance();
    }

    public Round toEntity(List<RoundGroup> roundGroup){
        return Round.builder()
                .Idx(idx)
                .Comp(comp.toEntity()) // DTO → 엔티티 변환
                .cubeEvent(cubeEvent)
                .RoundName(roundName)
                .EventStart(eventStart)
                .EventEnd(eventEnd)
                .roundGroup(roundGroup)
                .cutoff(cutoff)
                .timeLimit(timeLimit)
                .Advance(advance)
                .build();
    }
}

