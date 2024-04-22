package CubingClubKorea.CCK_CompRank.Controller;

import CubingClubKorea.CCK_CompRank.DTO.CompListDTO;
import CubingClubKorea.CCK_CompRank.DTO.ParticipateDTO;
import CubingClubKorea.CCK_CompRank.DTO.RoundDTO;
import CubingClubKorea.CCK_CompRank.Service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequiredArgsConstructor
public class ListController {
    @Autowired
    private final CompListService complistService;
    @Autowired
    private final RoundService roundService;
    @Autowired
    private final ParticipateService participateService;
    @Autowired
    private final AccountService accountService;
    @GetMapping("/pastlist")
    public List<CompListDTO> PastList(){
        LocalDateTime now=LocalDateTime.now();
        return complistService.getListPast(now);
    }
    @GetMapping("/nowlist")
    public List<CompListDTO> NowList(){
        LocalDateTime now=LocalDateTime.now();
        return complistService.getListToday(now);
    }
    @GetMapping("/futurelist")
    public List<CompListDTO> FutureList(){
        LocalDateTime now=LocalDateTime.now();
        return complistService.getListFuture(now);
    }
    @GetMapping("/pastround")
    public List<RoundDTO> PastRound(@RequestParam(name="compIdx") Integer compIdx){
        LocalDateTime now=LocalDateTime.now();
        return roundService.getRoundPast(now, compIdx);
    }
    @GetMapping("/nowround")
    public List<RoundDTO> NowRound(@RequestParam(name="compIdx") Integer compIdx){
        LocalDateTime now=LocalDateTime.now();
        return roundService.getRoundNow(now, compIdx);
    }
    @GetMapping("/futureround")
    public List<RoundDTO> FutureRound(@RequestParam(name="compIdx") Integer compIdx){
        LocalDateTime now=LocalDateTime.now();
        return roundService.getRoundFuture(now, compIdx);
    }
    @GetMapping("/record")
    public List<ParticipateDTO> Record(@RequestParam(name="roundIdx") Integer roundIdx, @RequestParam(required=false, name="search")String search){
        if(search==null) {
            return participateService.getParticipate(roundIdx);
        }
        else{
            return participateService.getSearchedParticipate(roundIdx, search);
        }
    }
    @PostMapping("/makecomp")
    public Integer MakeComp(@RequestBody CompListDTO compList){
        return complistService.create(compList);
    }
    @PostMapping("/deletecomp")
    public void DeleteComp(@RequestParam(name="compIdx") Integer compIdx){
        List<RoundDTO> list=roundService.getCompRound(compIdx);
        for (RoundDTO roundDTO : list) {
            participateService.deleteByRoundIdx(roundDTO.getIdx());
        }
        roundService.deleteRound(compIdx);
        complistService.deleteByIdx(compIdx);
    }
    @PostMapping("/makeround")
    public List<Integer> MakeRound(@RequestBody List<RoundDTO> roundList){
        List<Integer> list=new ArrayList<>();
        for(RoundDTO roundDTO:roundList){
            list.add(roundService.create(roundDTO));
        }
        return list;
    }
    @PostMapping("/deleteround")
    public void DeleteRound(@RequestParam(name="roundIdx")Integer roundIdx){
        roundService.deleteByRoundIdx(roundIdx);
    }
    @PostMapping("/updateadvance")
    public Integer UpdateAdvance(@RequestParam(name="roundIdx")Integer roundIdx, @RequestParam(name="advance")Integer advance){
        roundService.updateAdvance(roundIdx,advance);
        return roundService.getOne(roundIdx).getAdvance();
    }
    @PostMapping("/deleterecord")
    public void DeleteRecord(@RequestParam(name="idx")Integer idx){
        participateService.deleteParticipate(idx);
    }
    @PostMapping("/addparticipate")
    public Integer AddParticipate(@RequestBody ParticipateDTO participate){
        return participateService.addParticipate(participate);
    }
    @PostMapping("/advance")
    public List<Integer> Advance(@RequestParam(name="pastRoundIdx")Integer pastRoundIdx, @RequestParam(name="advanceRoundIdx")Integer advanceRoundIdx){
        RoundDTO past=roundService.getOne(pastRoundIdx);
        List<ParticipateDTO> list=participateService.getParticipate(pastRoundIdx);
        List<Integer> numList=new ArrayList<>();
        for(int i=0; i<past.getAdvance(); i++){
            ParticipateDTO p=list.get(i);
            ParticipateDTO advance=new ParticipateDTO(p.getUserName(), p.getEventName(), p.getEmail(),  advanceRoundIdx);
            numList.add(participateService.addParticipate(advance));
        }
        return numList;
    }
    /*
    @PostMapping("/updatechecker")
    public String UpdateChecker(@RequestParam(name="compIdx") int compIdx, @RequestParam(name="roundIdx") int roundIdx, @RequestParam(name="idx")int idx, Authentication authentication)throws ParseException{
        String checkerName=accountService.getUserName(authentication.getName());
        participateService.updateChecker(checkerName, idx);
        return "redirect:/record?compIdx="+compIdx+"&roundIdx="+roundIdx;
    }
    */
    /*
    @PostMapping("/record")
    public String UpdateRecord(@RequestBody Recorder recorder, @RequestParam(name="compIdx") int compIdx, @RequestParam(name="roundIdx") int roundIdx, @RequestParam(name="idx")int idx, Authentication authentication)throws ParseException{
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
            System.out.println(list.get(i));
            participateService.updateRanking(i+1,list.get(i));
        }
        return "redirect:/recordcomp?compIdx="+compIdx+"&roundIdx="+roundIdx;
    }
    */
}