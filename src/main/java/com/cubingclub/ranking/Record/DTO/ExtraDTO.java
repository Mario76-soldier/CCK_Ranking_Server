package com.cubingclub.ranking.Record.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ExtraDTO {
    Integer recordIdx;
    Integer extraNum;
    Integer extraTry;
    String extraReason;
}
