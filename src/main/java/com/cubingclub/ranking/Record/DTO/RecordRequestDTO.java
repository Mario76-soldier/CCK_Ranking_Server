package com.cubingclub.ranking.Record.DTO;

import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecordRequestDTO {
    private Integer pdx;
    private String name;
    private String state;
    private String cckId;
    private Integer round;
    private String roundGroup;
    private String note;
    public Record toEntity(RoundRepository roundRepository){
        return Record
                .builder()
                .pdx(pdx)
                .name(name)
                .state(state)
                .cckId(cckId)
                .round(roundRepository.findByIdx(round))
                .roundGroup(roundGroup)
                .note(note)
                .build();
    }
}
