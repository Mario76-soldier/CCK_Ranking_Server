package com.cubingclub.ranking.Repository;

import com.cubingclub.ranking.Entity.CubeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<CubeEvent, Integer> {
    CubeEvent findByEventName(String eventName);
}
