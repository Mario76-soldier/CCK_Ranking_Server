package com.cubingclub.ranking.Record.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StateRecordDTO {
    private Integer recordIdx;
    private String state;
    private String name;
    private String cckId;
    private String roundName;
    private String compName;
    private String eventName;
    private BigDecimal single;
    private BigDecimal average;
    private List<AttemptDTO> attempts;
}

