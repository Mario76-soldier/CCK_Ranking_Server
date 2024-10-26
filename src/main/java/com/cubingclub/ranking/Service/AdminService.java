package com.cubingclub.ranking.Service;

import com.cubingclub.ranking.DTO.AccountListDTO;
import com.cubingclub.ranking.DTO.RoleUpdateDTO;
import com.cubingclub.ranking.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final AccountRepository accountRepository;
    public List<AccountListDTO> findAllList(){
        return accountRepository.findAllList();
    }
    public String updatePosition(RoleUpdateDTO roleUpdateDTO){
        return accountRepository.save(accountRepository.findByEmail(roleUpdateDTO.getEmail()).orElseThrow().roleUpdate(roleUpdateDTO.getPosition())).getEmail();
    }
}
