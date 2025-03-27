package com.cubingclub.ranking.DTO;
import com.cubingclub.ranking.Entity.UserRole;

import java.time.LocalDate;

public interface AccountListDTO {
    String getEmail();
    String getName();
    String getEnName();
    LocalDate getBirthDate();
    String getState();
    String getGender();
    String getPhoneNum();
    UserRole getPosition();
}
