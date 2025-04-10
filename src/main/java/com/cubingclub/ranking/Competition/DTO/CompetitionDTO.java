package com.cubingclub.ranking.Competition.DTO;

import com.cubingclub.ranking.Competition.Entity.Competition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CompetitionDTO {
    private Integer idx;
    private String compName;
    private LocalDate compDateStart;
    private LocalDate compDateEnd;
    private LocalDateTime joinDateStart;
    private LocalDateTime joinDateEnd;
    private Integer maxParticipate;
    public CompetitionDTO(Competition entity){
        this.idx=entity.getIdx();
        this.compName=entity.getCompName();
        this.compDateStart=entity.getCompDateStart();
        this.compDateEnd=entity.getCompDateEnd();
        this.joinDateStart=entity.getJoinDateStart();
        this.joinDateEnd=entity.getJoinDateEnd();
        this.maxParticipate=entity.getMaxParticipate();
    }
    public Competition toEntity(){
        return Competition.builder()
                .Idx(idx)
                .CompName(compName)
                .CompDateStart(compDateStart)
                .CompDateEnd(compDateEnd)
                .JoinDateStart(joinDateStart)
                .JoinDateEnd(joinDateEnd)
                .MaxParticipate(maxParticipate)
                .build();
    }
    public Competition toEntityNoIdx(){
        return Competition.builder()
                .CompName(compName)
                .CompDateStart(compDateStart)
                .CompDateEnd(compDateEnd)
                .JoinDateStart(joinDateStart)
                .JoinDateEnd(joinDateEnd)
                .MaxParticipate(maxParticipate)
                .build();
    }
}
