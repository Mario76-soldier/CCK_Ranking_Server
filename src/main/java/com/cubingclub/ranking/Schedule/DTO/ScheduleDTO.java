package com.cubingclub.ranking.Schedule.DTO;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Round.DTO.RoundGroupDTO;
import com.cubingclub.ranking.Schedule.Entity.PlayType;
import com.cubingclub.ranking.Schedule.Entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleDTO {
    private Integer idx;
    private CompetitionDTO competition;
    private AccountDTO accountDTO;
    private RoundGroupDTO roundGroup;
    private PlayType playType;
    public ScheduleDTO(Schedule entity){
        this.idx=entity.getIdx();
        this.competition=new CompetitionDTO(entity.getCompetition());
        this.accountDTO=new AccountDTO(entity.getAccount());
        this.roundGroup=new RoundGroupDTO(entity.getRoundGroup());
        this.playType=entity.getPlayType();
    }
}
