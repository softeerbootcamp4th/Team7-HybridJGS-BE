package JGS.CasperEvent.domain.event.service.AdminService;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.repository.AdminRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.jwt.dto.AdminLoginDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final AdminRepository adminRepository;

    public Admin verifyAdmin(AdminLoginDto adminLoginDto) {
        return adminRepository.findById(adminLoginDto.getId()).orElseThrow(NoSuchElementException::new);
    }

    public String postAdmin(AdminRequestDto adminRequestDto) {


        String adminId = adminRequestDto.getAdminId();
        //Todo: 비밀번호 암호화 필요
        String password = adminRequestDto.getPassword();

        Admin admin = adminRepository.findById(adminId).orElse(null);

        if (admin != null) throw new CustomException("이미 등록된 ID입니다.", CustomErrorCode.CONFLICT);
        adminRepository.save(new Admin(adminId, password, Role.ADMIN));

        return "admin Created";
    }
}
