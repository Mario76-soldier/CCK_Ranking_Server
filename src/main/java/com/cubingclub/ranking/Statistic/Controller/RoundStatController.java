package com.cubingclub.ranking.Statistic.Controller;

import com.cubingclub.ranking.Record.DTO.*;
import com.cubingclub.ranking.Statistic.Service.RoundStatService;
import com.cubingclub.ranking.Statistic.DTO.RoundGlobalStatDTO;
import com.cubingclub.ranking.Statistic.DTO.RoundStateStatDTO;
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
@RequestMapping("/statistics/round")
public class RoundStatController {
    private final RoundStatService roundStatService;

    @GetMapping("/{roundIdx}/global")
    public ResponseEntity<RoundGlobalStatDTO> getRoundStateAverageAvg(@PathVariable Integer roundIdx){
        return ResponseEntity.ok(roundStatService.getSingleAndAverageGlobal(roundIdx));
    }

    @GetMapping("/{roundIdx}/state")
    public ResponseEntity<List<RoundStateStatDTO>> getRoundStateSingleAvg(@PathVariable Integer roundIdx){
        return ResponseEntity.ok(roundStatService.getSingleAndAverageState(roundIdx));
    }

    @GetMapping("/{roundIdx}/extra")
    public ResponseEntity<List<AttemptDTO>> getStateStat(@PathVariable Integer roundIdx){
        return ResponseEntity.ok(roundStatService.getExtraByRoundIdx(roundIdx));
    }
}
