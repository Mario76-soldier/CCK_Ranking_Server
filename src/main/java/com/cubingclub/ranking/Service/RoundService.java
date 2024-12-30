package com.cubingclub.ranking.Service;

import com.cubingclub.ranking.DTO.CreateRoundDTO;
import com.cubingclub.ranking.DTO.CubeEventDTO;
import com.cubingclub.ranking.DTO.RoundDTO;
import com.cubingclub.ranking.Entity.CubeEvent;
import com.cubingclub.ranking.Repository.CompetitionRepository;
import com.cubingclub.ranking.Repository.EventRepository;
import com.cubingclub.ranking.Repository.ParticipateRepository;
import com.cubingclub.ranking.Repository.RoundRepository;
import com.cubingclub.ranking.Entity.Round;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoundService {
    private final ParticipateRepository participateRepository;
    private final RoundRepository roundRepository;
    private final CompetitionRepository competitionRepository;
    private final EventRepository eventRepository;
    public Integer create(CreateRoundDTO round){
        RoundDTO roundDTO=new RoundDTO(round.getIdx(), competitionRepository.findByIdx(round.getComp()), eventRepository.findByEventName(round.getCubeEvent()), round.getRoundName(), round.getEventStart(), round.getEventEnd(), round.getAdvance());
        if(roundDTO.getIdx()!=null){
            return roundRepository.save(roundDTO.toEntity()).getIdx();
        }
        return roundRepository.save(roundDTO.toEntityNoIdx()).getIdx();
    }
    public RoundDTO getOne(Integer idx){
        return new RoundDTO(roundRepository.findByIdx(idx));
    }
    public List<CubeEventDTO> getCubeEvent(){
        List<CubeEvent> list=eventRepository.findAllBy();
        List<CubeEventDTO> listDTO=new ArrayList<>();
        for (CubeEvent cubeEvent:list) {
            listDTO.add(new CubeEventDTO(cubeEvent));
        }
        return listDTO;
    }
    public List<RoundDTO> getCompRound(Integer idx){
        List<Round> list=roundRepository.findByCompIdx(idx);
        List<RoundDTO> listDTO=new ArrayList<>();
        for (Round round:list) {
            listDTO.add(new RoundDTO(round));
        }
        return listDTO;
    }
    public List<RoundDTO> getRoundNow(LocalDateTime date, Integer idx){
        List<Round> list=roundRepository.findNow(date, idx);
        List<RoundDTO> listDTO=new ArrayList<>();
        for (Round now:list) {
            listDTO.add(new RoundDTO(now));
        }
        return listDTO;
    }

    public List<RoundDTO> getRoundFuture(LocalDateTime date, Integer idx){
        List<Round> list=roundRepository.findFuture(date, idx);
        List<RoundDTO> listDTO=new ArrayList<>();
        for (Round future:list) {
            listDTO.add(new RoundDTO(future));
        }
        return listDTO;
    }
    public List<RoundDTO> getRoundPast(LocalDateTime date, Integer idx){
        List<Round> list=roundRepository.findPast(date, idx);
        List<RoundDTO> listDTO=new ArrayList<>();
        for (Round past:list) {
            listDTO.add(new RoundDTO(past));
        }
        return listDTO;
    }
    public void deleteRound(Integer compIdx){
        roundRepository.deleteByCompIdx(compIdx);
    }
    public void deleteByRoundIdx(Integer roundIdx){
        participateRepository.deleteByRound_Idx(roundIdx);
        roundRepository.deleteByIdx(roundIdx);
    }
    public void updateAdvance(Integer nextCompIdx, Integer advance){
        roundRepository.updateAdvance(nextCompIdx, advance);
    }
}
