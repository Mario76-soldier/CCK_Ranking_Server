package com.cubingclub.ranking.Account.Controller;

import com.cubingclub.ranking.Config.TokenProvider;
import com.cubingclub.ranking.Account.DTO.AccountJoinDTO;
import com.cubingclub.ranking.Account.DTO.AccountEditDTO;
import com.cubingclub.ranking.Account.DTO.AccountRequest;
import com.cubingclub.ranking.Account.DTO.AccountResponseDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Account.Entity.UserRole;
import com.cubingclub.ranking.Account.Service.AccountService;
import com.cubingclub.ranking.Mail.Service.MailSendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AccountController {
    private final AccountService accountService;
    private final TokenProvider tokenProvider;
    private final MailSendService mailSendService;
    @PostMapping("/login")
    public ResponseEntity<?> Login(@Valid @RequestBody AccountJoinDTO accountJoinDTO){
        if(accountJoinDTO.getEmail()==null){
            throw new RuntimeException("Invalid email value");
        }
        if(accountJoinDTO.getPassword()==null){
            throw new RuntimeException("Invalid password value");
        }
        final AccountRequest accountRequest=new AccountRequest(accountJoinDTO.getEmail(), accountJoinDTO.getPassword());
        Account account=accountService.login(accountRequest);
        if(account!=null) {
            final String token = tokenProvider.create(account);
            final AccountResponseDTO accountResponseDTO=new AccountResponseDTO(account.getEmail(),account.getName(), account.getEnName(), account.getPosition(), token);
            return ResponseEntity.ok(accountResponseDTO);
        }
        else{
            return ResponseEntity.badRequest().body("No User Exists");
        }
    }
    @PostMapping("/join")
    public ResponseEntity<?> AddJoin(@Valid @RequestBody AccountJoinDTO accountJoinDTO){
        try {
            if (accountService.checkEmailDuplicate(accountJoinDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email duplicate");
            }
            if (accountJoinDTO.getPassword().length() < 8 || accountJoinDTO.getPassword().length() > 16) {
                return ResponseEntity.badRequest().body("Password size should be in 8~16");
            }
            if (!accountJoinDTO.getPassword().equals(accountJoinDTO.getPasswordCheck())){
                return ResponseEntity.badRequest().body("Password check incorrect");
            }
            if (!mailSendService.CheckAuthNum(accountJoinDTO.getEmail(), accountJoinDTO.getAuthNum())){
                return ResponseEntity.badRequest().body("Mail authenticate incorrect");
            }
            Account account=accountService.join(accountJoinDTO);
            return ResponseEntity.ok().body(account);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
    @GetMapping("/role")
    public ResponseEntity<?> CheckRole(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserRole myPosition = accountService.getUserRole(authentication.getName());
            return ResponseEntity.ok().body(myPosition);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
    @PostMapping("/edit")
    public ResponseEntity<?> Edit(@RequestBody AccountEditDTO accountEditDTO){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            accountEditDTO.setEmail(authentication.getName());
            if(accountService.updateUser(accountEditDTO)==0) {
                return ResponseEntity.ok().build();
            }
            else{
                return ResponseEntity.badRequest().build();
            }
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
    @GetMapping("/info")
    public ResponseEntity<?> Info(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            return ResponseEntity.ok(accountService.getAccountInfo(authentication.getName()));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
}
