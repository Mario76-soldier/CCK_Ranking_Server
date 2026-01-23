package com.cubingclub.ranking.Record.DTO;

import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Round.Entity.Round;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PlayerAddDTO {
    private Integer pdx;
    private String name;
    private String state;
    private String cckId;
    private Integer roundIdx;
    private String roundGroup;
    private String note;

    public Record toEntity(Round round){
        return Record
                .builder()
                .pdx(pdx)
                .name(name)
                .state(state)
                .cckId(cckId)
                .round(round)
                .roundGroup(roundGroup)
                .note(note)
                .build();
    }
}
