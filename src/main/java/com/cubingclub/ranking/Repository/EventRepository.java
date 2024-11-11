package com.cubingclub.ranking.Repository;

import com.cubingclub.ranking.Entity.CubeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<CubeEvent, Integer> {
    List<CubeEvent> findAllBy();
    CubeEvent findByEventName(String eventName);
}
