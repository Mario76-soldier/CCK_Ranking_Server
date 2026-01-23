package com.cubingclub.ranking.Competition.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompetitionUpdateDTO {
    @NotNull
    private Integer idx;
    @NotBlank
    private String compName;
    @NotNull
    private LocalDate compDateStart;
    @NotNull
    private LocalDate compDateEnd;
    @NotNull
    private LocalDateTime joinDateStart;
    @NotNull
    private LocalDateTime joinDateEnd;
    private Integer baseFee;
    private String location;
    private Integer maxParticipate;
    private List<EventFeeDTO> eventFeeList;
}
