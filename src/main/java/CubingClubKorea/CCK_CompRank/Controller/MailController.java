package CubingClubKorea.CCK_CompRank.Controller;

import CubingClubKorea.CCK_CompRank.DTO.EmailRequestDTO;
import CubingClubKorea.CCK_CompRank.Service.MailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {
    private final MailSendService mailService;
    @PostMapping ("/send")
    public ResponseEntity<?> mailSend(@Valid @RequestBody EmailRequestDTO emailDto){
        log.info("Email request created");
        log.info("Email request address :"+emailDto.getEmail());
        return ResponseEntity.ok(mailService.joinEmail(emailDto.getEmail()));
    }
}
