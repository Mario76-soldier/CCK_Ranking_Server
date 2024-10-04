package CubingClubKorea.CCK_CompRank.Controller;

import CubingClubKorea.CCK_CompRank.DTO.AccountListDTO;
import CubingClubKorea.CCK_CompRank.DTO.RoleUpdateDTO;
import CubingClubKorea.CCK_CompRank.Entity.UserRole;
import CubingClubKorea.CCK_CompRank.Service.AccountService;
import CubingClubKorea.CCK_CompRank.Service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static CubingClubKorea.CCK_CompRank.Entity.UserRole.*;

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
        if (myPosition == ROLE_CUSTOMER || roleUpdateDTO.getPosition() == ROLE_ROOT) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } else if (targetPosition == roleUpdateDTO.getPosition() ||
                (myPosition == ROLE_ADMIN && targetPosition == ROLE_ROOT) ||
                (myPosition == ROLE_ROOT && targetPosition == ROLE_ROOT)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(adminService.updatePosition(roleUpdateDTO));
    }
}
