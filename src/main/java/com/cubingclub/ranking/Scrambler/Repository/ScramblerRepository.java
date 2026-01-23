package com.cubingclub.ranking.Scrambler.Repository;

import com.cubingclub.ranking.Scrambler.Entity.Scramble;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScramblerRepository extends JpaRepository<Scramble, Integer> {
    @Query("select s from Scramble s join fetch s.round r join fetch r.comp join fetch r.cubeEvent where r.idx = :roundIdx and s.roundGroup = :group and s.seq = :seq")
    Scramble findByRound_IdxAndRoundGroupAndSeq(@Param("roundIdx") Integer roundIdx, @Param("group") String group, @Param("seq") String seq);
    
    @Query("select s from Scramble s join fetch s.round r join fetch r.comp join fetch r.cubeEvent where r.idx = :roundIdx and s.roundGroup = :roundGroup")
    List<Scramble> findByRound_IdxAndRoundGroup(@Param("roundIdx") Integer roundIdx, @Param("roundGroup") String roundGroup);
    
    void deleteByRound_Idx(Integer roundIdx);
}