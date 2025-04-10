package com.cubingclub.ranking.RFID.DTO;

import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Account.Entity.Gender;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.RFID.Entity.RFID;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class RFIDCreateDTO {
    private String email;
    private String uid;
    public RFIDCreateDTO(RFID entity){
        this.email=entity.getAccount().getEmail();
        this.uid=entity.getUid();
    }
    public RFID toEntity(AccountRepository accountRepository){
        return RFID
                .builder()
                .account(accountRepository.findByEmail(email).orElseThrow())
                .uid(uid)
                .build();
    }
}
