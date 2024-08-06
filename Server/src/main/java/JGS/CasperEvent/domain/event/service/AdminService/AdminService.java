package JGS.CasperEvent.domain.event.service.AdminService;

import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.repository.AdminRepository;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.error.exception.ErrorCode;
import JGS.CasperEvent.global.jwt.dto.AdminLoginDto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    public String postAdmin(String body) {

        JsonParser jsonParser = new JsonParser();

        JsonObject adminObject = (JsonObject) jsonParser.parse(body);

        String adminId = adminObject.get("id").getAsString();
        String password = adminObject.get("password").getAsString();

        Admin admin = adminRepository.findById(adminId).orElse(null);

        if (admin != null) throw new CustomException("이미 등록된 ID입니다.", ErrorCode.ACCESS_DENIED);
        adminRepository.save(new Admin(adminId, password, Role.ADMIN));

        return "admin Created";
    }
}
