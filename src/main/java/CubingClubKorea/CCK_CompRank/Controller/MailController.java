package CubingClubKorea.CCK_CompRank.Controller;

import CubingClubKorea.CCK_CompRank.DTO.EmailCheckDTO;
import CubingClubKorea.CCK_CompRank.DTO.EmailRequestDTO;
import CubingClubKorea.CCK_CompRank.Service.AccountService;
import CubingClubKorea.CCK_CompRank.Service.MailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailSendService mailService;
    private final AccountService accountService;
    @PostMapping ("/mailsend")
    public String mailSend(@Valid EmailRequestDTO emailDto){
        System.out.println("이메일 인증 요청이 들어옴");
        System.out.println("이메일 인증 이메일 :"+emailDto.getEmail());
        return mailService.joinEmail(emailDto.getEmail());
    }
    @PostMapping("/mailauthcheck")
    public String AuthCheck(@Valid EmailCheckDTO emailCheckDto){
        Boolean Checked=mailService.CheckAuthNum(emailCheckDto.getEmail(),emailCheckDto.getAuthNum());
        if(Checked){
            return "ok";
        }
        else{
            throw new NullPointerException("뭔가 잘못!");
        }
    }

    @PostMapping("/dupcheck")
    public String DupCheck(@Valid EmailRequestDTO email){
        if(accountService.checkEmailDuplicate(email.getEmail())) {
            return "error";
        }
        else{
            return "ok";
        }
    }
}
