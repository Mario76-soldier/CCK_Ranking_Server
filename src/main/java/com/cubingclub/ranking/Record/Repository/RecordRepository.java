package com.cubingclub.ranking.Record.Repository;

import com.cubingclub.ranking.Record.DTO.BestResult;
import com.cubingclub.ranking.Record.Entity.Record;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Integer> {
    @Query("select p from Record p join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where r.idx=:roundIdx order by (case when p.ranking is null or p.ranking=0 then 1 else 0 end) asc, p.ranking asc")
    List<Record> findByRound_Idx(@Param(value="roundIdx")Integer roundIdx);
    @Query("select p from Record p join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where r.idx=:roundIdx and p.single is not null and p.average is not null and p.single!=0 " +
            "and p.average!=0 order by (case when p.ranking is null or p.ranking=0 then 1 else 0 end) asc, p.ranking asc")
    List<Record> findByRound_IdxForBestType(@Param(value="roundIdx")Integer roundIdx);
    @Modifying
    @Query(value = "UPDATE Record r JOIN (" +
            "SELECT idx, RANK() OVER (" +
            "ORDER BY " +
            "(CASE WHEN average = -2 THEN 2 WHEN average = -1 THEN 1 ELSE 0 END), average, " +
            "(CASE WHEN single = -2 THEN 2 WHEN single = -1 THEN 1 ELSE 0 END), single" +
            ") AS new_rank " +
            "FROM Record " +
            "WHERE round_idx = :roundIdx AND average IS NOT NULL AND average != 0" +
            ") ranked ON r.idx = ranked.idx " +
            "SET r.ranking = ranked.new_rank " +
            "WHERE r.round_idx = :roundIdx AND r.average IS NOT NULL AND r.average != 0",
            nativeQuery = true)
    void setRankOnRound(@Param("roundIdx") Integer roundIdx);
    @Query("select p.idx as idx, p.single as best from Record p join p.round r join r.cubeEvent ce where p.cckId=:cckId and ce.eventName=:eventName and p.single>0 and p.single is not null " +
            "order by p.single asc limit 1")
    BestResult findPersonalSingleBest(@Param(value="cckId")String cckId, @Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.average as best from Record p join p.round r join r.cubeEvent ce where p.cckId=:cckId and ce.eventName=:eventName and p.average>0 and p.average is not null " +
            "order by p.average asc limit 1")
    BestResult findPersonalAverageBest(@Param(value="cckId")String cckId, @Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.single as best from Record p join p.round r join r.cubeEvent ce where p.state=:state and ce.eventName=:eventName and p.single>0 and p.single is not null " +
            "order by p.single asc limit 1")
    BestResult findStateSingleBest(@Param(value="state")String state, @Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.average as best from Record p join p.round r join r.cubeEvent ce where p.state=:state and ce.eventName=:eventName and p.average>0 and p.average is not null " +
            "order by p.average asc limit 1")
    BestResult findStateAverageBest(@Param(value="state")String state, @Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.single as best from Record p join p.round r join r.cubeEvent ce where ce.eventName=:eventName and p.single>0 and p.single is not null " +
            "order by p.single asc limit 1")
    BestResult findAllSingleBest(@Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.average as best from Record p join  p.round r join  r.cubeEvent ce where ce.eventName=:eventName and p.average>0 and p.average is not null " +
            "order by p.average asc limit 1")
    BestResult findAllAverageBest(@Param(value="eventName")String eventName);
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
    @Query("select p from Record p join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where p.cckId=:cckId and ce.eventName=:eventName order by p.average limit 1")
    Optional<Record> findAverageBest(String cckId, String eventName);
    @Query("select p from Record p join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where p.cckId=:cckId and ce.eventName=:eventName order by p.single limit 1")
    Optional<Record> findSingleBest(String cckId, String eventName);
    @EntityGraph(attributePaths = {"attempts"})
    @Query("SELECT r FROM Record r WHERE r.idx = :idx")
    Record findByIdx(Integer idx);
    void deleteByIdx(Integer idx);
    //@Query("SELECT r.idx, COUNT(*) FROM Record p JOIN p.round r GROUP BY r.idx")
    //List<Object[]> findAllRoundParticipantCounts();
    //@Query("select count(p.idx) from Record p join fetch Scrambler r where p.round.idx=:roundIdx and p.email.")
    //Integer countParticipateByRoundState(@Param(value="roundIdx")Integer roundIdx);
}
