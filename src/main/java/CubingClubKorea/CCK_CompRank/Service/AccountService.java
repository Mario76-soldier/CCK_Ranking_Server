package CubingClubKorea.CCK_CompRank.Service;

import CubingClubKorea.CCK_CompRank.DTO.AccountDTO;
import CubingClubKorea.CCK_CompRank.DTO.AccountRequest;
import CubingClubKorea.CCK_CompRank.Entity.Account;
import CubingClubKorea.CCK_CompRank.Entity.UserRole;
import CubingClubKorea.CCK_CompRank.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder encoder;

    public boolean checkEmailDuplicate(String email){
        return accountRepository.existsByEmail(email);
    }
    public Account join(AccountDTO accountDTO){
        return accountRepository.save(accountDTO.toEntity(encoder.encode(accountDTO.getPassword())));
    }
    public Account login(AccountRequest accountRequest){
        Optional<Account> account=accountRepository.findByEmail(accountRequest.getEmail());
        if(account.isEmpty()){
            return null;
        }
        Account account1=account.get();
        if(!encoder.matches(accountRequest.getPassword(), account1.getPassword())){
            return null;
        }
        return account1;
    }
    public String getUserName(String email){
        return accountRepository.findByEmail(email).get().getName();
    }
    public UserRole getUserRole(String email){
        return accountRepository.findByEmail(email).get().getPosition();
    }
}
