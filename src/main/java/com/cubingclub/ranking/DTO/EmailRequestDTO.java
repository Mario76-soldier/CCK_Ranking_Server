package com.cubingclub.ranking.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EmailRequestDTO {
    @Email
    @NotEmpty
    private String email;
}
