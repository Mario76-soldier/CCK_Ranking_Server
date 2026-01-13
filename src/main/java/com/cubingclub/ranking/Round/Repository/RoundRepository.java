package com.cubingclub.ranking.Round.Repository;

import com.cubingclub.ranking.Round.Entity.Round;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RoundRepository extends JpaRepository<Round, Integer> {
    @Query("select r from Round r join fetch r.comp join fetch r.cubeEvent where r.idx = :idx")
    Round findByIdx(@Param("idx") Integer idx);

    @Query("select r from Round r join fetch r.comp c join fetch r.cubeEvent e where c.idx = :compIdx and r.eventEnd < :now order by r.eventStart desc")
    Page<Round> findPastRoundsByCompIdx(Pageable pageable, @Param("compIdx") Integer compIdx, @Param("now") LocalDateTime now);

    @Query("select r from Round r join fetch r.comp c join fetch r.cubeEvent e where c.idx = :compIdx and r.eventStart <= :now and r.eventEnd >= :now order by r.eventStart asc")
    Page<Round> findPresentRoundsByCompIdx(Pageable pageable, @Param("compIdx") Integer compIdx, @Param("now") LocalDateTime now);

    @Query("select r from Round r join fetch r.comp c join fetch r.cubeEvent e where c.idx = :compIdx and r.eventStart > :now order by r.eventStart asc")
    Page<Round> findFutureRoundsByCompIdx(Pageable pageable, @Param("compIdx") Integer compIdx, @Param("now") LocalDateTime now);
}
