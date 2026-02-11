package com.cubingclub.ranking.Statistic.Repository;

import com.cubingclub.ranking.Statistic.DTO.RoundGlobalStatDTO;
import com.cubingclub.ranking.Statistic.DTO.RoundStateStatDTO;
import com.cubingclub.ranking.Record.Entity.Attempt;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoundStatRepositoryImpl implements RoundStatRepository {
    private final EntityManager entityManager;

    @Override
    public List<Attempt> findExtraByRoundIdx(Integer roundIdx) {
        return entityManager.createQuery(
                "select a from Attempt a join fetch a.record r join fetch r.round ro where a.extra is not null and ro.idx=:roundIdx", Attempt.class)
                .setParameter("roundIdx", roundIdx)
                .getResultList();
    }

    @Override
    public RoundGlobalStatDTO findSingleAndAverageGlobal(Integer roundIdx) {
        return entityManager.createQuery(
                "select new com.cubingclub.ranking.Record.DTO.RoundGlobalStatDTO(" +
                        "avg(case when r.single > 0 then r.single else null end), " +
                        "avg(case when r.average > 0 then r.average else null end), " +
                        "count(r)) from Record r " +
                        "where r.round.idx=:roundIdx", RoundGlobalStatDTO.class)
                .setParameter("roundIdx", roundIdx)
                .getSingleResult();
    }

    @Override
    public List<RoundStateStatDTO> findSingleAndAverageState(Integer roundIdx) {
        return entityManager.createQuery(
                "select new com.cubingclub.ranking.Record.DTO.RoundStateStatDTO(r.state, " +
                        "avg(case when r.single > 0 then r.single else null end), " +
                        "avg(case when r.average > 0 then r.average else null end), " +
                        "count(r)) from Record r " +
                        "where r.round.idx=:roundIdx and r.single>0 and r.average>0 group by r.state", RoundStateStatDTO.class)
                .setParameter("roundIdx", roundIdx)
                .getResultList();
    }
}
