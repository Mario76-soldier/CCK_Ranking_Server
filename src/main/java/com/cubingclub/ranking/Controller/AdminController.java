package com.cubingclub.ranking.Controller;

import com.cubingclub.ranking.DTO.RoleUpdateDTO;
import com.cubingclub.ranking.Entity.UserRole;
import com.cubingclub.ranking.Service.AccountService;
import com.cubingclub.ranking.Service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AccountService accountService;
    private final AdminService adminService;

    @GetMapping("/list")
    public ResponseEntity<?> UserList(){
        return ResponseEntity.ok(adminService.findAllList());
    }
    @PostMapping("/role")
    public ResponseEntity<?> ChangeRole(@RequestBody RoleUpdateDTO roleUpdateDTO){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        UserRole myPosition=accountService.getUserRole(authentication.getName());
        UserRole targetPosition=accountService.getUserRole(roleUpdateDTO.getEmail());
        if (myPosition == UserRole.ROLE_CUSTOMER || roleUpdateDTO.getPosition() == UserRole.ROLE_ROOT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else if (targetPosition == roleUpdateDTO.getPosition() ||
                (myPosition == UserRole.ROLE_ADMIN && targetPosition == UserRole.ROLE_ROOT) ||
                (myPosition == UserRole.ROLE_ROOT && targetPosition == UserRole.ROLE_ROOT)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(adminService.updatePosition(roleUpdateDTO));
    }
}
