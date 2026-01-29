package com.cubingclub.ranking.Record.Repository;

import com.cubingclub.ranking.Record.DTO.BestResult;
import com.cubingclub.ranking.Record.Entity.Record;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Integer> {
    @Query("select p from Record p " +
            "join fetch p.round r " +
            "join fetch r.cubeEvent ce " +
            "join fetch r.comp c " +
            "where r.idx=:roundIdx " +
            "order by (case when p.ranking is null or p.ranking=0 then 1 else 0 end) asc, p.ranking asc")
    List<Record> findByRound_Idx(@Param(value="roundIdx")Integer roundIdx);

    @Query("select p from Record p " +
            "join fetch p.round r " +
            "join fetch r.cubeEvent ce " +
            "join fetch r.comp c " +
            "where r.idx=:roundIdx and p.single is not null and p.average is not null and p.single!=0 " +
            "and p.average!=0 order by (case when p.ranking is null or p.ranking=0 then 1 else 0 end) asc, p.ranking asc")
    List<Record> findByRound_IdxForBestType(@Param(value="roundIdx")Integer roundIdx);

    @Modifying
    @Query(value = "UPDATE record r JOIN (" +
            "SELECT idx, RANK() OVER (" +
            "ORDER BY " +
            "(CASE WHEN average = -2 THEN 2 WHEN average = -1 THEN 1 ELSE 0 END), average, " +
            "(CASE WHEN single = -2 THEN 2 WHEN single = -1 THEN 1 ELSE 0 END), single" +
            ") AS new_rank " +
            "FROM record " +
            "WHERE round_idx = :roundIdx AND average IS NOT NULL AND average != 0" +
            ") ranked ON r.idx = ranked.idx " +
            "SET r.ranking = ranked.new_rank " +
            "WHERE r.round_idx = :roundIdx AND r.average IS NOT NULL AND r.average != 0",
            nativeQuery = true)
    void setRankOnRound(@Param("roundIdx") Integer roundIdx);

    @Query("""
    select p.idx as idx,
           case when :type = 'SINGLE' then p.single else p.average end as best
    from Record p
    join p.round r
    join r.cubeEvent ce
    where ce.eventName = :eventName
      and (:cckId is null or p.cckId = :cckId)
      and (:state is null or p.state = :state)
      and (case when :type='SINGLE' then p.single else p.average end) > 0
    order by (case when :type='SINGLE' then p.single else p.average end) asc
    """)
    List<BestResult> findBest(
            @Param("cckId") String cckId,
            @Param("state") String state,
            @Param("eventName") String eventName,
            @Param("type") String type,
            Pageable pageable
    );

    @Query("select p.idx from Record p where p.round.idx=:roundIdx order by " +
            "(case when p.average = 0 then 5 when p.average = -2 then 4 when p.average = -1 then 3 when p.single = -2 then 2 when p.single = -1 then 1 else 0 end),"+
            "p.average asc, p.single asc")
    List<Integer> findByRoundOrder(@Param(value="roundIdx")Integer roundIdx);

    @Query("select p.idx from Record p where p.round.idx=:roundIdx and p.state=:state order by " +
            "(case when p.average = 0 then 5 when p.average = -2 then 4 when p.average = -1 then 3 when p.single = -2 then 2 when p.single = -1 then 1 else 0 end),"+
            "p.average asc, p.single asc")
    List<Integer> findByRoundAndStateOrder(@Param(value="roundIdx")Integer roundIdx, @Param(value="state")String state);

    List<Record> findByCckIdAndRound_Comp_IdxOrderByRound_EventStart(String cckId, Integer compIdx);

    @Query("select p from Record p join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where p.cckId=:cckId order by ce.eventName")
    List<Record> findByEmailOrderByRound_CubeEvent_EventName(String cckId);

    @EntityGraph(attributePaths = {"attempts"})
    @Query("SELECT r FROM Record r WHERE r.idx = :idx")
    Record findByIdx(Integer idx);

    //@Query("SELECT r.idx, COUNT(*) FROM Record p JOIN p.round r GROUP BY r.idx")
    //List<Object[]> findAllRoundParticipantCounts();
    //@Query("select count(p.idx) from Record p join fetch Scrambler r where p.round.idx=:roundIdx and p.email.")
    //Integer countPlayerByRoundState(@Param(value="roundIdx")Integer roundIdx);
}
