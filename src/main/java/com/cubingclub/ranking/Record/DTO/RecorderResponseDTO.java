package com.cubingclub.ranking.Record.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecorderResponseDTO {
    Integer attemptIdx;
    Integer seq;
    String name;
    String enName;
    String state;
    String roundName;
    Integer extraNum;
}