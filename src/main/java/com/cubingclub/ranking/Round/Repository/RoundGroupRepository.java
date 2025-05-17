package com.cubingclub.ranking.Round.Repository;

import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Round.Entity.RoundGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoundGroupRepository extends JpaRepository<RoundGroup, Integer> {
    @Query("select g.groupName from RoundGroup g join Round r on g.round.idx=r.idx where r.idx=:roundIdx")
    List<String> findGroupByRound_Idx(Integer roundIdx);
    RoundGroup findByRound_IdxAndGroupName(Integer roundIdx, String groupName);
}
