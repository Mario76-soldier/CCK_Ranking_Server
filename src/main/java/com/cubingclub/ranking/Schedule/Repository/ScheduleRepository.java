package com.cubingclub.ranking.Schedule.Repository;

import com.cubingclub.ranking.Schedule.Entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByCompetition_IdxAndAccount_CckIdOrderByRoundGroup_Round_EventStartAsc(Integer compIdx, String cckId);
    @Query("SELECT s FROM Schedule s WHERE s.competition.idx = :compIdx ORDER BY s.account.name ASC, s.roundGroup.round.eventStart ASC")
    List<Schedule> findByCompetiton(Integer compIdx);
}
