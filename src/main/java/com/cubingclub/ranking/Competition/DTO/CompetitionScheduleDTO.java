package com.cubingclub.ranking.Competition.DTO;


import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Round.DTO.RoundScheduleDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompetitionScheduleDTO {
    private Integer idx;
    private String compName;
    private LocalDate compDateStart;
    private LocalDate compDateEnd;
    private List<RoundScheduleDTO> roundSchedules;
    public CompetitionScheduleDTO(Competition entity, List<RoundScheduleDTO> roundSchedules){
        this.idx=entity.getIdx();
        this.compName=entity.getCompName();
        this.compDateStart=entity.getCompDateStart();
        this.compDateEnd=entity.getCompDateEnd();
        this.roundSchedules=roundSchedules;
    }
}
