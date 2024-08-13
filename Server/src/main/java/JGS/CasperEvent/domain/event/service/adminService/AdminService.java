package JGS.CasperEvent.domain.event.service.adminService;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventOptionRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventDetailResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventParticipantsListResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventParticipantsResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.LotteryEventResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.AdminRushEventResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventParticipantResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventParticipantsListResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.RushEventResponseDto;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.domain.event.repository.AdminRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushOptionRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.RushParticipantsRepository;
import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Position;
import JGS.CasperEvent.global.enums.Role;
import JGS.CasperEvent.global.error.exception.CustomException;
import JGS.CasperEvent.global.error.exception.TooManyLotteryEventException;
import JGS.CasperEvent.global.error.exception.TooManyRushEventException;
import JGS.CasperEvent.global.response.ResponseDto;
import JGS.CasperEvent.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final LotteryEventRepository lotteryEventRepository;
    private final RushEventRepository rushEventRepository;
    private final LotteryParticipantsRepository lotteryParticipantsRepository;
    private final RushParticipantsRepository rushParticipantsRepository;
    private final RushOptionRepository rushOptionRepository;
    private final S3Service s3Service;

    public Admin verifyAdmin(AdminRequestDto adminRequestDto) {
        return adminRepository.findByIdAndPassword(adminRequestDto.getAdminId(), adminRequestDto.getPassword()).orElseThrow(NoSuchElementException::new);
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
        if (lotteryEventRepository.count() >= 1) throw new TooManyLotteryEventException();

        LotteryEvent lotteryEvent = lotteryEventRepository.save(new LotteryEvent(
                lotteryEventRequestDto.getEventStartDateTime(),
                lotteryEventRequestDto.getEventEndDateTime(),
                lotteryEventRequestDto.getWinnerCount()
        ));

        return LotteryEventResponseDto.of(lotteryEvent, LocalDateTime.now());
    }

    public LotteryEventDetailResponseDto getLotteryEvent() {
        return LotteryEventDetailResponseDto.of(
                lotteryEventRepository.findAll().get(0)
        );
    }

    public LotteryEventParticipantsListResponseDto getLotteryEventParticipants(int size, int page, String phoneNumber) {
        Pageable pageable = PageRequest.of(page, size);

        Page<LotteryParticipants> lotteryParticipantsPage = null;
        if (phoneNumber.isEmpty()) lotteryParticipantsPage = lotteryParticipantsRepository.findAll(pageable);
        else lotteryParticipantsRepository.findByBaseUser_Id(phoneNumber, pageable);

        List<LotteryEventParticipantsResponseDto> lotteryEventParticipantsResponseDtoList = new ArrayList<>();

        for (LotteryParticipants lotteryParticipant : lotteryParticipantsPage) {
            lotteryEventParticipantsResponseDtoList.add(
                    LotteryEventParticipantsResponseDto.of(lotteryParticipant)
            );
        }
        Boolean isLastPage = !lotteryParticipantsPage.hasNext();
        return new LotteryEventParticipantsListResponseDto(lotteryEventParticipantsResponseDtoList, isLastPage, lotteryParticipantsRepository.count());
    }

    public RushEventResponseDto createRushEvent(RushEventRequestDto rushEventRequestDto, MultipartFile prizeImg, MultipartFile leftOptionImg, MultipartFile rightOptionImg) {
        if (rushEventRepository.count() >= 6) throw new TooManyRushEventException();

        String prizeImgSrc = s3Service.upload(prizeImg);
        String leftOptionImgSrc = s3Service.upload(leftOptionImg);
        String rightOptionImgSrc = s3Service.upload(rightOptionImg);

        // Img s3 저장
        RushEvent rushEvent = rushEventRepository.save(
                new RushEvent(
                        LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getStartTime()),
                        LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getEndTime()),
                        rushEventRequestDto.getWinnerCount(),
                        prizeImgSrc,
                        rushEventRequestDto.getPrizeDescription()
                ));

        RushEventOptionRequestDto leftOption = rushEventRequestDto.getLeftOption();
        RushEventOptionRequestDto rightOption = rushEventRequestDto.getRightOption();

        RushOption leftRushOption = rushOptionRepository.save(new RushOption(
                rushEvent,
                leftOption.getMainText(),
                leftOption.getSubText(),
                leftOption.getResultMainText(),
                leftOption.getResultSubText(),
                leftOptionImgSrc,
                Position.LEFT
        ));

        RushOption rightRushOption = rushOptionRepository.save(new RushOption(
                rushEvent,
                rightOption.getMainText(),
                rightOption.getSubText(),
                rightOption.getResultMainText(),
                rightOption.getResultSubText(),
                rightOptionImgSrc,
                Position.RIGHT
        ));

        rushEvent.updateOption(leftRushOption, rightRushOption);

        return RushEventResponseDto.of(rushEvent);
    }

    public List<AdminRushEventResponseDto> getRushEvents() {
        List<RushEvent> rushEvents = rushEventRepository.findAll();
        return AdminRushEventResponseDto.of(rushEvents);
    }

    public RushEventParticipantsListResponseDto getRushEventParticipants(long rushEventId, int size, int page, int optionId, String phoneNumber) {
        Pageable pageable = PageRequest.of(page, size);

        Page<RushParticipants> rushParticipantsPage = null;

        boolean isPhoneNumberEmpty = phoneNumber.isEmpty();
        boolean isOptionIdValid = optionId == 1 || optionId == 2;

        if (!isPhoneNumberEmpty && isOptionIdValid) {
            // 전화번호와 유효한 옵션 ID가 있는 경우
            rushParticipantsPage = rushParticipantsRepository.findByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(rushEventId, optionId, phoneNumber, pageable);
        } else if (isPhoneNumberEmpty && !isOptionIdValid) {
            // 전화번호가 비어있고 유효하지 않은 옵션 ID가 있는 경우
            rushParticipantsPage = rushParticipantsRepository.findByRushEvent_RushEventId(rushEventId, pageable);
        } else if (isOptionIdValid) {
            // 유효한 옵션 ID가 있지만 전화번호는 비어있는 경우
            rushParticipantsPage = rushParticipantsRepository.findByRushEvent_RushEventIdAndOptionId(rushEventId, optionId, pageable);
        } else {
            // 유효하지 않은 옵션 ID와 전화번호가 주어진 경우
            rushParticipantsPage = rushParticipantsRepository.findByRushEvent_RushEventIdAndBaseUser_Id(rushEventId, phoneNumber, pageable);
        }

        List<RushEventParticipantResponseDto> rushEventParticipantResponseDtoList = new ArrayList<>();
        for (RushParticipants rushParticipant : rushParticipantsPage) {
            String userId = rushParticipant.getBaseUser().getId();
            int userChoice = rushParticipant.getOptionId();
            long rank = rushParticipantsRepository.findUserRankByEventIdAndUserIdAndOptionId(rushEventId, userId, userChoice);
            rushEventParticipantResponseDtoList.add(
                    RushEventParticipantResponseDto.of(rushParticipant, rank)
            );
        }

        Boolean isLastPage = !rushParticipantsPage.hasNext();
        return new RushEventParticipantsListResponseDto(rushEventParticipantResponseDtoList, isLastPage, rushParticipantsRepository.count());
    }
  
    public void deleteLotteryEvent() {
        List<LotteryEvent> lotteryEventList = lotteryEventRepository.findAll();

        if (lotteryEventList.isEmpty()) {
            throw new CustomException("현재 진행중인 lotteryEvent가 존재하지 않습니다.", CustomErrorCode.NO_LOTTERY_EVENT);
        }

        if (lotteryEventList.size() > 1) {
            throw new CustomException("현재 진행중인 lotteryEvent가 2개 이상입니다.", CustomErrorCode.TOO_MANY_LOTTERY_EVENT);
        }

        lotteryEventRepository.deleteAll();
    }
}
