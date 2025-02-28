package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.Account;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class MaskedAccountDTO {
    private String name;
    private String enName;
    private String state;
    private String sex;
    public MaskedAccountDTO(Account entity){
        this.name=entity.getName();
        this.enName=entity.getEnName();
        this.state=entity.getState();
        this.sex=entity.getSex();
    }
}
