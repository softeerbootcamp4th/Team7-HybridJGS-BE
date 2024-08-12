package JGS.CasperEvent.domain.event.dto.ResponseDto;

public record ResultRushEventOptionResponseDto(String mainText, String resultMainText, String resultSubText) {
    public static ResultRushEventOptionResponseDto of(RushEventOptionResponseDto rushEventOptionResponseDto) {
        return new ResultRushEventOptionResponseDto(
                rushEventOptionResponseDto.mainText(),
                rushEventOptionResponseDto.resultMainText(),
                rushEventOptionResponseDto.resultSubText()
        );
    }
}
