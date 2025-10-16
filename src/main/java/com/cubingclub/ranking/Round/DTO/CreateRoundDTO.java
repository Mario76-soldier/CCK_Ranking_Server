package com.cubingclub.ranking.Round.DTO;

import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateRoundDTO {
    private Integer idx;
    private Integer comp;
    private String cubeEvent;
    private String roundName;
    private LocalDateTime eventStart;
    private LocalDateTime eventEnd;
    private List<String> groupList;
    private BigDecimal cutoff;
    private BigDecimal timeLimit;
    private Integer advance;
    public Round toEntity(CompetitionRepository competitionRepository, CubeEventRepository cubeEventRepository){
        return Round.builder()
                .Idx(idx)
                .Comp(competitionRepository.findByIdx(comp))
                .cubeEvent(cubeEventRepository.findByEventName(cubeEvent))
                .RoundName(roundName)
                .EventStart(eventStart)
                .EventEnd(eventEnd)
                .Advance(advance)
                .cutoff(cutoff)
                .timeLimit(timeLimit)
                .build();
    }
}
