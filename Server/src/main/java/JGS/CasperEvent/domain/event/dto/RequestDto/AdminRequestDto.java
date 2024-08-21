package JGS.CasperEvent.domain.event.dto.RequestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
@Schema(description = "관리자 계정 생성 요청을 위한 데이터입니다.")
public class AdminRequestDto {

    @NotNull
    @Schema(description = "관리자 아이디", example = "adminId", required = true)
    private String adminId;

    @NotNull
    @Schema(description = "관리자 인증을 위한 비밀번호", example = "password", required = true)
    private String password;

}
