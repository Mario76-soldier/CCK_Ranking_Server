package com.cubingclub.ranking.Record.Service;

import com.cubingclub.ranking.Record.DTO.PlayerCountStatDTO;
import com.cubingclub.ranking.Record.DTO.RecordDTO;
import com.cubingclub.ranking.Record.DTO.StaffCountStatDTO;
import com.cubingclub.ranking.Record.DTO.PlayerRankingDTO;
import com.cubingclub.ranking.Record.Repository.PlayerRankingRepository;
import com.cubingclub.ranking.Record.Repository.PlayerStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlayerStatService {
    private final PlayerStatRepository playerStatRepository;
    private final PlayerRankingRepository playerRankingRepository;

    public List<PlayerRankingDTO> getPlayerRankingByCckId(String cckId) {
        return playerRankingRepository.findPlayerRankingByCckId(cckId);
    }

    public List<RecordDTO> getPlayerHistoriesByCckIdAndEventName(String cckId, String eventName) {
        return playerStatRepository.findHistoryByCckIdAndEventName(cckId, eventName)
                .stream()
                .map(RecordDTO::new)
                .toList();
    }

    public List<RecordDTO> getBreakRecordsByCckId(String cckId) {
        return playerStatRepository.findBreakRecordByCckId(cckId)
                .stream()
                .map(RecordDTO::new)
                .toList();
    }

    public PlayerCountStatDTO getPlayerCountByCckId(String cckId) {
        return playerStatRepository.findPlayerCountByCckId(cckId);
    }

    public StaffCountStatDTO getStaffCountByCckId(String cckId) {
        return playerStatRepository.findStaffCountByCckId(cckId);
    }
}
