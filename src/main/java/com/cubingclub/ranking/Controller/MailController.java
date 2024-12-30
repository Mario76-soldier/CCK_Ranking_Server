package com.cubingclub.ranking.Controller;

import com.cubingclub.ranking.DTO.EmailRequestDTO;
import com.cubingclub.ranking.Service.MailSendService;
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
}
