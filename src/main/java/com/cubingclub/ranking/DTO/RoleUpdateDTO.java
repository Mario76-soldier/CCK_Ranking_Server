package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.UserRole;
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
