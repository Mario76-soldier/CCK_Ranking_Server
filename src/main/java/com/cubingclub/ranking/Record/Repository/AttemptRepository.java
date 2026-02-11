package com.cubingclub.ranking.Record.Repository;

import com.cubingclub.ranking.Record.DTO.AttemptDTO;
import com.cubingclub.ranking.Record.Entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Integer> {
    Attempt findByIdx(Integer idx);
    Attempt findByRecord_IdxAndExtra(Integer recordIdx, Integer extra);

    @Query("""
    select new com.cubingclub.ranking.Record.DTO.AttemptDTO(
        a.idx,
        a.record.idx,
        a.attemptTry,
        a.time,
        a.scramblerName,
        a.scramblerCckId,
        a.judgeName,
        a.judgeCckId,
        a.extra,
        a.exReason,
        a.delegateName,
        a.delegateCckId,
        a.exScramblerName,
        a.exScramblerCckId
    )
    from Attempt a
    where a.record.idx in :recordIdxList
    order by a.record.idx, a.attemptTry
    """)
    List<AttemptDTO> findAttemptDTOByRecordIdxList(@Param("recordIdxList") List<Integer> recordIdxList);
}
