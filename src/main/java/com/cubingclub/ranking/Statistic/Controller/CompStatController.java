package com.cubingclub.ranking.Statistic.Controller;

import com.cubingclub.ranking.Record.DTO.AttemptDTO;
import com.cubingclub.ranking.Statistic.DTO.StaffStatDTO;
import com.cubingclub.ranking.Statistic.Service.CompStatService;
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
@RequestMapping("/statistics/comp")
public class CompStatController {
    private final CompStatService compStatService;

    @GetMapping("/{compIdx}/judge")
    public ResponseEntity<List<StaffStatDTO>> getCompJudgeStat(@PathVariable Integer compIdx){
        return ResponseEntity.ok(compStatService.getJudgeStatByCompIdx(compIdx));
    }

    @GetMapping("/{compIdx}/scrambler")
    public ResponseEntity<List<StaffStatDTO>> getCompScramblerStat(@PathVariable Integer compIdx){
        return ResponseEntity.ok(compStatService.getScramblerStatByCompIdx(compIdx));
    }

    @GetMapping("/{compIdx}/extra")
    public ResponseEntity<List<AttemptDTO>> getCompExtraStat(@PathVariable Integer compIdx){
        return ResponseEntity.ok(compStatService.getExtraByCompIdx(compIdx));
    }
}
