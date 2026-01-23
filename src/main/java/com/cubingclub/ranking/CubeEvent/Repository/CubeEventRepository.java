package com.cubingclub.ranking.CubeEvent.Repository;

import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CubeEventRepository extends JpaRepository<CubeEvent, String> {
}
