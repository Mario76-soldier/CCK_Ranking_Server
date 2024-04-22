package CubingClubKorea.CCK_CompRank.Controller;

import CubingClubKorea.CCK_CompRank.DTO.CompListDTO;
import CubingClubKorea.CCK_CompRank.DTO.ParticipateDTO;
import CubingClubKorea.CCK_CompRank.DTO.RoundDTO;
import CubingClubKorea.CCK_CompRank.Entity.CompList;
import CubingClubKorea.CCK_CompRank.Entity.CubeEvent;
import CubingClubKorea.CCK_CompRank.Entity.Participate;
import CubingClubKorea.CCK_CompRank.Entity.Round;
import CubingClubKorea.CCK_CompRank.Service.*;
import CubingClubKorea.CCK_CompRank.Structure.MultRound;
import CubingClubKorea.CCK_CompRank.Structure.Recorder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
    @Autowired
    private final EventService eventService;
    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

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
        List<ParticipateDTO> participates;
        if(search==null) {
            participates = participateService.getParticipate(roundIdx);
        }
        else{
            participates=participateService.getSearchedParticipate(roundIdx, search);
        }
        return participates;
    }

    /*@PreAuthorize("hasAuthority('admin')")
    @GetMapping("/makecomp")
    public String MakeComp(Model model){
        model.addAttribute("compList",new CompListDTO());
        return "makecomp";
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/makecomp")
    public String NewComp(@ModelAttribute("compList")CompListDTO compList){
        int index=complistService.create(compList);
        int countRound=complistService.getOne(index).getCountRound();
        return "redirect:/makeround?countRound="+countRound+"&compIdx="+index;
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/deletecomp")
    public String DeleteComp(@RequestParam(name="idx") int idx){
        List<Round> list=roundService.getCompRound(idx);
        for(int i=0; i<list.size(); i++) {
            participateService.deleteByRoundIdx(list.get(i).getIdx());
        }
        roundService.deleteRound(idx);
        complistService.deleteByIdx(idx);
        return "redirect:/";
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/makeround")
    public String MakeRound(@RequestParam(name="countRound") int countRound, @RequestParam(name="compIdx") int compIdx, Model model){
        model.addAttribute("countRound", countRound);
        MultRound round=new MultRound();
        model.addAttribute("round", round);
        model.addAttribute("compIdx", compIdx);
        return "makeround";
    }
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/makeround")
    public String NewRound(@ModelAttribute MultRound round, @RequestParam(name="compIdx")int compIdx) throws ParseException {
        for(int i=0; i<round.getEventStart().length; i++){
            RoundDTO roundDTO=new RoundDTO();
            roundDTO.setCompIdx(compIdx);
            roundDTO.setSeq(i+1);
            roundDTO.setRound(round.getRound().get(i));
            CubeEvent cubeEvent=eventService.findByEventName(round.getEventName().get(i));
            roundDTO.setCubeEvent(cubeEvent);
            roundDTO.setEventStart(formatter.parse(round.getEventStart()[i]));
            roundDTO.setEventEnd(formatter.parse(round.getEventEnd()[i]));
            roundDTO.setAdvance(round.getAdvance().get(i));
            roundService.create(roundDTO);
        }
        return "redirect:/";
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/deleteround")
    public String DeleteRound(@RequestParam(name="compIdx")int compIdx, @RequestParam(name="roundIdx")int roundIdx) throws ParseException {
        roundService.deleteByRoundIdx(roundIdx);
        return "redirect:/round?compIdx="+compIdx;
    }

    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/recordcomp")
    public String RecordComp(@RequestParam(name="compIdx") int compIdx, @RequestParam(name="roundIdx") int roundIdx, @RequestParam(required = false, name = "search")String search, Model model){
        CompList comp=complistService.getOne(compIdx);
        Round round=roundService.getOne(roundIdx);
        List<Participate> participates;
        if(search==null) {
            participates = participateService.getParticipate(roundIdx);
        }
        else{
            participates=participateService.getSearchedParticipate(roundIdx, search);
        }
        model.addAttribute("compIdx",compIdx);
        model.addAttribute("roundIdx",roundIdx);
        model.addAttribute("comp",comp);
        model.addAttribute("round",round);
        model.addAttribute("partList",participates);
        Recorder recorder=new Recorder();
        model.addAttribute("recorder",recorder);
        return "recordcomp";
    }
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/recordcomp")
    public String UpdateRecord(@ModelAttribute Recorder recorder, @RequestParam(name="compIdx") int compIdx, @RequestParam(name="roundIdx") int roundIdx, @RequestParam(name="idx")int idx, Authentication authentication)throws ParseException{
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

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/updatechecker")
    public String UpdateChecker(@RequestParam(name="compIdx") int compIdx, @RequestParam(name="roundIdx") int roundIdx, @RequestParam(name="idx")int idx, Authentication authentication)throws ParseException{
        String checkerName=accountService.getUserName(authentication.getName());
        participateService.updateChecker(checkerName, idx);
        return "redirect:/record?compIdx="+compIdx+"&roundIdx="+roundIdx;
    }
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/updateadvance")
    public String UpdateAdvance(@RequestParam(name="compIdx") int compIdx, @RequestParam(name="roundIdx")int roundIdx, Model model, HttpServletRequest request){
        roundService.updateAdvance(roundIdx,Integer.parseInt(request.getParameter("advance")));
        return "redirect:/recordcomp?compIdx="+compIdx+"&roundIdx="+roundIdx;
    }
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/deleterecord")
    public String UpdateRecord(@RequestParam(name="compIdx") int compIdx, @RequestParam(name="roundIdx") int roundIdx, @RequestParam(name="idx")int idx)throws ParseException{
        participateService.deleteParticipate(idx);
        return "redirect:/recordcomp?compIdx="+compIdx+"&roundIdx="+roundIdx;
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/addparticipate")
    public String AddParticipate(@RequestParam(name="compIdx") int compIdx, @RequestParam(name="roundIdx") int roundIdx, Model model){
        CompList comp=complistService.getOne(compIdx);
        Round round=roundService.getOne(roundIdx);
        model.addAttribute("compIdx",compIdx);
        model.addAttribute("roundIdx",roundIdx);
        model.addAttribute("comp",comp);
        model.addAttribute("round",round);
        model.addAttribute("participate",new ParticipateDTO());
        return "addparticipate";
    }
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/addparticipate")
    public String AddingParticipate(@ModelAttribute("participate")ParticipateDTO participate, @RequestParam(name="compIdx") int compIdx, @RequestParam(name="roundIdx") int roundIdx){
        participateService.addParticipate(participate.toEntity());
        return "redirect:/recordcomp?compIdx="+compIdx+"&roundIdx="+roundIdx;
    }
    @PreAuthorize("hasAuthority('admin')")
    @GetMapping("/advance")
    public String Advance(@RequestParam(name="compIdx")int compIdx, @RequestParam(name="roundIdx")int roundIdx, Model model){
        List<Round> list=roundService.getCompRound(compIdx);
        model.addAttribute("compIdx", compIdx);
        model.addAttribute("roundIdx",roundIdx);
        model.addAttribute("list",list);
        return "advance";
    }
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/advance")
    public String sendAdvance(@RequestParam(name="compIdx")int compIdx, @RequestParam(name="pastRoundIdx")int pastRoundIdx,  @RequestParam(name="nowRoundIdx")int nowRoundIdx){
        Round past=roundService.getOne(pastRoundIdx);
        List<Participate> list=participateService.getParticipate(pastRoundIdx);
        for(int i=0; i<past.getAdvance(); i++){
            Participate p=list.get(i);
            Participate advance=new Participate(p.getUserName(), p.getEmail(), p.getEventName(), nowRoundIdx);
            participateService.addParticipate(advance);
        }
        return "redirect:/round?compIdx="+compIdx;
    }*/
}