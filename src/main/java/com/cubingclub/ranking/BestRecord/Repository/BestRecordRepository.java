package com.cubingclub.ranking.BestRecord.Repository;

import com.cubingclub.ranking.BestRecord.Entity.BestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BestRecordRepository extends JpaRepository<BestRecord, Integer> {
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac join fetch r.single si join fetch r.average av where ac.email=:email order by ce.eventName")
    List<BestRecord> findByEmailOrderByRound_CubeEvent_EventName(@Param(value="email")String email);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac join fetch r.single si join fetch r.average av where ac.email=:email and ce.eventName=:eventName")
    Optional<BestRecord> findByEmailAndEventName(@Param(value="email")String email, @Param(value="eventName")String eventName);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.single si join fetch r.average av where ce.eventName=:eventName order by " +
            "(case when si.single=-2 then 1 else 0 end) asc," +
            "(case when si.single=-1 then 1 else 0 end) asc, " +
            "si.single asc")
    List<BestRecord> findByEventNameOrderBySingle(@Param(value="eventName")String eventName);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac join fetch r.single si join fetch r.average av where ce.eventName=:eventName and ac.state=:state order by " +
            "(case when si.single=-2 then 1 else 0 end) asc," +
            "(case when si.single=-1 then 1 else 0 end) asc, " +
            "si.single asc")
    List<BestRecord> findByEventNameAndStateOrderBySingle(@Param(value="eventName")String eventName, @Param(value="state")String state);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.single si join fetch r.average av where ce.eventName=:eventName order by " +
            "(case when av.average=-2 then 1 else 0 end) asc," +
            "(case when av.average=-1 then 1 else 0 end) asc, " +
            "av.average asc")
    List<BestRecord> findByEventNameOrderByAverage(@Param(value="eventName")String eventName);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac join fetch r.single si join fetch r.average av where ce.eventName=:eventName and ac.state=:state order by " +
            "(case when av.average=-2 then 1 else 0 end) asc," +
            "(case when av.average=-1 then 1 else 0 end) asc, " +
            "av.average asc")
    List<BestRecord> findByEventNameAndStateOrderByAverage(@Param(value="eventName")String eventName, @Param(value="state")String state);
    @Query("SELECT ce.eventName, COUNT(*) " +
            "FROM BestRecord r join r.cubeEvent ce " +
            "group by ce.eventName")
    List<Object[]> findTotalParticipantsByEvent();
    @Query("SELECT ce.eventName, ac.state, COUNT(*) " +
            "FROM BestRecord r join r.account ac join r.cubeEvent ce " +
            "GROUP BY ce.eventName, ac.state")
    List<Object[]> findTotalParticipantsByEventAndState();
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac join fetch r.single si join fetch r.average av where ce.eventName=:eventName order by si.single asc limit 1")
    Optional<BestRecord> findBestSingle(@Param(value="eventName")String eventName);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac join fetch r.single si join fetch r.average av where ce.eventName=:eventName order by av.average asc limit 1")
    Optional<BestRecord> findBestAverage(@Param(value="eventName")String eventName);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac join fetch r.single si join fetch r.average av where ac.state=:state and ce.eventName=:eventName order by si.single asc limit 1")
    Optional<BestRecord> findBestSingleState(@Param(value="eventName")String eventName, @Param(value="state")String state);
    @Query("select r from BestRecord r join fetch r.cubeEvent ce join fetch r.account ac join fetch r.single si join fetch r.average av where ac.state=:state and ce.eventName=:eventName order by av.average asc limit 1")
    Optional<BestRecord> findBestAverageState(@Param(value="eventName")String eventName, @Param(value="state")String state);
}
