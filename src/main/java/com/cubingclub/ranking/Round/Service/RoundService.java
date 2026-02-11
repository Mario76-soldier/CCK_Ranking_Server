package com.cubingclub.ranking.Round.Service;

import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Round.DTO.*;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import com.cubingclub.ranking.Scrambler.Service.ScramblerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoundService {
    private final RoundRepository roundRepository;
    private final CompetitionRepository competitionRepository;
    private final CubeEventRepository cubeEventRepository;
    private final ScramblerService scramblerService;

    @Transactional
    public void createRoundList(List<RoundCreateDTO> roundList) {
        roundList.forEach(this::createRound);
    }

    @Transactional
    public void updateRoundList(List<RoundUpdateDTO> dtoList) {
        for (RoundUpdateDTO roundUpdateDTO : dtoList) {
            updateSingleRound(roundUpdateDTO);
        }
    }

    @Transactional(readOnly = true)
    public RoundDTO getOne(Integer idx) {
        Round round = roundRepository.findByIdx(idx).orElseThrow(() -> new CustomizedException(ErrorCode.ROUND_NOT_FOUND));
        Hibernate.initialize(round.getGroupList());

        return new RoundDTO(round);
    }

    @Transactional(readOnly = true)
    public RoundListDTO getRoundList(Integer compIdx, Integer dayCount) {
        return new RoundListDTO(getRoundPast(compIdx, dayCount), getRoundNow(compIdx, dayCount), getRoundFuture(compIdx, dayCount));
    }

    private List<RoundDTO> getRoundPast(Integer compIdx, Integer dayCount){
        return roundRepository.findPastRoundsByCompIdx(compIdx, dayCount, LocalDateTime.now()).stream().map(RoundDTO::new).toList();
    }


    private List<RoundDTO> getRoundNow(Integer compIdx, Integer dayCount){
        return roundRepository.findPresentRoundsByCompIdx(compIdx, dayCount, LocalDateTime.now()).stream().map(RoundDTO::new).toList();
    }


    private List<RoundDTO> getRoundFuture(Integer compIdx, Integer dayCount){
        return roundRepository.findFutureRoundsByCompIdx(compIdx, dayCount, LocalDateTime.now()).stream().map(RoundDTO::new).toList();
    }

    @Transactional
    public void deleteByRoundIdx(Integer roundIdx){
        roundRepository.deleteById(roundIdx);
    }

    @Transactional
    public void updateAdvance(Integer roundIdx, Integer advance){
        Round round=roundRepository.findById(roundIdx).orElseThrow(()->new CustomizedException(ErrorCode.ROUND_NOT_FOUND));
        round.updateAdvance(advance);
    }

    private void updateSingleRound(RoundUpdateDTO roundUpdateDTO) {

        Round round = roundRepository.findById(roundUpdateDTO.getIdx())
                .orElseThrow(() -> new CustomizedException(ErrorCode.ROUND_NOT_FOUND));

        CubeEvent cubeEvent = cubeEventRepository.findById(roundUpdateDTO.getCubeEvent())
                .orElseThrow(() -> new CustomizedException(ErrorCode.CUBE_EVENT_NOT_FOUND));

        round.update(roundUpdateDTO, cubeEvent);

        round.updateRoundGroups(roundUpdateDTO.getGroupList());
    }

    private void createRound(RoundCreateDTO roundCreateDTO) {
        Round round = roundCreateDTO.toEntity(
                findCompetition(roundCreateDTO.getComp()),
                findCubeEvent(roundCreateDTO.getCubeEvent())
        );

        round.updateRoundGroups(roundCreateDTO.getGroupList());

        Round saved = roundRepository.save(round);

        scramblerService.saveScramble(saved.getIdx(), roundCreateDTO.getGroupList());
    }

    private Competition findCompetition(Integer idx) {
        return competitionRepository.findById(idx)
                .orElseThrow(() -> new CustomizedException(ErrorCode.COMPETITION_NOT_FOUND));
    }

    private CubeEvent findCubeEvent(String eventName) {
        return cubeEventRepository.findById(eventName)
                .orElseThrow(() -> new CustomizedException(ErrorCode.CUBE_EVENT_NOT_FOUND));
    }
}
