package com.cubingclub.ranking.BestRecord.DTO;

import com.cubingclub.ranking.Account.DTO.MaskedAccountDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BestRecordListDTO {
    private MaskedAccountDTO account;
    private List<EachBestRecordDTO> eachRecordList;
}
