package com.cubingclub.ranking.Round.Repository;

import com.cubingclub.ranking.Round.Entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoundRepository extends JpaRepository<Round, Integer> {
    @Query("select r from Round r join fetch r.comp join fetch r.cubeEvent where r.idx = :idx")
    Optional<Round> findByIdx(@Param("idx") Integer idx);

    @Query(value = """
        select r.*
        from round r
        join competition c on r.comp_idx = c.idx
        join cube_event e on r.cube_event_event_name = e.event_name
        where c.idx = :compIdx
        and r.event_start >= timestampadd(DAY, :dayCount, c.comp_date_start)
        and r.event_end < :now
        order by r.event_start desc
        """, nativeQuery = true
    )
    List<Round> findPastRoundsByCompIdx(@Param("compIdx") Integer compIdx, @Param("dayCount") Integer dayCount, @Param("now") LocalDateTime now);

    @Query(value = """
        select r.*
        from round r
        join competition c on r.comp_idx = c.idx
        join cube_event e on r.cube_event_event_name = e.event_name
        where c.idx = :compIdx
        and r.event_start >= timestampadd(DAY, :dayCount, c.comp_date_start)
        and r.event_start <= :now
        and r.event_end >= :now
        order by r.event_start asc
        """, nativeQuery = true
    )
    List<Round> findPresentRoundsByCompIdx(@Param("compIdx") Integer compIdx, @Param("dayCount") Integer dayCount, @Param("now") LocalDateTime now);

    @Query(value = """
        select r.*
        from round r
        join competition c on r.comp_idx = c.idx
        join cube_event e on r.cube_event_event_name = e.event_name
        where c.idx = :compIdx
        and r.event_start >= timestampadd(DAY, :dayCount, c.comp_date_start)
        and r.event_start > :now
        order by r.event_start asc
        """, nativeQuery = true
    )
    List<Round> findFutureRoundsByCompIdx(@Param("compIdx") Integer compIdx, @Param("dayCount") Integer dayCount, @Param("now") LocalDateTime now);
}
