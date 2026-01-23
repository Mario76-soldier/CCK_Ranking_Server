package com.cubingclub.ranking.Scrambler.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrambleCreateDTO {
    private Integer roundIdx;
    private String roundGroup;
    private String seq;
    private String scramble;
    private String svg;
}