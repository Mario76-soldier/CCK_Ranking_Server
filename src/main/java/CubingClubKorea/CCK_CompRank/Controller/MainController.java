/*
package CubingClubKorea.CCK_CompRank.Controller;


import CubingClubKorea.CCK_CompRank.DTO.*;
import CubingClubKorea.CCK_CompRank.Entity.*;
import CubingClubKorea.CCK_CompRank.Repository.EventRepository;
import CubingClubKorea.CCK_CompRank.Service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import CubingClubKorea.CCK_CompRank.Structure.MultRound;
import CubingClubKorea.CCK_CompRank.Structure.Recorder;

import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class MainController {

    @Autowired
    private final CompListService complistService;
    @Autowired
    private final RoundService roundService;
    @Autowired
    private final ParticipateService participateService;
    @Autowired
    private final AccountService accountService;

    private final EventService eventService;

    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");


    @GetMapping("/register")
    public String Register(HttpServletResponse response, Authentication authentication, Model model) throws Exception {
        if(authentication==null){
            response.setContentType("text/html; charset=euc-kr");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('로그인이 필요합니다.'); </script>");
            out.flush();
            return "login";
        }
        else {
            Date now = new Date();
            List<CompList> compListJoin = complistService.getListFuture(now);
            model.addAttribute("joinList", compListJoin);
            return "register";
        }
    }

    @GetMapping("/regist")
    public String Regist(HttpServletResponse response, Authentication authentication, Model model, @RequestParam(name="compIdx") int compIdx) throws Exception {
        if(authentication==null){
            response.setContentType("text/html; charset=euc-kr");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('로그인이 필요합니다.'); </script>");
            out.flush();
            return "login";
        }
        else {
            LocalDateTime start=complistService.getOne(compIdx).getStartDate();
            LocalDateTime end=complistService.getOne(compIdx).getEndDate();
            LocalDateTime now=LocalDateTime.now();
            if(now.isAfter(start) && now.isBefore(end)) {
                List<Round> roundList=roundService.getCompRound(compIdx);
                Account account=accountService.getAccount(authentication.getName());
                AddPartDTO participate=new AddPartDTO();
                model.addAttribute("roundList", roundList);
                model.addAttribute("account", account);
                model.addAttribute("participate",participate);
                return "regist";
            }
            else{
                response.setContentType("text/html; charset=euc-kr");
                PrintWriter out = response.getWriter();
                out.println("<script>alert('대회 신청 기간이 아닙니다.'); </script>");
                out.flush();
                return "register";
            }
        }
    }

    @PostMapping("/regist")
    public String AddRegist(Model model, @ModelAttribute("participate")AddPartDTO participate) throws ParseException {
        for(int i=0; i<participate.getRound().size(); i++) {
            ParticipateDTO part=new ParticipateDTO();
            part.setEmail(participate.getEmail());
            part.setUserName(participate.getUserName());
            part.setRound(participate.getRound().get(i));
            part.setEventName(roundService.getOne(participate.getRound().get(i)).getCubeEvent().getEventName());
            participateService.addParticipate(part.toEntity());
        }
        return "redirect:/";
    }


    @GetMapping("/myrank")
    public String MyRank(Model model){
        return "myrank";
    }


    @GetMapping("/confirm")
    public String Confirm(Model model){
        return "confirm";
    }
}
*/
