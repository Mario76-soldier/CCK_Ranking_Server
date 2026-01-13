package com.cubingclub.ranking.Competition.DTO;

import com.cubingclub.ranking.Competition.Entity.Competition;
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
public class CompetitionCreateDTO {
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<EventFeeDTO> eventFeeList;
    public Competition toEntity(){
        return Competition.builder()
                .CompName(compName)
                .CompDateStart(compDateStart)
                .CompDateEnd(compDateEnd)
                .JoinDateStart(joinDateStart)
                .JoinDateEnd(joinDateEnd)
                .baseFee(baseFee)
                .location(location)
                .MaxParticipate(maxParticipate)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
