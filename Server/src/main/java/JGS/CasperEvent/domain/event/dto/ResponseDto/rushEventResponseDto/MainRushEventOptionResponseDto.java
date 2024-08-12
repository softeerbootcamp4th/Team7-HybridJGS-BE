package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

public record MainRushEventOptionResponseDto(String mainText,
                                             String subText) {

    public static MainRushEventOptionResponseDto of(RushEventOptionResponseDto rushEventOptionResponseDto) {
        return new MainRushEventOptionResponseDto(
                rushEventOptionResponseDto.mainText(),
                rushEventOptionResponseDto.subText()
        );
    }
}
