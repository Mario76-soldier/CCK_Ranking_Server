package com.cubingclub.ranking.Competition.DTO;


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
}
