package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

public enum MouthShape {
    SMILING(1),
    DISPLEASED(2),
    GRINNING(3),
    BEAMING(4),
    NEUTRAL(5);

    private final int mouthShape;

    MouthShape(int mouthShape)
    {
        this.mouthShape = mouthShape;
    }

    public int getMouthShape() {
        return mouthShape;
    }
}
