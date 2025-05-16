package com.cubingclub.ranking.Scrambler.Repository;

import com.cubingclub.ranking.Scrambler.Entity.Scrambler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScramblerRepository extends JpaRepository<Scrambler, Integer> {
    Scrambler findByIdx(Integer idx);
    Scrambler findByRound_IdxAndRoundGroupAndSeq(Integer roundIdx, String group, String seq);
    List<Scrambler> findByRound_IdxAndRoundGroup(Integer roundIdx, String roundGroup);
    List<Scrambler> findByRound_Idx(Integer roundIdx);
    void deleteByRound_Idx(Integer roundIdx);

}
