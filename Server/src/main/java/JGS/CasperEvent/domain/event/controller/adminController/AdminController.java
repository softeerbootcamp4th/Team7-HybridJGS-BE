package JGS.CasperEvent.domain.event.controller.adminController;

import JGS.CasperEvent.domain.event.service.AdminService.AdminService;
import JGS.CasperEvent.global.auth.AdminCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<String> getResponse(){
        return ResponseEntity.ok("OK");
    }
}
