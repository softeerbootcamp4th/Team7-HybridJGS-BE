package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;


import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;

// rush Event로
public record MainRushEventOptionsResponseDto(RushEventOptionResponseDto leftOption,
                                              RushEventOptionResponseDto rightOption) {
}
