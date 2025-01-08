package com.cubingclub.ranking.DTO;
import com.cubingclub.ranking.Entity.UserRole;

import java.time.LocalDate;

public interface AccountListDTO {
    String getEmail();
    String getName();
    String getEnName();
    LocalDate getBirthDate();
    String getState();
    String getSex();
    String getPhoneNum();
    UserRole getPosition();
}
