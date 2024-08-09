package JGS.CasperEvent.domain.event.controller.adminController;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.global.response.ResponseDto;
import jakarta.validation.Valid;
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

    @PostMapping("/join")
    public ResponseEntity<ResponseDto> postAdmin(@RequestBody @Valid AdminRequestDto adminRequestDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.postAdmin(adminRequestDto));
    }
}
