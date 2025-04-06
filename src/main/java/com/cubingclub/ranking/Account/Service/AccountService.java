package com.cubingclub.ranking.Account.Service;

import com.cubingclub.ranking.Account.DTO.*;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Account.Entity.UserRole;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder encoder;

    public boolean checkEmailDuplicate(String email){
        return accountRepository.existsByEmail(email);
    }
    public Account join(AccountJoinDTO accountJoinDTO){
        return accountRepository.save(accountJoinDTO.toEntity(encoder.encode(accountJoinDTO.getPassword())));
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
        if(accountRepository.findByEmail(email).isPresent()) {
            return accountRepository.findByEmail(email).get().getName();
        }
        else{
            return null;
        }
    }
    public UserRole getUserRole(String email){
        return accountRepository.findByEmail(email).get().getPosition();
    }
    public int updateUser(AccountEditDTO accountEditDTO){
        Optional<Account> account=accountRepository.findByEmail(accountEditDTO.getEmail());
        if(account.isEmpty()){
            return -1;
        }
        if(!encoder.matches(accountEditDTO.getPassword(), account.get().getPassword())){
            return -1;
        }
        if(accountEditDTO.getNewPassword()==null){
            accountRepository.updateAccountInfo(accountEditDTO.getPhoneNum(), accountEditDTO.getState(), accountEditDTO.getEmail());
            return 0;
        }
        else{
            if(accountEditDTO.getNewPassword().equals(accountEditDTO.getNewPasswordCheck())) {
                accountRepository.updateAccountPassword(encoder.encode(accountEditDTO.getNewPassword()), accountEditDTO.getEmail());
                return 0;
            }
            else{
                return 1;
            }
        }
    }
    public AccountDTO getAccountInfo(String email){
        return new AccountDTO(accountRepository.findByEmail(email).get());
    }

    public boolean updatePassword(String email, String password){
        if(accountRepository.existsByEmail(email)){
            accountRepository.updateAccountPassword(encoder.encode(password), email);
            return true;
        }
        return false;
    }

    public List<AccountListDTO> findBySearchTerm(String searchTerm){
        return accountRepository.findByName(searchTerm);
    }
}
