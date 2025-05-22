package com.cubingclub.ranking.Account.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AccountEditDTO {
    private String email;
    private String password;
    private String newPassword;
    private String newPasswordCheck;
    private String state;
}
