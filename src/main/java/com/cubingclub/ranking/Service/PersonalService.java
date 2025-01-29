package com.cubingclub.ranking.Service;

import com.cubingclub.ranking.DTO.PersonalRecordDTO;
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
    public Map<String, List<PersonalRecordDTO>> getPersonalRecord(String email){
        List<Participate> participates=participateRepository.findByEmailOrderByRound_CubeEvent_EventName(email);
        Map<Integer, Long> roundParticipantCounts = participateRepository.findAllRoundParticipantCounts();
        return participates.stream()
                .map(PersonalRecordDTO::new)
                .peek(personalRecordDTO ->
                        personalRecordDTO.setRankingAll(
                                Math.toIntExact(roundParticipantCounts.get(personalRecordDTO.getRound().getIdx()))
                        )
                )
                .collect(Collectors.groupingBy(personalRecordDTO -> personalRecordDTO.getRound().getCubeEvent().getEventName()));
    }
}
