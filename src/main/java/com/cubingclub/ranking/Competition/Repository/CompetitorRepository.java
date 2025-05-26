package com.cubingclub.ranking.Competition.Repository;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.Competition.Entity.Competitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CompetitorRepository extends JpaRepository<Competitor, Integer> {
    List<Competitor> findByCompetition_Idx(Integer compIdx);
    Competitor findByCompetition_IdxAndAccount_email(Integer compIdx, String email);
    Competitor findByIdx(Integer idx);
    void deleteByIdx(Integer idx);

}
