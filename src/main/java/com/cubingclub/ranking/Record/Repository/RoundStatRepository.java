package com.cubingclub.ranking.Record.Repository;

import com.cubingclub.ranking.Record.DTO.RoundGlobalStatDTO;
import com.cubingclub.ranking.Record.DTO.RoundStateStatDTO;
import com.cubingclub.ranking.Record.Entity.Attempt;

import java.util.List;

public interface RoundStatRepository {
    List<Attempt> findExtraByRoundIdx(Integer roundIdx);
    RoundGlobalStatDTO findSingleAndAverageGlobal(Integer roundIdx);
    List<RoundStateStatDTO> findSingleAndAverageState(Integer roundIdx);
}
