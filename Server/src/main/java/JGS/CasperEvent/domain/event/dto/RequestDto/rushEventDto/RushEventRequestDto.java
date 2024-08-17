package JGS.CasperEvent.domain.event.dto.RequestDto.rushEventDto;

import JGS.CasperEvent.global.enums.CustomErrorCode;
import JGS.CasperEvent.global.enums.Position;
import JGS.CasperEvent.global.error.exception.CustomException;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@ToString
@Getter
@Builder
@EqualsAndHashCode
//todo 검증 항목 추가 필요
public class RushEventRequestDto {
    @NotNull(message = "선착순 이벤트 아이디는 필수 항목입니다.")
    private Long rushEventId;

    @NotNull(message = "이벤트 시작 날짜는 필수 항목입니다.")
    private LocalDate eventDate;

    @NotNull(message = "이벤트 시작 시간은 필수 항목입니다.")
    private LocalTime startTime;

    @NotNull(message = "이벤트 종료 시간 필수 항목입니다.")
    private LocalTime endTime;

    @NotNull(message = "당첨자 수는 필수 항목입니다.")
    private int winnerCount;

    @NotNull(message = "상품 사진은 필수 항목입니다.")
    private String prizeImageUrl;

    @NotNull(message = "상품 상세 정보는 필수 항목입니다.")
    private String prizeDescription;

    private Set<RushEventOptionRequestDto> options;

    public RushEventOptionRequestDto getLeftOptionRequestDto() {
        return options.stream()
                .filter(option -> option.getPosition() == Position.LEFT)
                .findFirst().orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_RUSH_EVENT_OPTION));
    }

    public RushEventOptionRequestDto getRightOptionRequestDto() {
        return options.stream()
                .filter(option -> option.getPosition() == Position.RIGHT)
                .findFirst().orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_RUSH_EVENT_OPTION));
    }
}
