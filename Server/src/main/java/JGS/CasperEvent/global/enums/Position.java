package JGS.CasperEvent.global.enums;

import JGS.CasperEvent.global.error.exception.CustomException;
import lombok.Getter;

@Getter
public enum Position {
    LEFT(1),
    RIGHT(2);

    private final int position;

    Position(int position) {
        this.position = position;
    }

    public static Position of(int position) {
        for (Position pos : Position.values()) {
            if (pos.getPosition() == position) {
                return pos;
            }
        }

        throw new CustomException("optionId는 1 또는 2여야 합니다.", CustomErrorCode.INVALID_RUSH_EVENT_OPTION_ID);
    }
}
