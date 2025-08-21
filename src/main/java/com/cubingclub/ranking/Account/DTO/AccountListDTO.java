package com.cubingclub.ranking.Account.DTO;
import com.cubingclub.ranking.Account.Entity.Gender;
import com.cubingclub.ranking.Account.Entity.UserRole;

import java.time.LocalDate;

public interface AccountListDTO {
    String getName();
    String getEnName();
    String getState();
    Gender getGender();
    String getCckId();
    String getWcaId();
}
