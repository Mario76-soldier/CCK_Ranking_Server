package com.cubingclub.ranking.Competition.Repository;

import com.cubingclub.ranking.Competition.Entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
    Competition findByIdx(Integer idx);
    List<Competition> findAllByOrderByCompDateStartDesc();
    List<Competition> findByCompDateStartBeforeAndCompDateEndAfter(LocalDate date1, LocalDate date2);
    List<Competition> findByJoinDateEndAfterAndJoinDateStartBefore(LocalDateTime date1, LocalDateTime date2);
    void deleteByIdx(Integer idx);
}
