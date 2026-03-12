package com.cubingclub.ranking.Competition.Repository;

import com.cubingclub.ranking.Competition.Entity.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Integer> {
    // Past
    Page<Competition> findByCompDateEndBeforeOrderByCompDateStartDesc(Pageable pageable, LocalDate today);

    // Now
    Page<Competition> findByCompDateStartLessThanEqualAndCompDateEndGreaterThanEqualOrderByCompDateStartDesc(Pageable pageable, LocalDate today1, LocalDate today2);

    // Future
    Page<Competition> findByCompDateStartAfterOrderByCompDateStartAsc(Pageable pageable, LocalDate today);

    // Past
    Page<Competition> findByJoinDateEndBeforeOrderByJoinDateStartDesc(Pageable pageable, LocalDateTime today);

    // Now
    Page<Competition> findByJoinDateStartLessThanEqualAndJoinDateEndGreaterThanEqualOrderByJoinDateStartDesc(Pageable pageable, LocalDateTime today1, LocalDateTime today2);

    // Future
    Page<Competition> findByJoinDateStartAfterOrderByJoinDateStartAsc(Pageable pageable, LocalDateTime today);

    List<Competition> findByCompDateStartLessThanEqualAndCompDateEndGreaterThanEqualOrderByCompDateStartDesc(LocalDate today1, LocalDate today2);
}
