package com.cubingclub.ranking.Account.DTO;

import com.cubingclub.ranking.Account.Entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    String email;
    String name;
    String enName;
    UserRole role;
    String token;
}
