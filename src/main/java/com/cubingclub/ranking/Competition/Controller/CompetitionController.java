package com.cubingclub.ranking.Competition.Controller;

import com.cubingclub.ranking.Competition.DTO.CompetitionDTO;
import com.cubingclub.ranking.Competition.DTO.CompetitorDTO;
import com.cubingclub.ranking.Competition.Service.CompetitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CompetitionController {
    private final CompetitionService competitionService;
    @GetMapping("/comps")
    public ResponseEntity<Map<String, List<CompetitionDTO>>> CompList(){
        LocalDate now=LocalDate.now();
        return ResponseEntity.ok(competitionService.getCompList(now));
    }
    @GetMapping("/comp/{compIdx}")
    public ResponseEntity<CompetitionDTO> CompInfo(@PathVariable(value="compIdx")Integer compIdx){
        return ResponseEntity.ok(competitionService.getOne(compIdx));
    }
    @PostMapping("/comp")
    public ResponseEntity<Integer> MakeComp(@RequestBody CompetitionDTO compList){
        return ResponseEntity.ok(competitionService.create(compList));
    }
    @DeleteMapping("/comp/{compIdx}")
    public ResponseEntity<?> DeleteComp(@PathVariable(value="compIdx") Integer compIdx){
        competitionService.deleteByIdx(compIdx);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/competitor/{compIdx}")
    public ResponseEntity<List<CompetitorDTO>> CompetitorList(@PathVariable(value="compIdx")Integer compIdx){
        return ResponseEntity.ok(competitionService.getCompetitorList(compIdx));
    }
    @PostMapping("/competitor/{compIdx}/{uid}")
    public ResponseEntity<?> UpdateAttend(@PathVariable(value = "compIdx")Integer compIdx, @PathVariable(value="uid")String uid){
        if(competitionService.updateAttend(compIdx, uid)==-1){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }
}