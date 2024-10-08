package JGS.CasperEvent.domain.event.entity.event;

import JGS.CasperEvent.global.entity.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class BaseEvent extends BaseEntity {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    protected LocalDateTime startDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    protected LocalDateTime endDateTime;
    protected int winnerCount;

    // 기본 생성자에서 디폴트 값 설정
    public BaseEvent() {
        this.startDateTime = LocalDateTime.now();
        this.endDateTime = LocalDateTime.now().plusMinutes(10);
        this.winnerCount = 0; // 기본 우승자 수를 0으로 설정
    }

    // 특정 값을 설정할 수 있는 생성자
    public BaseEvent(LocalDateTime startDateTime, LocalDateTime endDateTime, int winnerCount) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.winnerCount = winnerCount;
    }
}
