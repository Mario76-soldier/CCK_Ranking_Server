package com.cubingclub.ranking.Statistic.Repository;

import com.cubingclub.ranking.Statistic.DTO.PlayerCountStatDTO;
import com.cubingclub.ranking.Statistic.DTO.StaffCountStatDTO;
import com.cubingclub.ranking.Record.Entity.Record;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerStatRepositoryImpl implements PlayerStatRepository{
    private final EntityManager entityManager;

    @Override
    public List<Record> findBreakRecordByCckId(String cckId) {
        return entityManager
                .createQuery("select r from Record r where " +
                        "r.cckId=:cckId and " +
                        "(r.singleBestType in (com.cubingclub.ranking.Record.Entity.BestType.SR, com.cubingclub.ranking.Record.Entity.BestType.CCR) " +
                        "or r.averageBestType in (com.cubingclub.ranking.Record.Entity.BestType.SR, com.cubingclub.ranking.Record.Entity.BestType.CCR))", Record.class)
                .setParameter("cckId", cckId)
                .getResultList();
    }

    @Override
    public List<Record> findHistoryByCckIdAndEventName(String cckId, String eventName) {
        return entityManager
                .createQuery("select r from Record r where " +
                        "r.cckId=:cckId and " +
                        "r.round.cubeEvent.eventName=:eventName", Record.class)
                .setParameter("cckId", cckId)
                .setParameter("eventName", eventName)
                .getResultList();
    }

    @Override
    public PlayerCountStatDTO findPlayerCountByCckId(String cckId) {
        return entityManager
                .createQuery("select new com.cubingclub.ranking.Statistic.DTO.PlayerCountStatDTO(count(distinct r.round.comp.idx), count(case when a.time > 0 then a.idx end)) from Record r " +
                        "left join r.attempts a where r.cckId=:cckId", PlayerCountStatDTO.class)
                .setParameter("cckId", cckId)
                .getSingleResult();
    }

    @Override
    public StaffCountStatDTO findStaffCountByCckId(String cckId) {
        return entityManager
                .createQuery("select new com.cubingclub.ranking.Statistic.DTO.StaffCountStatDTO(" +
                        "count(case when a.judgeCckId=:cckId then a.idx end), " +
                        "count(case when a.scramblerCckId=:cckId then a.idx end)) from Attempt a", StaffCountStatDTO.class)
                .setParameter("cckId", cckId)
                .getSingleResult();
    }
}
