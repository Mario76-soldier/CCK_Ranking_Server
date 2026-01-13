package com.cubingclub.ranking.Competition.Service;

import com.cubingclub.ranking.Competition.DTO.*;
import com.cubingclub.ranking.Competition.Entity.EventFee;
import com.cubingclub.ranking.Competition.Repository.CompetitionRepository;
import com.cubingclub.ranking.Competition.Entity.Competition;
import com.cubingclub.ranking.CubeEvent.Entity.CubeEvent;
import com.cubingclub.ranking.CubeEvent.Repository.CubeEventRepository;
import com.cubingclub.ranking.Exception.Entity.ErrorCode;
import com.cubingclub.ranking.Exception.Exceptions.CustomizedException;
import com.cubingclub.ranking.Round.DTO.RoundScheduleDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final CubeEventRepository cubeEventRepository;

    @Transactional
    public void create(CompetitionCreateDTO competitionCreateDTO) {
        Competition competition = competitionCreateDTO.toEntity();

        if (competitionCreateDTO.getEventFeeList() != null) {
            for (EventFeeDTO feeDTO : competitionCreateDTO.getEventFeeList()) {
                CubeEvent cubeEvent = cubeEventRepository.findById(feeDTO.getEventName()).orElseThrow(() -> new CustomizedException(ErrorCode.CUBE_EVENT_NOT_FOUND));
                EventFee feeEntity = feeDTO.toEntity(competition, cubeEvent);
                competition.getEventFees().add(feeEntity);
            }
        }
        competitionRepository.save(competition);
    }

    @Transactional
    public void update(CompetitionUpdateDTO competitionUpdateDTO){
        Competition competition=competitionRepository.findById(competitionUpdateDTO.getIdx()).orElseThrow(()->new CustomizedException(ErrorCode.COMPETITION_NOT_FOUND));
        competition.update(competitionUpdateDTO);
    }

    @Transactional(readOnly=true)
    public CompetitionDTO getOne(Integer idx){
        return new CompetitionDTO(competitionRepository.findById(idx).orElseThrow(()->new CustomizedException(ErrorCode.COMPETITION_NOT_FOUND)));
    }

    @Transactional(readOnly=true)
    public CompetitionScheduleDTO getOneSchedule(Integer idx){
        Competition competition=competitionRepository.findById(idx).orElseThrow(()->new CustomizedException(ErrorCode.COMPETITION_NOT_FOUND));
        // OneToMany 컬렉션 초기화
        competition.getRounds().size();
        return new CompetitionScheduleDTO(competition, competition.getRounds().stream().map(RoundScheduleDTO::new).toList());
    }

    @Transactional(readOnly = true)
    public Page<CompetitionDTO> getCompPast(Pageable pageable){
        LocalDate currentTime=LocalDate.now();
        Page<Competition> competitions = competitionRepository
                .findByCompDateEndBeforeOrderByCompDateStartDesc(pageable, currentTime);
        // OneToMany 컬렉션 초기화
        competitions.forEach(comp -> {
            comp.getEventFees().size();
        });
        return competitions.map(CompetitionDTO::new);
    }

    @Transactional(readOnly=true)
    public Page<CompetitionDTO> getCompNow(Pageable pageable){
        LocalDate currentTime=LocalDate.now();
        Page<Competition> competitions = competitionRepository
                .findByCompDateStartLessThanEqualAndCompDateEndGreaterThanEqualOrderByCompDateStartDesc(pageable, currentTime, currentTime);
        // OneToMany 컬렉션 초기화
        competitions.forEach(comp -> {
            comp.getEventFees().size();
        });
        return competitions.map(CompetitionDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<CompetitionDTO> getCompFuture(Pageable pageable){
        LocalDate currentTime=LocalDate.now();
        Page<Competition> competitions = competitionRepository
                .findByCompDateStartAfterOrderByCompDateStartAsc(pageable, currentTime);
        // OneToMany 컬렉션 초기화
        competitions.forEach(comp -> {
            comp.getEventFees().size();
        });
        return competitions.map(CompetitionDTO::new);
    }

    @Transactional
    public void deleteByIdx(Integer idx){
        competitionRepository.deleteById(idx);
    }
}
