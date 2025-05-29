package com.cubingclub.ranking.Schedule.DTO;

import com.cubingclub.ranking.Schedule.Entity.PlayType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SchedulePartDTO {
    private Integer idx;
    private String cckId;
    private Integer roundIdx;
    private String groupName;
    private PlayType playType;
}
