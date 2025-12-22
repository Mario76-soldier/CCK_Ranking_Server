package com.cubingclub.ranking.Competition.Service;

import com.cubingclub.ranking.Competition.DTO.*;
import com.cubingclub.ranking.Competition.Entity.EventFee;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.Round.DTO.RoundScheduleDTO;
import com.cubingclub.ranking.Round.Entity.Round;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final CubeEventRepository cubeEventRepository;
    public Integer create(CompetitionCreateDTO compDTO) {
        Competition competition;

        if (compDTO.getIdx() != null) {
            competition = competitionRepository.findByIdx(compDTO.getIdx());
            competition.update(compDTO);
            competition.getEventFees().clear();
        } else {
            compDTO.setCreatedAt(LocalDateTime.now());
            compDTO.setUpdatedAt(LocalDateTime.now());
            competition = compDTO.toEntity();
        }

        if (compDTO.getEventFeeList() != null) {
            for (EventFeeDTO feeDTO : compDTO.getEventFeeList()) {
                CubeEvent cubeEvent = cubeEventRepository.findByEventName(feeDTO.getEventName());
                EventFee feeEntity = feeDTO.toEntity(competition, cubeEvent);
                competition.getEventFees().add(feeEntity);
            }
        }

        return competitionRepository.save(competition).getIdx();
    }
    public CompetitionDTO getOne(Integer idx){
        return new CompetitionDTO(competitionRepository.findByIdx(idx));
    }
    public CompetitionScheduleDTO getOneSchedule(Integer idx){
        Competition competition=competitionRepository.findByIdx(idx);
        CompetitionScheduleDTO competitionScheduleDTO=new CompetitionScheduleDTO(competition);
        List<RoundScheduleDTO> roundScheduleDTOList=new ArrayList<>();
        for(Round round:competition.getRounds()){
            roundScheduleDTOList.add(new RoundScheduleDTO(round));
        }
        competitionScheduleDTO.setRoundSchedules(roundScheduleDTOList);
        return competitionScheduleDTO;
    }
    public Map<String, List<CompetitionDTO>> getCompList(LocalDate date){
        List<Competition> list=competitionRepository.findAllByOrderByCompDateStartDesc();

        Map<String, List<CompetitionDTO>> result = new HashMap<>();
        result.put("past", new ArrayList<>());
        result.put("now", new ArrayList<>());
        result.put("future", new ArrayList<>());

        for(Competition c : list){
            CompetitionDTO competitionDTO=new CompetitionDTO(c);
            if(c.getCompDateEnd().isBefore(date)){
                result.get("past").add(competitionDTO);
            }
            else if ((c.getCompDateEnd().isEqual(date) || c.getCompDateEnd().isAfter(date)) && (c.getCompDateStart().isEqual(date)||c.getCompDateStart().isBefore(date))){
                result.get("now").add(competitionDTO);
            }
            else{
                result.get("future").add(competitionDTO);
            }
        }
        return result;
    }
    public List<CompetitionDTO> getCompListToday(LocalDate date){
        List<Competition> list=competitionRepository.findByCompDateStartLessThanEqualAndCompDateEndGreaterThanEqual(date, date);
        List<CompetitionDTO> listDTO=new ArrayList<>();
        for(Competition c : list){
            listDTO.add(new CompetitionDTO(c));

        }
        return listDTO;
    }
    public void deleteByIdx(Integer idx){
        competitionRepository.deleteByIdx(idx);
    }
}
