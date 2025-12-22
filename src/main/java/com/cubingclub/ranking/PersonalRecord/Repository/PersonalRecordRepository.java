package com.cubingclub.ranking.PersonalRecord.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.cubingclub.ranking.Record.Entity.Record;

import java.util.List;

public interface PersonalRecordRepository extends JpaRepository<Record, Integer> {
    @Query("select r from Record r join fetch r.round ro join fetch ro.cubeEvent ce join fetch ro.comp c where r.cckId=:cckId order by ce.eventName, ro.eventStart desc")
    List<Record> findByCCKId(String cckId);

}
