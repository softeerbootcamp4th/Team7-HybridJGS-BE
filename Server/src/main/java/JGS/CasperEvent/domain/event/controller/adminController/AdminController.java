package JGS.CasperEvent.domain.event.controller.adminController;

import JGS.CasperEvent.domain.event.service.AdminService.AdminService;
import JGS.CasperEvent.global.auth.AdminCheck;
import JGS.CasperEvent.global.response.ResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @AdminCheck
    @GetMapping
    public ResponseEntity<String> getResponse() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/auth")
    public ResponseEntity<ResponseDto> postAdmin(@RequestBody String body){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.postAdmin(body));
    }
}
