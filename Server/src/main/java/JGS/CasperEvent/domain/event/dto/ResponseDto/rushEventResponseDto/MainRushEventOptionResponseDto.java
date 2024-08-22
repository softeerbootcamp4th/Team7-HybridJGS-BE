package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;

public record MainRushEventOptionResponseDto(String mainText,
                                             String subText) {

    public static MainRushEventOptionResponseDto of(RushEventOptionResponseDto rushEventOptionResponseDto) {
        return new MainRushEventOptionResponseDto(
                rushEventOptionResponseDto.getMainText(),
                rushEventOptionResponseDto.getSubText()
        );
    }
}
