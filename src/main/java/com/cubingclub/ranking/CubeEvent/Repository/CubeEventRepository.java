package com.cubingclub.ranking.CubeEvent.Repository;

import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CubeEventRepository extends JpaRepository<CubeEvent, Integer> {
    List<CubeEvent> findAllBy();
    CubeEvent findByEventName(String eventName);
}
