package JGS.CasperEvent.domain.event.service.adminService;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.repository.AdminRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final LotteryEventRepository lotteryEventRepository;
    private final RushEventRepository rushEventRepository;

    public Admin verifyAdmin(AdminRequestDto adminRequestDto) {
        return adminRepository.findById(adminRequestDto.getAdminId()).orElseThrow(NoSuchElementException::new);
    }

    public ResponseDto postAdmin(AdminRequestDto adminRequestDto) {
        String adminId = adminRequestDto.getAdminId();
        //Todo: 비밀번호 암호화 필요
        String password = adminRequestDto.getPassword();

        Admin admin = adminRepository.findById(adminId).orElse(null);

        if (admin != null) throw new CustomException("이미 등록된 ID입니다.", CustomErrorCode.CONFLICT);
        adminRepository.save(new Admin(adminId, password, Role.ADMIN));

        return ResponseDto.of("관리자 생성 성공");
    }

    public LotteryEventResponseDto createLotteryEvent(LotteryEventRequestDto lotteryEventRequestDto) {
        LotteryEvent lotteryEvent = lotteryEventRepository.save(new LotteryEvent(
                lotteryEventRequestDto.getEventStartDate(),
                lotteryEventRequestDto.getEventEndDate(),
                lotteryEventRequestDto.getWinnerCount()
        ));

        return LotteryEventResponseDto.of(LocalDateTime.now(), lotteryEvent);
    }
}
