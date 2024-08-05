package JGS.CasperEvent.domain.event.service.AdminService;

import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.repository.AdminRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.response.ResponseDto;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }


    public ResponseDto postAdmin(String body) {
        JsonParser jsonParser = new JsonParser();

        JsonObject adminObject = (JsonObject) jsonParser.parse(body);

        String adminId = adminObject.get("adminId").getAsString();
        String password = adminObject.get("password").getAsString();

        Admin admin = adminRepository.findById(adminId).orElse(null);

        if (admin != null) throw new CustomException("이미 등록된 ID입니다.", CustomErrorCode.CONFLICT);
        adminRepository.save(new Admin(adminId, password));

        return ResponseDto.of("관리자 생성 완료");
    }
}
