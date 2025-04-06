package com.cubingclub.ranking.Account.DTO;

import com.cubingclub.ranking.Account.Entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleUpdateDTO {
    @NotBlank
    private String email;
    private UserRole position;
}
