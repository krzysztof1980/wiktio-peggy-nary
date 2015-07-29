package wiktiopeggynary.model.substantiv;

/**
 * @author Krzysztof Witukiewicz
 */
public enum Gender {
    MASKULINUM("der", "m"),
    FEMININUM("die", "f"),
    NEUTRUM("das", "n"),
    UTRUM("-", "u");

    private final String artikel;
    private final String shortcut;

    Gender(String artikel, String shortcut) {
        this.artikel = artikel;
        this.shortcut = shortcut;
    }

    public String getArtikel() {
        return artikel;
    }

    public String getShortcut() {
        return shortcut;
    }

    public static Gender fromShortcut(String shortcut) {
        switch (shortcut) {
            case "m":
                return MASKULINUM;
            case "f":
                return FEMININUM;
            case "n":
                return NEUTRUM;
            case "u":
                return UTRUM;
            default:
                throw new IllegalArgumentException(String.format("The shortcut '%s' is not supported", shortcut));
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", shortcut, artikel);
    }
}
