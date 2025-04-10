package com.cubingclub.ranking.RFID.Service;

import com.cubingclub.ranking.Account.Repository.AccountRepository;
import com.cubingclub.ranking.RFID.DTO.RFIDCreateDTO;
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
        rfidRepository.save(rfidCreateDTO.toEntity(accountRepository));
    }
    public String getEmail(String uid){
        return rfidRepository.findEmailByUid(uid);
    }
    public void deleteRFID(String email){
        rfidRepository.deleteByAccount_email(email);
    }
}
