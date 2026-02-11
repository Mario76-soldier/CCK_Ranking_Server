package com.cubingclub.ranking.Statistic.Controller;

import com.cubingclub.ranking.Statistic.DTO.PlayerCountStatDTO;
import com.cubingclub.ranking.Statistic.DTO.PlayerRankingDTO;
import com.cubingclub.ranking.Record.DTO.RecordDTO;
import com.cubingclub.ranking.Statistic.DTO.StaffCountStatDTO;
import com.cubingclub.ranking.Statistic.Service.PlayerStatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics/player")
public class PlayerStatController {
    private final PlayerStatService playerStatService;

    @GetMapping("/{cckId}/ranking")
    public ResponseEntity<List<PlayerRankingDTO>> getPlayerRanking(@PathVariable String cckId){
        return ResponseEntity.ok(playerStatService.getPlayerRankingByCckId(cckId));
    }

    @GetMapping("/{cckId}/history/{eventName}")
    public ResponseEntity<List<RecordDTO>> getPlayerHistory(@PathVariable String cckId, @PathVariable String eventName){
        return ResponseEntity.ok(playerStatService.getPlayerHistoriesByCckIdAndEventName(cckId, eventName));
    }

    @GetMapping("/{cckId}/break")
    public ResponseEntity<List<RecordDTO>> getBreakRecords(@PathVariable String cckId){
        return ResponseEntity.ok(playerStatService.getBreakRecordsByCckId(cckId));
    }

    @GetMapping("/{cckId}/staff-count")
    public ResponseEntity<StaffCountStatDTO> getStaffCount(@PathVariable String cckId){
        return ResponseEntity.ok(playerStatService.getStaffCountByCckId(cckId));
    }

    @GetMapping("/{cckId}/player-count")
    public ResponseEntity<PlayerCountStatDTO> getPlayerCount(@PathVariable String cckId){
        return ResponseEntity.ok(playerStatService.getPlayerCountByCckId(cckId));
    }
}
