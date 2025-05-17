package com.cubingclub.ranking.Participate.DTO;

import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.Participate.Entity.Participate;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParticipateRequestDTO {
    private Integer pdx;
    private String email;
    private Integer round;
    private String roundGroup;
    public Participate toEntity(AccountRepository accountRepository, RoundRepository roundRepository){
        return Participate
                .builder()
                .pdx(pdx)
                .account(accountRepository.findByEmail(email).orElseThrow(null))
                .round(roundRepository.findByIdx(round))
                .roundGroup(roundGroup)
                .build();
    }
}
