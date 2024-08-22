package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;


import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;

public record MainRushEventOptionsResponseDto(RushEventOptionResponseDto leftOption,
                                              RushEventOptionResponseDto rightOption) {
}
