package CubingClubKorea.CCK_CompRank.Controller;

import CubingClubKorea.CCK_CompRank.Config.TokenProvider;
import CubingClubKorea.CCK_CompRank.DTO.AccountDTO;
import CubingClubKorea.CCK_CompRank.DTO.AccountRequest;
import CubingClubKorea.CCK_CompRank.DTO.AccountResponseDTO;
import CubingClubKorea.CCK_CompRank.Entity.Account;
import CubingClubKorea.CCK_CompRank.Service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountService accountService;
    private final TokenProvider tokenProvider;
    @PostMapping("/login")
    public ResponseEntity<?> Login(@Valid @RequestBody AccountDTO accountDTO){
        if(accountDTO.getEmail()==null){
            throw new RuntimeException("Invalid email value");
        }
        if(accountDTO.getPassword()==null){
            throw new RuntimeException("Invalid password value");
        }
        final AccountRequest accountRequest=new AccountRequest(accountDTO.getEmail(), accountDTO.getPassword());
        Account account=accountService.login(accountRequest);
        if(account!=null) {
            final String token = tokenProvider.create(account);
            final AccountResponseDTO accountResponseDTO=new AccountResponseDTO(account.getEmail(),account.getName(),token);
            return ResponseEntity.ok(accountResponseDTO);
        }
        else{
            return ResponseEntity.badRequest().body("No User Exists");
        }
    }
    @PostMapping("/join")
    public ResponseEntity<?> AddJoin(@Valid @RequestBody AccountDTO accountDTO){
        try {
            if (accountService.checkEmailDuplicate(accountDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email duplicate");
            }
            if (accountDTO.getPassword().length() < 8 || accountDTO.getPassword().length() > 16) {
                return ResponseEntity.badRequest().body("Password size should be in 8~16");
            }
            if(!accountDTO.getPassword().equals(accountDTO.getPasswordCheck())){
                return ResponseEntity.badRequest().body("Password check incorrect");
            }
            Account account=accountService.join(accountDTO);
            return ResponseEntity.ok().body(account);
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(e);
        }
    }
    @PostMapping("/edit")
    public ResponseEntity<?> Edit(@RequestBody AccountDTO accountDTO){
        return null;
    }
}
