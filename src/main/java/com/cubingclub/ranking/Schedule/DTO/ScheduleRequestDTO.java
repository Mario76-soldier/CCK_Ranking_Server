package com.cubingclub.ranking.Schedule.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleRequestDTO {
    private Integer compIdx;
    private List<SchedulePartDTO> groups;
}
