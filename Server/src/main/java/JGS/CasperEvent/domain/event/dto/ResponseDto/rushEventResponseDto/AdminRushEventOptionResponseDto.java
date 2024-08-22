package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto;
import JGS.CasperEvent.domain.event.entity.event.RushEvent;

import java.util.HashSet;
import java.util.Set;

public record AdminRushEventOptionResponseDto(Set<RushEventOptionResponseDto> options) {
    public static AdminRushEventOptionResponseDto of(RushEvent rushEvent){
        Set<JGS.CasperEvent.domain.event.dto.response.rush.RushEventOptionResponseDto> optionResponseDtoList = new HashSet<>();
        optionResponseDtoList.add(RushEventOptionResponseDto.of(rushEvent.getLeftOption()));
        optionResponseDtoList.add(RushEventOptionResponseDto.of(rushEvent.getRightOption()));
        return new AdminRushEventOptionResponseDto(optionResponseDtoList);
    }
}
