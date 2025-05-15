package com.cubingclub.ranking.Round.Service;

import com.cubingclub.ranking.Round.DTO.CreateRoundDTO;
import com.cubingclub.ranking.Round.DTO.RoundDTO;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.Participate.Repository.ParticipateRepository;
import com.cubingclub.ranking.Round.Repository.RoundRepository;
import com.cubingclub.ranking.Round.Entity.Round;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RoundService {
    private final ParticipateRepository participateRepository;
    private final RoundRepository roundRepository;
    private final CompetitionRepository competitionRepository;
    private final CubeEventRepository cubeEventRepository;
    public List<Integer> create(List<CreateRoundDTO> roundList){
        List<Integer> list=new ArrayList<>();
        for(CreateRoundDTO roundDTO:roundList){
            if(roundDTO.getIdx()!=null) {
                list.add(roundRepository.save(roundDTO.toEntity(competitionRepository, cubeEventRepository)).getIdx());
            }
            else{
                if(competitionRepository.findByIdx(roundDTO.getComp())!=null) {
                    list.add(roundRepository.save(roundDTO.toEntityNoIdx(competitionRepository, cubeEventRepository)).getIdx());
                }
            }
        }
        return list;
    }
    public RoundDTO getOne(Integer idx){
        return new RoundDTO(roundRepository.findByIdx(idx));
    }
    public Map<String, List<RoundDTO>> getRoundList(Integer compIdx, LocalDateTime date){
        List<Round> list=roundRepository.findRoundList(compIdx);

        Map<String, List<RoundDTO>> result = new HashMap<>();
        result.put("past", new ArrayList<>());
        result.put("now", new ArrayList<>());
        result.put("future", new ArrayList<>());

        for(Round r : list){
            RoundDTO roundDTO=new RoundDTO(r);
            if(r.getEventEnd().isBefore(date)){
                result.get("past").add(roundDTO);
            }
            else if ((r.getEventEnd().isEqual(date) || r.getEventEnd().isAfter(date)) && (r.getEventStart().isEqual(date)||r.getEventStart().isBefore(date))){
                result.get("now").add(roundDTO);
            }
            else{
                result.get("future").add(roundDTO);
            }
        }
        return result;
    }
    public void deleteByRoundIdx(Integer roundIdx){
        participateRepository.deleteByRound_Idx(roundIdx);
        roundRepository.deleteByIdx(roundIdx);
    }
    public void updateAdvance(Integer roundIdx, Integer advance){
        RoundDTO roundDTO=new RoundDTO(roundRepository.findByIdx(roundIdx));
        roundDTO.setAdvance(advance);
        roundRepository.save(roundDTO.toEntity());
    }
}
