package com.cubingclub.ranking.Statistic.Repository;

import com.cubingclub.ranking.Record.Entity.Record;
import com.cubingclub.ranking.Statistic.DTO.CompRecordStatDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    """)
    Page<Record> findGlobalSingle(@Param("eventName") String eventName, Pageable pageable);

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
    """)
    Page<Record> findGlobalAverage(@Param("eventName") String eventName, Pageable pageable);

    @Query("select c.compName compName, min(r.single) bestSingle, min(r.average) bestAverage, avg(r.single) avgSingle, avg(r.average) avgAverage, count(r.idx) count " +
            "from Record r join r.round ro join ro.comp c where r.single>0 and r.average>0 and ro.cubeEvent.eventName=:eventName " +
            "group by c.compName, c.compDateStart order by c.compDateStart")
    List<CompRecordStatDTO> findGlobalRecordStatByCompetition(String eventName);
}
