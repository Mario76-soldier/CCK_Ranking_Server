package com.cubingclub.ranking.Round.DTO;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Round.Entity.Round;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoundCreateDTO {
    @NotNull
    private Integer comp;
    @NotBlank
    private String cubeEvent;
    @Size(max=30)
    @NotBlank
    private String roundName;
    @NotNull
    private LocalDateTime eventStart;
    @NotNull
    private LocalDateTime eventEnd;
    private List<String> groupList;
    private BigDecimal cutoff;
    private BigDecimal timeLimit;
    private Integer advance;
    public Round toEntity(Competition competition, CubeEvent cubeEvent){
        return Round.builder()
                .comp(competition)
                .cubeEvent(cubeEvent)
                .roundName(roundName)
                .eventStart(eventStart)
                .eventEnd(eventEnd)
                .advance(advance)
                .cutoff(cutoff)
                .timeLimit(timeLimit)
                .build();
    }
}
