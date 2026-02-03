package com.cubingclub.ranking.Round.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoundListDTO {
    private List<RoundDTO> past;
    private List<RoundDTO> now;
    private List<RoundDTO> future;
}
