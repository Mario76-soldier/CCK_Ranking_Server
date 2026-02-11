package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Record.DTO.AttemptDTO;
import com.cubingclub.ranking.Record.DTO.RoundGlobalStatDTO;
import com.cubingclub.ranking.Record.DTO.RoundStateStatDTO;
import com.cubingclub.ranking.Record.Repository.RoundStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoundStatService {
    private final RoundStatRepository roundStatRepository;

    public List<AttemptDTO> getExtraByRoundIdx(Integer roundIdx) {
        return roundStatRepository.findExtraByRoundIdx(roundIdx)
                .stream()
                .map(AttemptDTO::new)
                .toList();
    }

    public List<RoundStateStatDTO> getSingleAndAverageState(Integer roundIdx) {
        return roundStatRepository.findSingleAndAverageState(roundIdx);
    }
    public RoundGlobalStatDTO getSingleAndAverageGlobal(Integer roundIdx) {
        return roundStatRepository.findSingleAndAverageGlobal(roundIdx);
    }
}
