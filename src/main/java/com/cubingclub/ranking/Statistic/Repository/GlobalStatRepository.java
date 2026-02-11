package com.cubingclub.ranking.Statistic.Repository;

import com.cubingclub.ranking.Record.Entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GlobalStatRepository extends JpaRepository<Record, Integer> {

    @Query("""
    select r from Record r
    join fetch r.round ro
    join fetch ro.comp c
    where ro.cubeEvent.eventName = :eventName
    and r.single > 0
    and r.single not in (-1, -2)
    and r.single = (
        select min(r2.single)
        from Record r2
        join r2.round ro2
        where ro2.cubeEvent.eventName = :eventName
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
    limit 10
    """)
    List<Record> findGlobalSingleTop10(@Param("eventName") String eventName);

    @Query("""
    select r from Record r
    join fetch r.round ro
    join fetch ro.comp c
    join fetch ro.cubeEvent ce
    where ro.cubeEvent.eventName = :eventName
    and r.average > 0
    and r.average not in (-1, -2)
    and r.average = (
        select min(r2.average)
        from Record r2
        join r2.round ro2
        where ro2.cubeEvent.eventName = :eventName
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
    limit 10
    """)
    List<Record> findGlobalAverageTop10(@Param("eventName") String eventName);
}
