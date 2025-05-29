package com.cubingclub.ranking.PersonalRecord.DTO;

import com.cubingclub.ranking.Record.DTO.RecordDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PersonalRecordDTO {
    private Map<String, RecordDTO> bestSingle;
    private Map<String, RecordDTO> bestAverage;
    private Map<String, List<RecordDTO>> recordList;
}