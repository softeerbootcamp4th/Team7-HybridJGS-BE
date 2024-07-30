package JGS.CasperEvent.domain.event.entity.casperBot.casperEnum;

public enum Color {
    BUTTERCREAM_YELLOW_PEARL(1),
    SIENNA_ORANGE_METALLIC(2),
    DUSK_BLUE_MATT(3),
    AERO_SILVER_MATT(4),
    ABYSS_BLACK_PEARL(5),
    LIME_GREEN(6),
    TEAL_GREEN(7),
    LIGHT_BLUE(8),
    SKY_BLUE(9),
    INDIGO(10),
    DEEP_PURPLE(11),
    PURPLE(12),
    MAGENTA(13),
    RED(14),
    ORANGE(15),
    GOLD(16),
    YELLOW(17);

    private final int color;

    Color(int color)
    {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
