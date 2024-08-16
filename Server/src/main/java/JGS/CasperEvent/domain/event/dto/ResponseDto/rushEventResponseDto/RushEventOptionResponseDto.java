package JGS.CasperEvent.domain.event.dto.ResponseDto.rushEventResponseDto;

import JGS.CasperEvent.domain.event.entity.event.RushOption;
import JGS.CasperEvent.global.enums.Position;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public record RushEventOptionResponseDto(long optionId, String mainText,
                                         String subText, String resultMainText,
                                         String resultSubText, String imageUrl,
                                         Position position,
                                         @JsonSerialize(using = LocalDateTimeSerializer.class)
                                         @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                                         LocalDateTime createdAt,
                                         @JsonSerialize(using = LocalDateTimeSerializer.class)
                                         @JsonDeserialize(using = LocalDateTimeDeserializer.class)
                                         LocalDateTime updatedAt) {
    public static RushEventOptionResponseDto of(RushOption rushOption) {
        return new RushEventOptionResponseDto(
                rushOption.getOptionId(),
                rushOption.getMainText(),
                rushOption.getSubText(),
                rushOption.getResultMainText(),
                rushOption.getResultSubText(),
                rushOption.getImageUrl(),
                rushOption.getPosition(),
                rushOption.getCreatedAt(),
                rushOption.getUpdatedAt()
        );
    }

}
