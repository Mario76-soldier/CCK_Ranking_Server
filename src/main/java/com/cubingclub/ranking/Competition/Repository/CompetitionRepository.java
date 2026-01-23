package com.cubingclub.ranking.Competition.Repository;

import com.cubingclub.ranking.Competition.Entity.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
    // Past
    Page<Competition> findByCompDateEndBeforeOrderByCompDateStartDesc(Pageable pageable, LocalDate today);

    // Now
    Page<Competition> findByCompDateStartLessThanEqualAndCompDateEndGreaterThanEqualOrderByCompDateStartDesc(Pageable pageable, LocalDate today1, LocalDate today2);

    // Future
    Page<Competition> findByCompDateStartAfterOrderByCompDateStartAsc(Pageable pageable, LocalDate today);
}
