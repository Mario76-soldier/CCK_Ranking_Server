package com.cubingclub.ranking.Participate.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecorderResponseDTO {
    Integer idx;
    Integer seq;
    String name;
    String state;
    String roundName;
    Integer extraNum;
}