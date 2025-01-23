package com.cubingclub.ranking.Repository;

import com.cubingclub.ranking.Entity.Participate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ParticipateRepository extends JpaRepository<Participate, Integer> {
    @Query("select p.idx from Participate p where p.round.idx=:roundIdx order by " +
            "(case when p.average=0 then 1 else 0 end) asc, " +
            "(case when p.single=-2 then 1 else 0 end) asc," +
            "(case when p.single=-1 then 1 else 0 end) asc, " +
            "(case when p.average=-2 then 1 else 0 end) asc, " +
            "(case when p.average=-1 then 1 else 0 end) asc, " +
            "p.average asc, p.single asc")
    List<Integer> findByRoundOrder(@Param(value="roundIdx")Integer roundIdx);
    List<Participate> findByEmailAndRound_Comp_Idx(String email, Integer compIdx);
    @Query("select p from Participate p join fetch p.round r join fetch r.cubeEvent ce join fetch r.comp c where p.email=:email order by ce.eventName")
    List<Participate> findByEmailOrderByRound_CubeEvent_EventName(String email);
    Participate findByIdx(Integer idx);
    @Query("select p from Participate p where p.round.idx=:roundIdx order by (case when p.ranking is null or p.ranking=0 then 1 else 0 end) asc, p.ranking asc")
    List<Participate> findByRound(@Param(value="roundIdx")Integer roundIdx);
    @Modifying
    @Query("update Participate p set p.ranking=:ranking where p.idx=:idx and p.single!=0")
    void updateRanking(@Param(value="ranking")Integer ranking, @Param(value="idx")Integer idx);
    @Modifying
    @Query("update Participate p set " +
            "p.first=:first," +
            "p.second=:second," +
            "p.third=:third," +
            "p.fourth=:fourth," +
            "p.fifth=:fifth," +
            "p.average=:average," +
            "p.single=:single " +
            "where p.idx=:idx")
    void updateRecord(@Param(value="first")BigDecimal first,
                      @Param(value="second")BigDecimal second,
                      @Param(value="third")BigDecimal third,
                      @Param(value="fourth") BigDecimal fourth,
                      @Param(value="fifth")BigDecimal fifth,
                      @Param(value="average") BigDecimal average,
                      @Param(value="single")BigDecimal single,
                      @Param(value="idx")Integer idx);

    @Modifying
    @Query("update Participate p set " +
            "p.checker1=:checker "+
            "where p.idx=:idx")
    void updateChecker(@Param(value="checker")String checker,
                      @Param(value="idx")Integer idx);
    void deleteByIdx(Integer idx);
    void deleteByRound_Idx(Integer idx);
    @Modifying
    @Query("update Participate p set p.checker2=:checker2 where p.idx=:idx")
    void updateChecker2(@Param(value="checker2")String checker2, @Param(value="idx")Integer idx);
}
