package com.cubingclub.ranking.Schedule.DTO;

import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Round.Entity.RoundGroup;
import com.cubingclub.ranking.Schedule.Entity.PlayType;
import com.cubingclub.ranking.Schedule.Entity.Schedule;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleCreateDTO {
    private Integer idx;
    private Competition competition;
    private Account account;
    private RoundGroup roundGroup;
    private PlayType playType;
    public Schedule toCreateEntity(){
        return Schedule.builder()
                .competition(competition)
                .account(account)
                .roundGroup(roundGroup)
                .playType(playType)
                .build();
    }
    public Schedule toUpdateEntity(){
        return Schedule.builder()
                .idx(idx)
                .competition(competition)
                .account(account)
                .roundGroup(roundGroup)
                .playType(playType)
                .build();
    }
}
