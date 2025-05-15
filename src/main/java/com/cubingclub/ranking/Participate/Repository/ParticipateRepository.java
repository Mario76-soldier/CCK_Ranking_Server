package com.cubingclub.ranking.Participate.Repository;

import com.cubingclub.ranking.Participate.DTO.BestResult;
import com.cubingclub.ranking.Participate.DTO.ParticipateUserDTO;
import com.cubingclub.ranking.Participate.Entity.Participate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipateRepository extends JpaRepository<Participate, Integer> {
    void deleteByRound_Comp_Idx(Integer compIdx);
    @Query("select new com.cubingclub.ranking.Participate.DTO.ParticipateUserDTO(p.idx, p.pdx, a.email, a.name, a.enName, a.state, " +
            "p.first, p.second, p.third, p.fourth, p.fifth, p.extra1, p.extra1Reason, p.extra2, p.extra2Reason, p.single, p.average, p.ranking, p.stateRanking, p.singleBestType, p.averageBestType) " +
            "from Participate p join Account a on (p.account.email=a.email) join Round r on (p.round.idx=r.idx) where r.idx=:roundIdx order by (case when p.ranking is null or p.ranking=0 then 1 else 0 end) asc, p.ranking asc")
    List<ParticipateUserDTO> findByRound_IdxForUser(@Param(value="roundIdx")Integer roundIdx);
    @Query("select p from Participate p join fetch p.account a join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where r.idx=:roundIdx and p.single is not null and p.average is not null and p.single!=0 " +
            "and p.average!=0 order by (case when p.ranking is null or p.ranking=0 then 1 else 0 end) asc, p.ranking asc")
    List<Participate> findByRound_IdxForBestType(@Param(value="roundIdx")Integer roundIdx);
    @Query("select p from Participate p join fetch p.account a join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where r.idx=:roundIdx " +
            "order by (case when p.ranking is null or p.ranking=0 then 1 else 0 end) asc, p.ranking asc")
    List<Participate> findByRound_Idx(@Param(value="roundIdx")Integer roundIdx);
    @Modifying
    @Query(value = "update Participate p JOIN (" +
            "SELECT idx, RANK() OVER (ORDER BY (case when average = -2 then 2 when average = -1 then 1 else 0 end), average) AS new_rank FROM Participate where round_idx =:roundIdx and average is not null and average!=0) " +
            "ranked on p.idx=ranked.idx set p.ranking=ranked.new_rank where p.round_idx =:roundIdx and p.average is not null and p.average!=0", nativeQuery = true)
    void setRankOnRound(Integer roundIdx);
    @Query("select p.idx as idx, p.single as best from Participate p join p.account a join p.round r join r.cubeEvent ce where a.email=:email and ce.eventName=:eventName and p.single!=0 and p.single is not null " +
            "order by (case when p.single = -2 then 2 when p.single = -1 then 1 else 0 end), p.single asc limit 1")
    BestResult findPersonalSingleBest(@Param(value="email")String email, @Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.average as best from Participate p join p.account a join p.round r join r.cubeEvent ce where a.email=:email and ce.eventName=:eventName and p.average!=0 and p.average is not null " +
            "order by (case when p.average = -2 then 2 when p.average = -1 then 1 else 0 end), p.average asc limit 1")
    BestResult findPersonalAverageBest(@Param(value="email")String email, @Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.single as best from Participate p join p.account a join p.round r join r.cubeEvent ce where a.state=:state and ce.eventName=:eventName and p.single!=0 and p.single is not null " +
            "order by (case when p.single = -2 then 2 when p.single = -1 then 1 else 0 end), p.single asc limit 1")
    BestResult findStateSingleBest(@Param(value="state")String state, @Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.average as best from Participate p join p.account a join p.round r join r.cubeEvent ce where a.state=:state and ce.eventName=:eventName and p.average!=0 and p.average is not null " +
            "order by (case when p.average = -2 then 2 when p.average = -1 then 1 else 0 end), p.average asc limit 1")
    BestResult findStateAverageBest(@Param(value="state")String state, @Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.single as best from Participate p join p.account a join p.round r join r.cubeEvent ce where ce.eventName=:eventName and p.single!=0 and p.single is not null " +
            "order by (case when p.single = -2 then 2 when p.single = -1 then 1 else 0 end), p.single asc limit 1")
    BestResult findAllSingleBest(@Param(value="eventName")String eventName);
    @Query("select p.idx as idx, p.average as best from Participate p join p.account a join  p.round r join  r.cubeEvent ce where ce.eventName=:eventName and p.average!=0 and p.average is not null " +
            "order by (case when p.average = -2 then 2 when p.average = -1 then 1 else 0 end), p.average asc limit 1")
    BestResult findAllAverageBest(@Param(value="eventName")String eventName);
    @Query("select p.idx from Participate p where p.round.idx=:roundIdx order by " +
            "(case when p.average = 0 then 5 when p.average = -2 then 4 when p.average = -1 then 3 when p.single = -2 then 2 when p.single = -1 then 1 else 0 end),"+
            "p.average asc, p.single asc")
    List<Integer> findByRoundOrder(@Param(value="roundIdx")Integer roundIdx);
    @Query("select p.idx from Participate p where p.round.idx=:roundIdx and p.account.state=:state order by " +
            "(case when p.average = 0 then 5 when p.average = -2 then 4 when p.average = -1 then 3 when p.single = -2 then 2 when p.single = -1 then 1 else 0 end),"+
            "p.average asc, p.single asc")
    List<Integer> findByRoundAndStateOrder(@Param(value="roundIdx")Integer roundIdx, @Param(value="state")String state);
    List<Participate> findByAccount_EmailAndRound_Comp_Idx(String email, Integer compIdx);
    @Query("select p from Participate p join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where p.account.email=:email order by ce.eventName")
    List<Participate> findByEmailOrderByRound_CubeEvent_EventName(String email);
    @Query("select p from Participate p join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where p.account.email=:email and ce.eventName=:eventName order by p.average limit 1")
    Optional<Participate> findAverageBest(String email, String eventName);
    @Query("select p from Participate p join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where p.account.email=:email and ce.eventName=:eventName order by p.single limit 1")
    Optional<Participate> findSingleBest(String email, String eventName);
    Participate findByIdx(Integer idx);
    void deleteByIdx(Integer idx);
    void deleteByRound_Idx(Integer idx);
    @Query("SELECT r.idx, COUNT(*) FROM Participate p JOIN p.round r GROUP BY r.idx")
    List<Object[]> findAllRoundParticipantCounts();
    //@Query("select count(p.idx) from Participate p join fetch Round r where p.round.idx=:roundIdx and p.email.")
    //Integer countParticipateByRoundState(@Param(value="roundIdx")Integer roundIdx);
}
