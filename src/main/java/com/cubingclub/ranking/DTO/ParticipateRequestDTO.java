package com.cubingclub.ranking.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ParticipateRequestDTO {
    private Integer pdx;
    private String email;
    private Integer round;
}
