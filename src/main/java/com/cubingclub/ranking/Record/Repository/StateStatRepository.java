package com.cubingclub.ranking.Record.Repository;

import com.cubingclub.ranking.Record.DTO.StateRecordFlatDTO;
import com.cubingclub.ranking.Record.DTO.StateStatDTO;
import com.cubingclub.ranking.Record.Entity.Record;
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