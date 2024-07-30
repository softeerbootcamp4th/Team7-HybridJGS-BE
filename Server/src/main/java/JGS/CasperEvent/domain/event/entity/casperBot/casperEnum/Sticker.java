package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

public enum Sticker {
    ELECTRIC_SHOCK(1),
    FULL_CHARGE(2),
    BATTERY_BLINKING(3),
    LOVELY_RIBBON(4),
    SPARKLING(5);

    private final int sticker;

    Sticker(int sticker) {
        this.sticker = sticker;
    }

    public int getSticker() {
        return sticker;
    }
}
