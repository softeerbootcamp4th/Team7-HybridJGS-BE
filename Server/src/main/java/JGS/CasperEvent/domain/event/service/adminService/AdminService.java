package JGS.CasperEvent.domain.event.service.adminService;

import JGS.CasperEvent.domain.event.dto.RequestDto.AdminRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.lotteryEventDto.LotteryEventRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventOptionRequestDto;
import JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto.RushEventRequestDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.ImageUrlResponseDto;
import JGS.CasperEvent.domain.event.dto.ResponseDto.lotteryEventResponseDto.*;
import JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto.*;
import JGS.CasperEvent.domain.event.entity.admin.Admin;
import JGS.CasperEvent.domain.event.entity.casperBot.CasperBot;
import JGS.CasperEvent.domain.event.entity.event.LotteryEvent;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;
import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.domain.event.entity.participants.LotteryParticipants;
import JGS.CasperEvent.domain.event.entity.participants.LotteryWinners;
import JGS.CasperEvent.domain.event.entity.participants.RushParticipants;
import JGS.CasperEvent.domain.event.repository.AdminRepository;
import JGS.CasperEvent.domain.event.repository.CasperBotRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.LotteryEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushEventRepository;
import JGS.CasperEvent.domain.event.repository.eventRepository.RushOptionRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryParticipantsRepository;
import JGS.CasperEvent.domain.event.repository.participantsRepository.LotteryWinnerRepository;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static JGS.CasperEvent.global.util.RepositoryErrorHandler.findByIdOrElseThrow;

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
    private final CasperBotRepository casperBotRepository;
    private final LotteryWinnerRepository lotteryWinnerRepository;

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

    public ImageUrlResponseDto postImage(MultipartFile image) {
        return new ImageUrlResponseDto(s3Service.upload(image));
    }

    public LotteryEventResponseDto createLotteryEvent(LotteryEventRequestDto lotteryEventRequestDto) {
        if (lotteryEventRepository.count() >= 1) throw new TooManyLotteryEventException();

        LotteryEvent lotteryEvent = lotteryEventRepository.save(new LotteryEvent(
                LocalDateTime.of(lotteryEventRequestDto.getStartDate(), lotteryEventRequestDto.getStartTime()),
                LocalDateTime.of(lotteryEventRequestDto.getEndDate(), lotteryEventRequestDto.getEndTime()),
                lotteryEventRequestDto.getWinnerCount()
        ));

        return LotteryEventResponseDto.of(lotteryEvent, LocalDateTime.now());
    }

    public LotteryEventDetailResponseDto getLotteryEvent() {
        return LotteryEventDetailResponseDto.of(
                getCurrentLotteryEvent()
        );
    }

    public LotteryEventParticipantsListResponseDto getLotteryEventParticipants(int size, int page, String phoneNumber) {
        Pageable pageable = PageRequest.of(page, size);

        Page<LotteryParticipants> lotteryParticipantsPage = null;
        long count;
        if (phoneNumber.isEmpty()) {
            lotteryParticipantsPage = lotteryParticipantsRepository.findAll(pageable);
            count = lotteryParticipantsRepository.count();
        } else {
            lotteryParticipantsPage = lotteryParticipantsRepository.findByBaseUser_Id(phoneNumber, pageable);
            count = lotteryParticipantsRepository.countByBaseUser_Id(phoneNumber);
        }

        List<LotteryEventParticipantsResponseDto> lotteryEventParticipantsResponseDtoList = new ArrayList<>();

        for (LotteryParticipants lotteryParticipant : lotteryParticipantsPage) {
            lotteryEventParticipantsResponseDtoList.add(
                    LotteryEventParticipantsResponseDto.of(lotteryParticipant)
            );
        }
        Boolean isLastPage = !lotteryParticipantsPage.hasNext();
        return new LotteryEventParticipantsListResponseDto(lotteryEventParticipantsResponseDtoList, isLastPage, count);
    }

    public AdminRushEventResponseDto createRushEvent(RushEventRequestDto rushEventRequestDto, MultipartFile prizeImg, MultipartFile leftOptionImg, MultipartFile rightOptionImg) {
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

        RushEventOptionRequestDto leftOption = rushEventRequestDto.getLeftOptionRequestDto();
        RushEventOptionRequestDto rightOption = rushEventRequestDto.getRightOptionRequestDto();

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

        rushEvent.addOption(leftRushOption, rightRushOption);

        return AdminRushEventResponseDto.of(rushEvent);
    }

    public List<AdminRushEventResponseDto> getRushEvents() {
        List<RushEvent> rushEvents = rushEventRepository.findAll();
        List<AdminRushEventResponseDto> rushEventResponseDtoList = new ArrayList<>();
        for (RushEvent rushEvent : rushEvents) {
            rushEventResponseDtoList.add(AdminRushEventResponseDto.of(rushEvent));
        }
        return rushEventResponseDtoList;
    }

    public RushEventParticipantsListResponseDto getRushEventParticipants(long rushEventId, int size, int page, int optionId, String phoneNumber) {
        Pageable pageable = PageRequest.of(page, size);

        Page<RushParticipants> rushParticipantsPage = null;

        boolean isPhoneNumberEmpty = phoneNumber.isEmpty();
        boolean isOptionIdValid = optionId == 1 || optionId == 2;
        long count;

        if (!isPhoneNumberEmpty && isOptionIdValid) {
            // 전화번호와 유효한 옵션 ID가 있는 경우
            rushParticipantsPage = rushParticipantsRepository.findByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(rushEventId, optionId, phoneNumber, pageable);
            count = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionIdAndBaseUser_Id(rushEventId, optionId, phoneNumber);
        } else if (isPhoneNumberEmpty && !isOptionIdValid) {
            // 전화번호가 비어있고 유효하지 않은 옵션 ID가 있는 경우
            rushParticipantsPage = rushParticipantsRepository.findByRushEvent_RushEventId(rushEventId, pageable);
            count = rushParticipantsRepository.countByRushEvent_RushEventId(rushEventId);
        } else if (isOptionIdValid) {
            // 유효한 옵션 ID가 있지만 전화번호는 비어있는 경우
            rushParticipantsPage = rushParticipantsRepository.findByRushEvent_RushEventIdAndOptionId(rushEventId, optionId, pageable);
            count = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(rushEventId, optionId);
        } else {
            // 유효하지 않은 옵션 ID와 전화번호가 주어진 경우
            rushParticipantsPage = rushParticipantsRepository.findByRushEvent_RushEventIdAndBaseUser_Id(rushEventId, phoneNumber, pageable);
            count = rushParticipantsRepository.countByRushEvent_RushEventIdAndBaseUser_Id(rushEventId, phoneNumber);
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
        return new RushEventParticipantsListResponseDto(rushEventParticipantResponseDtoList, isLastPage, count);
    }

    public RushEventParticipantsListResponseDto getRushEventWinners(long rushEventId, int size, int page, String phoneNumber) {
        Page<RushParticipants> rushParticipantsPage = null;

        RushEvent rushEvent = findByIdOrElseThrow(rushEventRepository, rushEventId, CustomErrorCode.NO_RUSH_EVENT);
        int winnerCount = rushEvent.getWinnerCount();
        Pageable winnerPage = PageRequest.of(0, winnerCount);

        long leftSelect = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(rushEventId, 1);
        long rightSelect = rushParticipantsRepository.countByRushEvent_RushEventIdAndOptionId(rushEventId, 2);

        boolean isPhoneNumberEmpty = phoneNumber.isEmpty();

        int winnerOptionId;
        if (leftSelect > rightSelect) winnerOptionId = 1;
        else if (leftSelect < rightSelect) winnerOptionId = 2;
        else winnerOptionId = 0;

        if (!isPhoneNumberEmpty && winnerOptionId != 0) {
            // 전화번호와 유효한 옵션 ID가 있는 경우
            rushParticipantsPage = rushParticipantsRepository.findWinnerByEventIdAndOptionIdAndPhoneNumber(rushEventId, winnerOptionId, phoneNumber, winnerPage);
        } else if (isPhoneNumberEmpty && winnerOptionId == 0) {
            // 전화번호가 비어있고 두 선택지가 동점인 경우
            rushParticipantsPage = rushParticipantsRepository.findWinnerByEventId(rushEventId, winnerPage);
        } else if (winnerOptionId != 0) {
            // 유효한 옵션 ID가 있지만 전화번호는 비어있는 경우
            rushParticipantsPage = rushParticipantsRepository.findWinnerByEventIdAndOptionId(rushEventId, winnerOptionId, winnerPage);
        } else {
            // 두 선택지가 동점이고 전화번호가 주어진 경우
            rushParticipantsPage = rushParticipantsRepository.findByWinnerByEventIdAndPhoneNumber(rushEventId, phoneNumber, winnerPage);
        }

        List<RushParticipants> rushParticipantsList = rushParticipantsPage.getContent();
        rushParticipantsPage = paginateList(rushParticipantsList, page, size);

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
        long totalParticipants = rushParticipantsList.size();
        return new RushEventParticipantsListResponseDto(rushEventParticipantResponseDtoList, isLastPage, totalParticipants);
    }

    @Transactional
    public void deleteLotteryEvent() {
        LotteryEvent currentLotteryEvent = getCurrentLotteryEvent();
        lotteryEventRepository.deleteById(currentLotteryEvent.getLotteryEventId());
    }

    @Transactional
    public LotteryEventDetailResponseDto updateLotteryEvent(LotteryEventRequestDto lotteryEventRequestDto) {
        LotteryEvent currentLotteryEvent = getCurrentLotteryEvent();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newStartDateTime = LocalDateTime.of(lotteryEventRequestDto.getStartDate(), lotteryEventRequestDto.getStartTime());
        LocalDateTime newEndDateTime = LocalDateTime.of(lotteryEventRequestDto.getEndDate(), lotteryEventRequestDto.getEndTime());

        // 종료 날짜가 시작 날짜보다 뒤인지 체크
        if (newEndDateTime.isBefore(newStartDateTime)) {
            throw new CustomException(CustomErrorCode.EVENT_END_TIME_BEFORE_START_TIME);
        }

        if (currentLotteryEvent.getStartDateTime().isBefore(now) && currentLotteryEvent.getEndDateTime().isAfter(now)) {
            // 현재 진행 중인 이벤트인 경우
            if (!currentLotteryEvent.getStartDateTime().equals(newStartDateTime)) {
                throw new CustomException(CustomErrorCode.EVENT_IN_PROGRESS_CANNOT_CHANGE_START_TIME);
            }
            if (newEndDateTime.isBefore(now)) {
                throw new CustomException(CustomErrorCode.EVENT_IN_PROGRESS_END_TIME_BEFORE_NOW);
            }
        }

        // 이벤트가 시작 전인 경우
        else if (newStartDateTime.isBefore(now)) {
            throw new CustomException(CustomErrorCode.EVENT_BEFORE_START_TIME);
        }

        // 필드 업데이트
        currentLotteryEvent.updateLotteryEvent(newStartDateTime, newEndDateTime, lotteryEventRequestDto.getWinnerCount());

        return LotteryEventDetailResponseDto.of(currentLotteryEvent);
    }

    private LotteryEvent getCurrentLotteryEvent() {
        List<LotteryEvent> lotteryEventList = lotteryEventRepository.findAll();

        if (lotteryEventList.isEmpty()) {
            throw new CustomException("현재 진행중인 lotteryEvent가 존재하지 않습니다.", CustomErrorCode.NO_LOTTERY_EVENT);
        }

        if (lotteryEventList.size() > 1) {
            throw new CustomException("현재 진행중인 lotteryEvent가 2개 이상입니다.", CustomErrorCode.TOO_MANY_LOTTERY_EVENT);
        }

        return lotteryEventList.get(0);
    }

    @Transactional
    public ResponseDto pickLotteryEventWinners() {
        if (lotteryWinnerRepository.count() > 1) throw new CustomException(CustomErrorCode.LOTTERY_EVENT_ALREADY_DRAWN);
        LotteryEvent lotteryEvent = getCurrentLotteryEvent();

        int winnerCount = lotteryEvent.getWinnerCount();

        Pageable pageable = PageRequest.of(0, winnerCount);

        //todo 당첨자 추첨 알고리즘 변경해야함
        Page<LotteryParticipants> lotteryWinners = lotteryParticipantsRepository.findAll(pageable);
        for (LotteryParticipants lotteryWinner : lotteryWinners) {
            lotteryWinnerRepository.save(new LotteryWinners(lotteryWinner));
        }

        return new ResponseDto("추첨이 완료되었습니다.");
    }

    public LotteryEventWinnerListResponseDto getLotteryEventWinners(int size, int page, String phoneNumber) {
        Pageable pageable = PageRequest.of(page, size);
        if (lotteryWinnerRepository.count() == 0) throw new CustomException(CustomErrorCode.LOTTERY_EVENT_NOT_DRAWN);

        Page<LotteryWinners> lotteryWinnersPage = null;
        long count;
        if (phoneNumber.isEmpty()) {
            lotteryWinnersPage = lotteryWinnerRepository.findAll(pageable);
            count = lotteryWinnerRepository.count();
        } else {
            lotteryWinnersPage = lotteryWinnerRepository.findByPhoneNumber(phoneNumber, pageable);
            count = lotteryWinnerRepository.countByPhoneNumber(phoneNumber);
        }

        List<LotteryEventWinnerResponseDto> lotteryEventWinnerResponseDto = new ArrayList<>();

        for (LotteryWinners lotteryWinners : lotteryWinnersPage) {
            lotteryEventWinnerResponseDto.add(
                    LotteryEventWinnerResponseDto.of(lotteryWinners)
            );
        }
        Boolean isLastPage = !lotteryWinnersPage.hasNext();
        return new LotteryEventWinnerListResponseDto(lotteryEventWinnerResponseDto, isLastPage, count);
    }

    @Transactional
    public List<AdminRushEventResponseDto> updateRushEvents(List<RushEventRequestDto> rushEventRequestDtoList) {
        LocalDateTime now = LocalDateTime.now();

        for (RushEventRequestDto rushEventRequestDto : rushEventRequestDtoList) {
            RushEvent rushEvent = rushEventRepository.findByRushEventId(rushEventRequestDto.getRushEventId());

            LocalDateTime curStartDateTime = rushEvent.getStartDateTime();
            LocalDateTime curEndDateTime = rushEvent.getEndDateTime();
            LocalDateTime startDateTime = LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getStartTime());
            LocalDateTime endDateTime = LocalDateTime.of(rushEventRequestDto.getEventDate(), rushEventRequestDto.getEndTime());
            if (!Objects.equals(curStartDateTime, startDateTime) || !Objects.equals(curEndDateTime, endDateTime)) {
                // 종료 날짜가 시작 날짜보다 뒤인지 체크
                if (endDateTime.isBefore(startDateTime)) {
                    throw new CustomException(CustomErrorCode.EVENT_END_TIME_BEFORE_START_TIME);
                }

                if (curStartDateTime.isBefore(now) && curEndDateTime.isAfter(now)) {
                    // 현재 진행 중인 이벤트인 경우
                    if (!curStartDateTime.equals(startDateTime)) {
                        throw new CustomException(CustomErrorCode.EVENT_IN_PROGRESS_CANNOT_CHANGE_START_TIME);
                    }
                    if (endDateTime.isBefore(now)) {
                        throw new CustomException(CustomErrorCode.EVENT_IN_PROGRESS_END_TIME_BEFORE_NOW);
                    }
                }

                // 이벤트가 시작 전인 경우
                else if (startDateTime.isBefore(now)) {
                    throw new CustomException(CustomErrorCode.EVENT_BEFORE_START_TIME);
                }
            }

            RushOption leftOption = rushEvent.getLeftOption();
            RushOption rightOption = rushEvent.getRightOption();

            rushEvent.updateRushEvent(rushEventRequestDto);
            leftOption.updateRushOption(rushEventRequestDto.getLeftOptionRequestDto());
            rightOption.updateRushOption(rushEventRequestDto.getRightOptionRequestDto());
        }

        List<RushEvent> rushEvents = rushEventRepository.findAll();
        List<AdminRushEventResponseDto> rushEventResponseDtoList = new ArrayList<>();
        for (RushEvent rushEvent : rushEvents) {
            rushEventResponseDtoList.add(AdminRushEventResponseDto.of(rushEvent));
        }
        return rushEventResponseDtoList;
    }

    @Transactional
    public ResponseDto deleteRushEvent(Long rushEventId) {
        RushEvent rushEvent = rushEventRepository.findById(rushEventId).orElseThrow(() -> new CustomException(CustomErrorCode.NO_RUSH_EVENT));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = rushEvent.getStartDateTime();
        LocalDateTime endDateTime = rushEvent.getEndDateTime();

        if (now.isAfter(startDateTime) && now.isBefore(endDateTime))
            throw new CustomException(CustomErrorCode.EVENT_IN_PROGRESS_CANNOT_DELETE);
        rushEventRepository.delete(rushEvent);
        return ResponseDto.of("요청에 성공하였습니다.");
    }

    public AdminRushEventOptionResponseDto getRushEventOptions(Long rushEventId) {
        return AdminRushEventOptionResponseDto.of(
                rushEventRepository.findById(rushEventId).orElseThrow(
                        () -> new CustomException(CustomErrorCode.NO_RUSH_EVENT)
                )
        );
    }

    public List<LotteryEventExpectationResponseDto> getLotteryEventExpectations(Long participantId) {
        LotteryParticipants lotteryParticipant = lotteryParticipantsRepository.findById(participantId).orElseThrow(
                () -> new CustomException(CustomErrorCode.USER_NOT_FOUND)
        );

        List<CasperBot> casperBotList = casperBotRepository.findByPhoneNumber(lotteryParticipant.getBaseUser().getId());

        // 기대평을 작성하지 않은 경우(기대평이 빈 문자열인 경우, 삭제된 경우)는 제외하여 반환합니다.
        return casperBotList.stream().filter(casperBot -> !casperBot.getExpectation().isEmpty() && !casperBot.isDeleted()).map(LotteryEventExpectationResponseDto::of).toList();
    }

    @Transactional
    public void deleteLotteryEventExpectation(Long casperId) {
        CasperBot casperBot = casperBotRepository.findById(casperId).orElseThrow(
                () -> new CustomException(CustomErrorCode.CASPERBOT_NOT_FOUND)
        );

        casperBot.deleteExpectation();
    }

    public static <T> Page<T> paginateList(List<T> list, int page, int size) {
        int start = Math.min(page * size, list.size());
        int end = Math.min(start + size, list.size());

        List<T> paginatedList = list.subList(start, end);

        return new PageImpl<>(paginatedList, PageRequest.of(page, size), list.size());
    }
}
