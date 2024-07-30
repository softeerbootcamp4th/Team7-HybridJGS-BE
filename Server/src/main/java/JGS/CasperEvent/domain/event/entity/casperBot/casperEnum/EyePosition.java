package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

public enum EyePosition {
    CENTER(1),
    LEFT(2),
    RIGHT(3);

    private final int eyePosition;

    EyePosition(int eyePosition)
    {
        this.eyePosition = eyePosition;
    }

    public int getEyePosition() {
        return eyePosition;
    }
}
