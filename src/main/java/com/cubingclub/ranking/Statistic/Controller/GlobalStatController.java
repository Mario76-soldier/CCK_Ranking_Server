package com.cubingclub.ranking.Statistic.Controller;

import com.cubingclub.ranking.Record.DTO.RecordDTO;
import com.cubingclub.ranking.Statistic.DTO.CompRecordStatDTO;
import com.cubingclub.ranking.Statistic.Service.GlobalStatService;
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
@RequestMapping("/statistics/global")
public class GlobalStatController {
    private final GlobalStatService globalStatService;

    @GetMapping("/single/{eventName}")
    public ResponseEntity<List<RecordDTO>> getSingleGlobalTop10(@PathVariable String eventName){
        return ResponseEntity.ok(globalStatService.getGlobalTop10BySingleByEvent(eventName));
    }

    @GetMapping("/average/{eventName}")
    public ResponseEntity<List<RecordDTO>> getAverageGlobalTop10(@PathVariable String eventName){
        return ResponseEntity.ok(globalStatService.getGlobalTop10ByAverageByEvent(eventName));
    }

    @GetMapping("/comp/{eventName}")
    public ResponseEntity<List<CompRecordStatDTO>> getGlobalRecordStatByCompetition(@PathVariable String eventName){
        return ResponseEntity.ok(globalStatService.getGlobalRecordStatByCompetition(eventName));
    }
}
