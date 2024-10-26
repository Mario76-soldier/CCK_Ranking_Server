package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class AccountListDTO {
    @NotBlank
    private String email;
    private String name;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;
    private String state;
    private String sex;
    private String phoneNum;
    private UserRole position;
}
