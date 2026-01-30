package com.cubingclub.ranking.Competition.Controller;

import com.cubingclub.ranking.Competition.DTO.*;
import com.cubingclub.ranking.Competition.Service.CompetitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comp")
public class CompetitionController {
    private final CompetitionService competitionService;

    @GetMapping("/past")
    public ResponseEntity<Page<CompetitionDTO>> getCompPast(Pageable pageable){
        return ResponseEntity.ok(competitionService.getCompPast(pageable));
    }

    @GetMapping("/now")
    public ResponseEntity<Page<CompetitionDTO>> getCompNow(Pageable pageable){
        return ResponseEntity.ok(competitionService.getCompNow(pageable));
    }

    @GetMapping("/future")
    public ResponseEntity<Page<CompetitionDTO>> getCompFuture(Pageable pageable){
        return ResponseEntity.ok(competitionService.getCompFuture(pageable));
    }

    @GetMapping("/join/past")
    public ResponseEntity<Page<CompetitionDTO>> getJoinPast(Pageable pageable){
        return ResponseEntity.ok(competitionService.getJoinPast(pageable));
    }

    @GetMapping("/join/now")
    public ResponseEntity<Page<CompetitionDTO>> getJoinNow(Pageable pageable){
        return ResponseEntity.ok(competitionService.getJoinNow(pageable));
    }

    @GetMapping("/join/future")
    public ResponseEntity<Page<CompetitionDTO>> getJoinFuture(Pageable pageable){
        return ResponseEntity.ok(competitionService.getJoinFuture(pageable));
    }

    @GetMapping("/{compIdx}")
    public ResponseEntity<CompetitionDTO> getComp(@PathVariable Integer compIdx){
        return ResponseEntity.ok(competitionService.getOne(compIdx));
    }

    @GetMapping("/schedule/{compIdx}")
    public ResponseEntity<CompetitionScheduleDTO> getCompSchedule(@PathVariable Integer compIdx){
        return ResponseEntity.ok(competitionService.getOneSchedule(compIdx));
    }

    @PostMapping("")
    public ResponseEntity<Void> createComp(@Valid @RequestBody CompetitionCreateDTO competitionCreateDTO){
        competitionService.create(competitionCreateDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("")
    public ResponseEntity<Void> updateComp(@Valid @RequestBody CompetitionUpdateDTO competitionUpdateDTO){
        competitionService.update(competitionUpdateDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{compIdx}")
    public ResponseEntity<?> deleteComp(@PathVariable Integer compIdx){
        competitionService.deleteByIdx(compIdx);
        return ResponseEntity.ok().build();
    }
}