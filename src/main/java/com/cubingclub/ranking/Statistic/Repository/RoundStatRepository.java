package com.cubingclub.ranking.Statistic.Repository;

import com.cubingclub.ranking.Statistic.DTO.DNFStatDTO;
import com.cubingclub.ranking.Statistic.DTO.RoundGlobalStatDTO;
import com.cubingclub.ranking.Statistic.DTO.RoundStateStatDTO;
import com.cubingclub.ranking.Record.Entity.Attempt;

import java.util.List;

public interface RoundStatRepository {
    List<Attempt> findExtraByRoundIdx(Integer roundIdx);
    RoundGlobalStatDTO findSingleAndAverageGlobal(Integer roundIdx);
    List<RoundStateStatDTO> findSingleAndAverageState(Integer roundIdx);
    DNFStatDTO findDNFStatByRoundIdx(Integer roundIdx);
}
