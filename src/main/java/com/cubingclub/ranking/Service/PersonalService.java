package com.cubingclub.ranking.Service;

import com.cubingclub.ranking.DTO.ParticipateDTO;
import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Repository.ParticipateRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonalService {
    private final ParticipateRepository participateRepository;

    public Map<String, List<ParticipateDTO>> getPersonalRecord(String email){
        List<Participate> participates=participateRepository.findByEmailOrderByRound_CubeEvent_EventName(email);
        return participates.stream()
                .map(ParticipateDTO::new)
                .collect(Collectors.groupingBy(participateDTO -> participateDTO.getRound().getCubeEvent().getEventName()));
    }
}
