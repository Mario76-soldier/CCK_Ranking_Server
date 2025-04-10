package com.cubingclub.ranking.Account.DTO;

import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Account.Entity.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MaskedAccountDTO {
    private String name;
    private String enName;
    private String state;
    private Gender gender;
    public MaskedAccountDTO(Account entity){
        this.name=entity.getName();
        this.enName=entity.getEnName();
        this.state=entity.getState();
        this.gender=entity.getGender();
    }
}
