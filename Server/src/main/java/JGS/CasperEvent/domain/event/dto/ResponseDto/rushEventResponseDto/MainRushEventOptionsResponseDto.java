package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;


public record MainRushEventOptionsResponseDto(MainRushEventOptionResponseDto leftOption,
                                              MainRushEventOptionResponseDto rightOption) {

    public MainRushEventOptionsResponseDto(MainRushEventOptionResponseDto leftOption, MainRushEventOptionResponseDto rightOption) {
        this.leftOption = leftOption;
        this.rightOption = rightOption;
    }
}
