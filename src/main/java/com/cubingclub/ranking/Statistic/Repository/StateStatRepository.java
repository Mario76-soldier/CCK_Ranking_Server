package com.cubingclub.ranking.Statistic.Repository;

import com.cubingclub.ranking.Statistic.DTO.StateRecordFlatDTO;
import com.cubingclub.ranking.Statistic.DTO.StateStatDTO;
import com.cubingclub.ranking.Record.Entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StateStatRepository extends JpaRepository<Record, Integer> {

    @Query(value = """
    SELECT *
    FROM (
        SELECT x.*,
               ROW_NUMBER() OVER (PARTITION BY x.state ORDER BY x.single) AS rn
        FROM (
            SELECT r.idx AS recordIdx,
                   r.state AS state,
                   r.name AS name,
                   r.cck_id AS cckId,
                   ro.round_name AS roundName,
                   c.comp_name AS compName,
                   ce.event_name AS eventName,
                   r.single AS single,
                   r.average AS average
            FROM record r
            JOIN round ro ON r.round_idx = ro.idx
            JOIN competition c ON ro.comp_idx = c.idx
            JOIN cube_event ce ON ro.cube_event_event_name = ce.event_name
            WHERE ce.event_name = :eventName
              AND r.state IS NOT NULL
              AND r.single > 0
              AND r.single NOT IN (-1, -2)
              AND r.single = (
                  SELECT MIN(r2.single)
                  FROM record r2
                  JOIN round ro2 ON r2.round_idx = ro2.idx
                  WHERE ro2.cube_event_event_name = :eventName
                    AND r2.cck_id = r.cck_id
                    AND r2.single > 0
                    AND r2.single NOT IN (-1, -2)
              )
                AND r.idx = (
                       SELECT MIN(r3.idx)
                       FROM record r3
                       WHERE r3.cck_id = r.cck_id
                         AND r3.single = r.single
                    )
        ) x
    ) t
    WHERE t.rn <= 3
    ORDER BY t.state, t.single
    """, nativeQuery = true)
    List<StateRecordFlatDTO> findStateSingleTop3Flat(@Param("eventName") String eventName);

    @Query(value = """
    SELECT *
    FROM (
        SELECT x.*,
               ROW_NUMBER() OVER (PARTITION BY x.state ORDER BY x.average) AS rn
        FROM (
            SELECT r.idx AS recordIdx,
                   r.state AS state,
                   r.name AS name,
                   r.cck_id AS cckId,
                   ro.round_name AS roundName,
                   c.comp_name AS compName,
                   ce.event_name AS eventName,
                   r.single AS single,
                   r.average AS average
            FROM record r
            JOIN round ro ON r.round_idx = ro.idx
            JOIN competition c ON ro.comp_idx = c.idx
            JOIN cube_event ce ON ro.cube_event_event_name = ce.event_name
            WHERE ce.event_name = :eventName
              AND r.state IS NOT NULL
              AND r.average > 0
              AND r.average NOT IN (-1, -2)
              AND r.average = (
                  SELECT MIN(r2.average)
                  FROM record r2
                  JOIN round ro2 ON r2.round_idx = ro2.idx
                  WHERE ro2.cube_event_event_name = :eventName
                    AND r2.cck_id = r.cck_id
                    AND r2.average > 0
                    AND r2.average NOT IN (-1, -2)
              )
                AND r.idx = (
                       SELECT MIN(r3.idx)
                       FROM record r3
                       WHERE r3.cck_id = r.cck_id
                         AND r3.average = r.average
                    )
        ) x
    ) t
    WHERE t.rn <= 3
    ORDER BY t.state, t.average
    """, nativeQuery = true)
    List<StateRecordFlatDTO> findStateAverageTop3Flat(@Param("eventName") String eventName);

    @Query("""
    select r from Record r
    join fetch r.round ro
    join fetch ro.comp c
    where ro.cubeEvent.eventName = :eventName
    and r.state = :state
    and r.single > 0
    and r.single not in (-1, -2)
    and r.single = (
        select min(r2.single)
        from Record r2
        join r2.round ro2
        where ro2.cubeEvent.eventName = :eventName
        and r2.state = :state
        and r2.cckId = r.cckId
        and r2.single > 0
        and r2.single not in (-1, -2)
    )
    and r.idx = (
            select min(r3.idx)
            from Record r3
            where r3.cckId = r.cckId
            and r3.single = r.single
        )
    order by r.single
    """)
    Page<Record> findStateSingle(@Param("eventName") String eventName, @Param("state") String state, Pageable pageable);

    @Query("""
    select r from Record r
    join fetch r.round ro
    join fetch ro.comp c
    join fetch ro.cubeEvent ce
    where ro.cubeEvent.eventName = :eventName
    and r.state = :state
    and r.average > 0
    and r.average not in (-1, -2)
    and r.average = (
        select min(r2.average)
        from Record r2
        join r2.round ro2
        where ro2.cubeEvent.eventName = :eventName
        and r2.state = :state
        and r2.cckId = r.cckId
        and r2.average > 0
        and r2.average not in (-1, -2)
    )
    and r.idx = (
            select min(r3.idx)
            from Record r3
            where r3.cckId = r.cckId
            and r3.average = r.average
    )
    order by r.average
    """)
    Page<Record> findStateAverage(@Param("eventName") String eventName, @Param("state") String state, Pageable pageable);

    @Query(value = """
    SELECT
        t.state AS state,
        MIN(t.single) AS bestSingle,
        MIN(t.average) AS bestAverage,
        AVG(t.single) AS avgSingle,
        AVG(t.average) AS avgAverage
    FROM (
        SELECT
            r.state,
            r.cck_id,
            r.single,
            r.average,
            ROW_NUMBER() OVER (
                PARTITION BY r.cck_id
                ORDER BY r.single
            ) AS rn
        FROM record r
        JOIN round ro ON r.round_idx = ro.idx
        JOIN cube_event ce ON ro.cube_event_event_name = ce.event_name
        WHERE ce.event_name = :eventName
          AND r.state IS NOT NULL
          AND r.single > 0
          AND r.single NOT IN (-1, -2)
          AND r.average > 0
          AND r.average NOT IN (-1, -2)
    ) t
    WHERE t.rn = 1
    GROUP BY t.state
    ORDER BY t.state
    """, nativeQuery = true)
    List<StateStatDTO> findStateStat(@Param("eventName") String eventName);
}