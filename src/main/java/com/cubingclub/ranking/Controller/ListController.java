package com.cubingclub.ranking.Controller;

import com.cubingclub.ranking.DTO.*;
import com.cubingclub.ranking.Entity.CubeEvent;
import com.cubingclub.ranking.Entity.Participate;
import com.cubingclub.ranking.Service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ListController {
    private final CompetitionService competitionService;
    private final RoundService roundService;
    private final ParticipateService participateService;
    private final AccountService accountService;
    private final PersonalService personalService;
    @GetMapping("/comp/{time}")
    public ResponseEntity<List<CompetitionDTO>> CompList(@PathVariable(value="time")String time){
        LocalDate now=LocalDate.now();
        return switch (time) {
            case "past" -> ResponseEntity.ok(competitionService.getListPast(now));
            case "now" -> ResponseEntity.ok(competitionService.getListToday(now));
            case "future" -> ResponseEntity.ok(competitionService.getListFuture(now));
            default -> ResponseEntity.badRequest().build();
        };
    }
    @GetMapping("/compname/{compIdx}")
    public ResponseEntity<String> Comp(@PathVariable(value="compIdx")Integer compIdx){
        try{
            return ResponseEntity.ok(competitionService.getOne(compIdx).getCompName());
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/compinfo/{compIdx}")
    public ResponseEntity<CompetitionDTO> CompInfo(@PathVariable(value="compIdx")Integer compIdx){
        try{
            return ResponseEntity.ok(competitionService.getOne(compIdx));
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/comp")
    public ResponseEntity<Integer> MakeComp(@RequestBody CompetitionDTO compList){
        return ResponseEntity.ok(competitionService.create(compList));
    }

    @DeleteMapping("/comp/{compIdx}")
    public ResponseEntity<?> DeleteComp(@PathVariable(value="compIdx") Integer compIdx){
        List<RoundDTO> list=roundService.getCompRound(compIdx);
        for (RoundDTO roundDTO : list) {
            participateService.deleteByRoundIdx(roundDTO.getIdx());
        }
        roundService.deleteRound(compIdx);
        competitionService.deleteByIdx(compIdx);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/round/{time}/{compIdx}")
    public ResponseEntity<List<RoundDTO>> RoundList(@PathVariable(value="compIdx") Integer compIdx, @PathVariable(value = "time")String time){
        LocalDateTime now=LocalDateTime.now();
        return switch (time) {
            case "past" -> ResponseEntity.ok(roundService.getRoundPast(now, compIdx));
            case "now" -> ResponseEntity.ok(roundService.getRoundNow(now, compIdx));
            case "future" -> ResponseEntity.ok(roundService.getRoundFuture(now, compIdx));
            default -> ResponseEntity.badRequest().build();
        };
    }
    @GetMapping("/roundinfo/{roundIdx}")
    public ResponseEntity<RoundDTO> RoundInfo(@PathVariable(value="roundIdx")Integer roundIdx){
        try{
            return ResponseEntity.ok(roundService.getOne(roundIdx));
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/round")
    public ResponseEntity<List<Integer>> MakeRound(@RequestBody List<CreateRoundDTO> roundList){
        List<Integer> list=new ArrayList<>();
        for(CreateRoundDTO roundDTO:roundList){
            list.add(roundService.create(roundDTO));
        }
        return ResponseEntity.ok(list);
    }
    @DeleteMapping("/round/{roundIdx}")
    public ResponseEntity<?> DeleteRound(@PathVariable(value="roundIdx")Integer roundIdx){
        roundService.deleteByRoundIdx(roundIdx);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/record/{roundIdx}")
    public ResponseEntity<List<ParticipateDTO>> RecordList(@PathVariable(value="roundIdx") Integer roundIdx){
        return ResponseEntity.ok(participateService.getParticipate(roundIdx));
    }
    @PostMapping("/record/{roundIdx}/{idx}")
    public ResponseEntity<?> UpdateRecord(@RequestBody Records records, @PathVariable(value="roundIdx")Integer roundIdx , @PathVariable(value="idx")Integer idx){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String checkerName=accountService.getUserName(authentication.getName());
        CubeEvent cubeEvent=roundService.getOne(roundIdx).getCubeEvent();
        String eventName=cubeEvent.getEventName();
        String avgType=cubeEvent.getAvgCalc();
        participateService.updateRecord(records.getFirst(), records.getSecond(), records.getThird(), records.getFourth(), records.getFifth(), checkerName, idx,avgType);
        ParticipateDTO participateDTO=participateService.getOne(idx);
        List<Integer> list=participateService.getRank(roundIdx);
        for(int i=0; i<list.size(); i++) {
            participateService.updateRanking(i+1,list.get(i));
        }
        personalService.updateBest(idx, participateDTO.getAccount().getEmail(), eventName, participateDTO.getSingle(),participateDTO.getAverage());
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/record/{idx}")
    public ResponseEntity<?> DeleteRecord(@PathVariable(value="idx")Integer idx){
        Integer roundIdx=participateService.getOne(idx).getRound().getIdx();
        participateService.deleteParticipate(idx);
        List<Integer> list=participateService.getRank(roundIdx);
        for(int i=0; i<list.size(); i++) {
            participateService.updateRanking(i+1,list.get(i));
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping("/advance/{pastRoundIdx}/{advanceRoundIdx}")//기록에서 어드밴스 인원 다음 라운드로 넘기는 콜
    public ResponseEntity<List<Integer>> Advance(@PathVariable(value="pastRoundIdx")Integer pastRoundIdx, @PathVariable(value="advanceRoundIdx")Integer advanceRoundIdx){
        RoundDTO past=roundService.getOne(pastRoundIdx);
        List<ParticipateDTO> list=participateService.getParticipate(pastRoundIdx);
        List<Integer> numList=new ArrayList<>();
        for(int i=0; i<past.getAdvance(); i++){
            ParticipateDTO p=list.get(i);
            ParticipateRequestDTO advance=new ParticipateRequestDTO(p.getPdx(), p.getAccount().getEmail(), advanceRoundIdx);
            numList.add(participateService.addParticipate(advance));
        }
        return ResponseEntity.ok(numList);
    }
    @PutMapping("/advance/{roundIdx}/{advance}")//어드밴스 인원 조정 콜
    public ResponseEntity<Integer> UpdateAdvance(@PathVariable(value="roundIdx")Integer roundIdx, @PathVariable(value="advance")Integer advance) {
        roundService.updateAdvance(roundIdx, advance);
        return ResponseEntity.ok(roundService.getOne(roundIdx).getAdvance());
    }
    @PostMapping("/participate")//선수 추가 콜
    public ResponseEntity<Integer> AddParticipate(@RequestBody List<ParticipateRequestDTO> participates){
        for(ParticipateRequestDTO participate:participates){
            participateService.addParticipate(participate);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/checker/{idx}")//checker 갱신 콜
    public ResponseEntity<?> UpdateChecker(@PathVariable(value="idx")Integer idx){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String checkerName=accountService.getUserName(authentication.getName());
        participateService.updateChecker(checkerName, idx);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/event")
    public ResponseEntity<?> EventList(){
        return ResponseEntity.ok(roundService.getCubeEvent());
    }
}