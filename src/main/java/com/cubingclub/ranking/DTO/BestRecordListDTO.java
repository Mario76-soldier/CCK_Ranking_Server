package com.cubingclub.ranking.DTO;

import com.cubingclub.ranking.Entity.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BestRecordListDTO {
    private Account account;
    private List<EachBestRecordDTO> eachRecordList;
}
