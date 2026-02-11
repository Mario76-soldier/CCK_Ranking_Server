package com.cubingclub.ranking.Record.Repository;

import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Record.DTO.PlayerRankingDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlayerRankingRepository extends JpaRepository<Record, Integer> {
    @Query(value = """
    select eventName, bestSingle, rankSingle, bestAverage, rankAverage, total
    from (
        select ro.cube_event_event_name as eventName,
            r.cck_id as cckId,
            min(r.single)  as bestSingle,
            rank() over(partition by ro.cube_event_event_name order by min(r.single))  as rankSingle,
            min(r.average) as bestAverage,
            rank() over(partition by ro.cube_event_event_name order by min(r.average)) as rankAverage,
            count(*) over(partition by ro.cube_event_event_name) as total
        from record r
        join round ro on r.round_idx = ro.idx
        where r.single > 0
          and r.average > 0
        group by ro.cube_event_event_name, r.cck_id
    ) t
    where t.cckId = :cckId
    order by t.eventName""", nativeQuery = true)
    List<PlayerRankingDTO> findPlayerRankingByCckId(@Param("cckId") String cckId);
}
