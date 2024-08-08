package CubingClubKorea.CCK_CompRank.Controller;

import CubingClubKorea.CCK_CompRank.DTO.CompListDTO;
import CubingClubKorea.CCK_CompRank.DTO.CreateRoundDTO;
import CubingClubKorea.CCK_CompRank.DTO.ParticipateDTO;
import CubingClubKorea.CCK_CompRank.DTO.RoundDTO;
import CubingClubKorea.CCK_CompRank.Entity.Participate;
import CubingClubKorea.CCK_CompRank.Repository.EventRepository;
import CubingClubKorea.CCK_CompRank.Service.*;
import CubingClubKorea.CCK_CompRank.Structure.Recorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ListController {
    private final CompListService complistService;
    private final RoundService roundService;
    private final ParticipateService participateService;
    private final AccountService accountService;
    private final EventRepository eventRepository;
    @GetMapping("/comp/{time}")
    public ResponseEntity<List<CompListDTO>> CompList(@PathVariable(value="time")String time){
        LocalDate now=LocalDate.now();
        return switch (time) {
            case "past" -> ResponseEntity.ok(complistService.getListPast(now));
            case "now" -> ResponseEntity.ok(complistService.getListToday(now));
            case "future" -> ResponseEntity.ok(complistService.getListFuture(now));
            default -> ResponseEntity.badRequest().build();
        };
    }
    @GetMapping("/compname/{compIdx}")
    public ResponseEntity<String> Comp(@PathVariable(value="compIdx")Integer compIdx){
        try{
            return ResponseEntity.ok(complistService.getOne(compIdx).getCompName());
        }
        catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @PostMapping("/comp")
    public ResponseEntity<Integer> MakeComp(@RequestBody CompListDTO compList){
        return ResponseEntity.ok(complistService.create(compList));
    }
    @DeleteMapping("/comp/{compIdx}")
    public ResponseEntity<?> DeleteComp(@PathVariable(value="compIdx") Integer compIdx){
        List<RoundDTO> list=roundService.getCompRound(compIdx);
        for (RoundDTO roundDTO : list) {
            participateService.deleteByRoundIdx(roundDTO.getIdx());
        }
        roundService.deleteRound(compIdx);
        complistService.deleteByIdx(compIdx);
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


    @PostMapping("/round")
    public ResponseEntity<List<Integer>> MakeRound(@RequestBody List<CreateRoundDTO> roundList){
        List<Integer> list=new ArrayList<>();
        for(CreateRoundDTO roundDTO:roundList){
            RoundDTO round=new RoundDTO(roundDTO);
            round.setCubeEvent(eventRepository.findByEventName(roundDTO.getCubeEvent()));
            list.add(roundService.create(round));
        }
        return ResponseEntity.ok(list);
    }
    @DeleteMapping("/round/{roundIdx}")
    public ResponseEntity<?> DeleteRound(@PathVariable(value="roundIdx")Integer roundIdx){
        roundService.deleteByRoundIdx(roundIdx);
        return ResponseEntity.ok().build();
    }
    @GetMapping(value={"/record/{roundIdx}", "/record/{roundIdx}/{search}"})
    public ResponseEntity<List<ParticipateDTO>> RecordList(@PathVariable(name="roundIdx") Integer roundIdx, @PathVariable(required=false, name="search")String search){
        if(search==null) {
            return ResponseEntity.ok(participateService.getParticipate(roundIdx));
        }
        else{
            return ResponseEntity.ok(participateService.getSearchedParticipate(roundIdx, search));
        }
    }
    @PostMapping("/record")
    public ResponseEntity<?> UpdateRecord(@RequestBody Recorder recorder, @RequestParam(name="roundIdx")Integer roundIdx ,@RequestParam(name="idx")Integer idx){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String checkerName=accountService.getUserName(authentication.getName());
        String avgType=roundService.getOne(roundIdx).getCubeEvent().getAvgCalc();
        if(avgType.equals("ao5")){
            participateService.updateRecordAo5(recorder, checkerName, idx);
        }
        else if(avgType.equals("mo3")){
            participateService.updateRecordMo3(recorder, checkerName, idx);
        }
        participateService.updateChecker(null, idx);
        List<Integer> list=participateService.getRank(roundIdx);
        for(int i=0; i<list.size(); i++) {
            log.info(String.valueOf(list.get(i)));
            participateService.updateRanking(i+1,list.get(i));
        }
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/record")
    public ResponseEntity<?> DeleteRecord(@RequestParam(name="idx")Integer idx){
        participateService.deleteParticipate(idx);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/advance/{pastRoundIdx}/{advanceRoundIdx}")//기록에서 어드밴스 인원 다음 라운드로 넘기는 콜
    public ResponseEntity<List<Integer>> Advance(@PathVariable(value="pastRoundIdx")Integer pastRoundIdx, @PathVariable(value="advanceRoundIdx")Integer advanceRoundIdx){
        RoundDTO past=roundService.getOne(pastRoundIdx);
        List<ParticipateDTO> list=participateService.getParticipate(pastRoundIdx);
        List<Integer> numList=new ArrayList<>();
        for(int i=0; i<past.getAdvance(); i++){
            ParticipateDTO p=list.get(i);
            ParticipateDTO advance=new ParticipateDTO(p.getUserName(), p.getEventName(), p.getEmail(),  advanceRoundIdx);
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
    public ResponseEntity<Integer> AddParticipate(@RequestBody List<ParticipateDTO> participates){
        for(ParticipateDTO participate:participates){
            participateService.addParticipate(participate);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/checker")//checker 갱신 콜
    public ResponseEntity<?> UpdateChecker(@RequestParam(name="idx")int idx){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String checkerName=accountService.getUserName(authentication.getName());
        participateService.updateChecker(checkerName, idx);
        return ResponseEntity.ok().build();
    }
}