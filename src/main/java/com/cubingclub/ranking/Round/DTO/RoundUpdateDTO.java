package com.cubingclub.ranking.Round.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoundUpdateDTO {
    @NotNull
    private Integer idx;
    @NotNull
    private Integer comp;
    @NotBlank
    private String cubeEvent;
    @NotBlank
    private String roundName;
    @NotBlank
    private LocalDateTime eventStart;
    @NotBlank
    private LocalDateTime eventEnd;
    private List<String> groupList;
    private BigDecimal cutoff;
    private BigDecimal timeLimit;
    private Integer advance;
}
