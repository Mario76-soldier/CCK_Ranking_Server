package com.cubingclub.ranking.Record.Repository;

import com.cubingclub.ranking.Record.Entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Integer> {
    Attempt findByIdx(Integer idx);
    Attempt findByRecord_IdxAndExtra(Integer recordIdx, Integer extra);
    List<Attempt> findByRecord_Idx(Integer recordIdx);
    Attempt findByRecord_IdxAndAttemptTry(Integer recordIdx, Integer seq);
}
