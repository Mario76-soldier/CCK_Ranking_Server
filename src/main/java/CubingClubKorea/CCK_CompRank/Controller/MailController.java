package CubingClubKorea.CCK_CompRank.Controller;

import CubingClubKorea.CCK_CompRank.DTO.EmailCheckDTO;
import CubingClubKorea.CCK_CompRank.DTO.EmailRequestDTO;
import CubingClubKorea.CCK_CompRank.Service.AccountService;
import CubingClubKorea.CCK_CompRank.Service.MailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {
    private final MailSendService mailService;
    private final AccountService accountService;
    @PostMapping ("/send")
    public ResponseEntity<?> mailSend(@Valid EmailRequestDTO emailDto){
        log.info("Email request created");
        log.info("Email request address :"+emailDto.getEmail());
        return ResponseEntity.ok(mailService.joinEmail(emailDto.getEmail()));
    }
    @PostMapping("/check")
    public ResponseEntity<?> AuthCheck(@Valid EmailCheckDTO emailCheckDto){
        boolean Checked=mailService.CheckAuthNum(emailCheckDto.getEmail(),emailCheckDto.getAuthNum());
        if(Checked){
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.internalServerError().body("Something Issues");
        }
    }
    @PostMapping("/duplicate")
    public ResponseEntity<?> DupCheck(@Valid EmailRequestDTO email){
        if(accountService.checkEmailDuplicate(email.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        else{
            return ResponseEntity.ok().build();
        }
    }
}
