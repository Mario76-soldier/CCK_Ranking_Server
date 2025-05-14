package com.cubingclub.ranking.RFID.Service;

import com.cubingclub.ranking.Account.DTO.AccountDTO;
import com.cubingclub.ranking.Account.Entity.Account;
import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.RFID.DTO.RFIDCreateDTO;
import com.cubingclub.ranking.RFID.Entity.RFID;
import com.cubingclub.ranking.RFID.Repository.RFIDRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RFIDService {
    private final RFIDRepository rfidRepository;
    private final AccountRepository accountRepository;
    public void createRFID(RFIDCreateDTO rfidCreateDTO){
        if(rfidRepository.findAccountByUid(rfidCreateDTO.getUid())==null)
            rfidRepository.save(rfidCreateDTO.toEntity(accountRepository));
        else{
            return;
        }
    }
    public AccountDTO getAccountInfo(String uid){
        RFID rfid=rfidRepository.findAccountByUid(uid);
        if(rfid==null){
            return null;
        }
        return new AccountDTO(rfid.getAccount());
    }
    public String getEmail(String uid){
        return rfidRepository.findEmailByUid(uid);
    }
    public void deleteRFID(String email){
        rfidRepository.deleteByAccount_email(email);
    }
}
