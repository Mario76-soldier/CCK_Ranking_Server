package com.cubingclub.ranking.Record.DTO;

import com.cubingclub.ranking.Account.Repository.AccountRepository;
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
    private String email;
    private Integer round;
    private String roundGroup;
    public Record toEntity(AccountRepository accountRepository, RoundRepository roundRepository){
        return Record
                .builder()
                .pdx(pdx)
                .account(accountRepository.findByEmail(email).orElseThrow(null))
                .round(roundRepository.findByIdx(round))
                .roundGroup(roundGroup)
                .build();
    }
}
