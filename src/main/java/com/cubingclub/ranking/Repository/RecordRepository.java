package com.cubingclub.ranking.Repository;

import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Integer> {
    @Query("select r from Record r join fetch r.cubeEvent ce join fetch r.account a where a.email=:email order by ce.eventName")
    List<Record> findByEmailOrderByRound_CubeEvent_EventName(@Param(value="email")String email);
    @Query("select r from Record r join fetch r.cubeEvent ce join fetch r.account a where a.email=:email and ce.eventName=:eventName")
    Optional<Record> findByEmailAndEventName(@Param(value="email")String email, @Param(value="eventName")String eventName);
    @Query("select r from Record r join fetch r.cubeEvent ce join fetch r.account a where ce.eventName=:eventName order by r.single asc")
    List<Record> findByEventNameOrderBySingle(@Param(value="eventName")String eventName);
    @Query("select r from Record r join fetch r.cubeEvent ce join fetch r.account a where ce.eventName=:eventName and a.state=:state order by r.single asc")
    List<Record> findByEventNameAndStateOrderBySingle(@Param(value="eventName")String eventName, @Param(value="state")String state);
    @Query("select r from Record r join fetch r.cubeEvent ce join fetch r.account a where ce.eventName=:eventName order by r.average asc")
    List<Record> findByEventNameOrderByAverage(@Param(value="eventName")String eventName);
    @Query("select r from Record r join fetch r.cubeEvent ce join fetch r.account a where ce.eventName=:eventName and a.state=:state order by r.average asc")
    List<Record> findByEventNameAndStateOrderByAverage(@Param(value="eventName")String eventName, @Param(value="state")String state);
}
