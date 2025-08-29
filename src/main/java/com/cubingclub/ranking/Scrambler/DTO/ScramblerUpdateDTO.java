package com.cubingclub.ranking.Scrambler.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScramblerUpdateDTO {
    Integer compIdx;
    String playerUid;
    String scramblerUid;
}
