package JGS.CasperEvent.domain.event.dto.RequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class AdminRequestDto {

    @NotNull
    private String adminId;

    @NotNull
    private String password;

}
