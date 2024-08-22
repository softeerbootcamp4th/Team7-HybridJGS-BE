package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;

public record ResultRushEventOptionResponseDto(String mainText, String resultMainText, String resultSubText) {
    public static ResultRushEventOptionResponseDto of(RushEventOptionResponseDto rushEventOptionResponseDto) {
        return new ResultRushEventOptionResponseDto(
                rushEventOptionResponseDto.getMainText(),
                rushEventOptionResponseDto.getResultMainText(),
                rushEventOptionResponseDto.getResultSubText()
        );
    }
}
