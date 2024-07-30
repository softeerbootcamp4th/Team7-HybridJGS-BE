package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

public enum EyeShape {
    ALLOY_WHEEL_15(1),
    ALLOY_WHEEL_17(2),
    PIXEL_BY_PIXEL(3),
    ELECTRIC(4),
    GLASSY(5),
    SMILE(6),
    CUTE(7),
    HEART(8);

    private final int eyeShape;

    EyeShape(int eyeShape)
    {
        this.eyeShape = eyeShape;
    }

    public int getEyeShape() {
        return eyeShape;
    }
}
