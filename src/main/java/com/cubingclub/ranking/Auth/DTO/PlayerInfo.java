package com.cubingclub.ranking.Auth.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerInfo {
    private String name;
    private String enName;
    private String state;
    private String cckId;
    private String wcaId;
    private Gender gender;
    private UserRole position;
}
