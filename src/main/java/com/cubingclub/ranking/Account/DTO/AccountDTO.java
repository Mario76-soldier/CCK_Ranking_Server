package com.cubingclub.ranking.Account.DTO;

import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Account.Entity.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AccountDTO {
    @NotBlank
    private String email;
    private String name;
    private String enName;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;
    private String state;
    private Gender gender;
    private String phoneNum;
    public AccountDTO(Account entity){
        this.email=entity.getEmail();
        this.name=entity.getName();
        this.enName=entity.getEnName();
        this.birthDate=entity.getBirthDate();
        this.state=entity.getState();
        this.gender=entity.getGender();
        this.phoneNum=entity.getPhoneNum();
    }
    public static AccountDTO from(Account entity) {
        if (entity == null) {
            return null;
        }
        return new AccountDTO(entity);
    }
}
