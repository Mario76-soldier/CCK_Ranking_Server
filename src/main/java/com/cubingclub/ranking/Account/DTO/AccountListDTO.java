package com.cubingclub.ranking.Account.DTO;
import com.cubingclub.ranking.Account.Entity.Gender;
import com.cubingclub.ranking.Account.Entity.UserRole;

import java.time.LocalDate;

public interface AccountListDTO {
    String getEmail();
    String getName();
    String getEnName();
    UserRole getPosition();
    LocalDate getBirthDate();
    String getState();
    Gender getGender();
    String getCckId();
    String getWcaId();
    String getPhoneNum();
}
