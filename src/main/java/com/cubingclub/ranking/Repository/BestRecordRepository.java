package com.cubingclub.ranking.Repository;

import com.cubingclub.ranking.Entity.BestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BestRecordRepository extends JpaRepository<BestRecord, Integer> {
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac where ac.email=:email order by ce.eventName")
    List<BestRecord> findByEmailOrderByRound_CubeEvent_EventName(@Param(value="email")String email);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac where ac.email=:email and ce.eventName=:eventName")
    Optional<BestRecord> findByEmailAndEventName(@Param(value="email")String email, @Param(value="eventName")String eventName);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce where ce.eventName=:eventName order by " +
            "(case when r.single=-2 then 1 else 0 end) asc," +
            "(case when r.single=-1 then 1 else 0 end) asc, " +
            "r.single asc")
    List<BestRecord> findByEventNameOrderBySingle(@Param(value="eventName")String eventName);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac where ce.eventName=:eventName and ac.state=:state order by r.single asc")
    List<BestRecord> findByEventNameAndStateOrderBySingle(@Param(value="eventName")String eventName, @Param(value="state")String state);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce where ce.eventName=:eventName order by " +
            "(case when r.average=-2 then 1 else 0 end) asc," +
            "(case when r.average=-1 then 1 else 0 end) asc, " +
            "r.average asc")
    List<BestRecord> findByEventNameOrderByAverage(@Param(value="eventName")String eventName);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac where ce.eventName=:eventName and ac.state=:state order by r.average asc")
    List<BestRecord> findByEventNameAndStateOrderByAverage(@Param(value="eventName")String eventName, @Param(value="state")String state);
    @Query("SELECT ce.eventName, COUNT(*) " +
            "FROM BestRecord r join r.cubeEvent ce " +
            "group by ce.eventName")
    List<Object[]> findTotalParticipantsByEvent();
    @Query("SELECT ce.eventName, ac.state, COUNT(*) " +
            "FROM BestRecord r join r.account ac join r.cubeEvent ce " +
            "GROUP BY ce.eventName, ac.state")
    List<Object[]> findTotalParticipantsByEventAndState();


}
