package com.cubingclub.ranking.Statistic.Controller;

import com.cubingclub.ranking.Statistic.DTO.StateRecordDTO;
import com.cubingclub.ranking.Statistic.DTO.StateStatDTO;
import com.cubingclub.ranking.Statistic.Service.StateStatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics/state")
public class StateStatController {
    private final StateStatService stateStatService;

    @GetMapping("/single/{eventName}")
    public ResponseEntity<Map<String, List<StateRecordDTO>>> getSingleStateTop3(@PathVariable String eventName){
        return ResponseEntity.ok(stateStatService.getStateSingleTop3(eventName));
    }

    @GetMapping("/average/{eventName}")
    public ResponseEntity<Map<String, List<StateRecordDTO>>> getAverageStateTop3(@PathVariable String eventName){
        return ResponseEntity.ok(stateStatService.getStateAverageTop3(eventName));
    }

    @GetMapping("/summary/{eventName}")
    public ResponseEntity<List<StateStatDTO>> getStateStat(@PathVariable String eventName){
        return ResponseEntity.ok(stateStatService.getStateStat(eventName));
    }
}
