package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Record.DTO.AttemptDTO;
import com.cubingclub.ranking.Record.DTO.StaffStatDTO;
import com.cubingclub.ranking.Record.Repository.CompStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompStatService {
    private final CompStatRepository compStatRepository;

    public List<AttemptDTO> getExtraByCompIdx(Integer compIdx) {
        return compStatRepository.findExtraByCompIdx(compIdx)
                .stream()
                .map(AttemptDTO::new)
                .toList();
    }

    public List<StaffStatDTO> getJudgeStatByCompIdx(Integer compIdx) {
        return compStatRepository.findJudgeStatByCompIdx(compIdx);
    }

    public List<StaffStatDTO> getScramblerStatByCompIdx(Integer compIdx) {
        return compStatRepository.findScramblerStatByCompIdx(compIdx);
    }
}
