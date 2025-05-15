package com.cubingclub.ranking.Round.Repository;

import com.cubingclub.ranking.Round.Entity.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoundRepository extends JpaRepository<Round, Integer> {
    Round findByIdx(Integer idx);
    @Query("select r from Round r join fetch r.cubeEvent ce join fetch r.comp c where r.comp.idx=:compIdx order by r.eventStart asc")
    List<Round> findRoundList(@Param(value="compIdx")Integer compIdx);
    @Modifying
    @Query("update Round u set u.advance=:advance where u.idx=:idx")
    void updateAdvance(@Param(value="idx")Integer idx, @Param(value = "advance")Integer advance);
    void deleteByComp_Idx(Integer compIdx);
    void deleteByIdx(Integer Idx);

}
