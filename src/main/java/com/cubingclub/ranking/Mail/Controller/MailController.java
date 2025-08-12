package com.cubingclub.ranking.Mail.Controller;

import com.cubingclub.ranking.Account.DTO.EmailRequestDTO;
import com.cubingclub.ranking.Account.DTO.PwReissueRequestDTO;
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
    @PostMapping ("/send/signup")
    public ResponseEntity<?> signupMailSend(@Valid @RequestBody EmailRequestDTO emailDto){
        log.info("Email request created");
        log.info("Email request address :"+emailDto.getEmail());
        if(Objects.equals(mailService.joinEmail(emailDto.getEmail()), -1)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email duplicate");
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping ("/send/password")
    public ResponseEntity<?> verifyPassMailSend(@Valid @RequestBody EmailRequestDTO emailDto){
        log.info("Email request created");
        log.info("Email request address :"+emailDto.getEmail());
        if(Objects.equals(mailService.verifyPassEmail(emailDto.getEmail()), -1)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Email doesn't exist");
        }
        return ResponseEntity.ok().build();
    }
    @PostMapping ("/password")//비밀번호 무작정 바꿀 수도 있기 때문에 이에 대한 대책 필요할 것...
    public ResponseEntity<?> pwMailSend(@Valid @RequestBody PwReissueRequestDTO pwReissueRequestDTO){
        log.info("Email request created");
        log.info("Email request address :"+pwReissueRequestDTO.getEmail());
        Integer ret=mailService.passwordEmail(pwReissueRequestDTO);
        if(Objects.equals(ret, -2)){
            return ResponseEntity.badRequest().body("Mail authenticate incorrect");
        }
        else if(Objects.equals(ret, -1)){
            return ResponseEntity.internalServerError().body("Failed to create password");
        }
        return ResponseEntity.ok().build();
    }
}
