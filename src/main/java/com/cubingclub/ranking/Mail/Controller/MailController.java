package com.cubingclub.ranking.Mail.Controller;

import com.cubingclub.ranking.Account.DTO.EmailRequestDTO;
import com.cubingclub.ranking.Mail.Service.MailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
        if(Objects.equals(mailService.joinEmail(emailDto.getEmail()), "-1")){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email duplicate");
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping ("/password/{email}")//비밀번호 무작정 바꿀 수도 있기 때문에 이에 대한 대책 필요할 것...
    public ResponseEntity<?> pwMailSend(@PathVariable(name = "email")String email){
        log.info("Email request created");
        log.info("Email request address :"+email);
        if(Objects.equals(mailService.passwordEmail(email), "-1")){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok().build();
    }
}
