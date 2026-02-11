package com.cubingclub.ranking.Statistic.Repository;

import com.cubingclub.ranking.Statistic.DTO.StaffStatDTO;
import com.cubingclub.ranking.Record.Entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompStatRepository extends JpaRepository<Attempt, Integer> {
    @Query("select a from Attempt a where a.extra is not null and a.record.round.comp.idx=:compIdx")
    List<Attempt> findExtraByCompIdx(@Param("compIdx") Integer compIdx);

    @Query("select a.judgeName staffName, a.judgeCckId staffCckId, count(a.judgeCckId) count from Attempt a where a.record.round.comp.idx=:compIdx group by a.judgeCckId, a.judgeName having count(a.judgeCckId)>0 order by count desc")
    List<StaffStatDTO> findJudgeStatByCompIdx(@Param("compIdx") Integer compIdx);

    @Query("select a.scramblerName staffName, a.scramblerCckId staffCckId, count(a.scramblerCckId) count from Attempt a where a.record.round.comp.idx=:compIdx group by a.scramblerCckId, a.scramblerName having count(a.scramblerCckId)>0 order by count desc")
    List<StaffStatDTO> findScramblerStatByCompIdx(@Param("compIdx") Integer compIdx);
}