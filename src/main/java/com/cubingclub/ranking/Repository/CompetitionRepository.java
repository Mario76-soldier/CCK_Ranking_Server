package com.cubingclub.ranking.Repository;

import com.cubingclub.ranking.Entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
    Competition findByIdx(Integer idx);
    List<Competition> findByCompDateEndBeforeOrderByCompDateStartDesc(LocalDate date);
    List<Competition> findByCompDateEndGreaterThanEqualAndCompDateStartLessThanEqualOrderByCompDateStartDesc(LocalDate date1, LocalDate date2);
    List<Competition> findByCompDateStartAfterOrderByCompDateStartDesc(LocalDate date);
    List<Competition> findByJoinDateEndAfterAndJoinDateStartBefore(LocalDateTime date1, LocalDateTime date2);
    void deleteByIdx(Integer idx);
}
