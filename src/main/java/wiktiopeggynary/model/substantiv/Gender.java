package wiktiopeggynary.model.substantiv;

import java.util.Arrays;

/**
 * @author Krzysztof Witukiewicz
 */
public enum Gender {
	MASKULINUM("der", "m"),
	FEMININUM("die", "f"),
	NEUTRUM("das", "n"),
	UTRUM("-", "u"),
	PLURAL("pl.", "0", "x");
	
	private final String artikel;
	private final String[] shortcuts;
	
	Gender(String artikel, String... shortcuts) {
		this.artikel = artikel;
		this.shortcuts = shortcuts;
	}
	
	public static Gender fromShortcut(String shortcut) {
		return Arrays.stream(Gender.values())
		             .filter(v -> Arrays.stream(v.shortcuts).anyMatch(s -> s.equals(shortcut)))
		             .findFirst()
		             .orElseThrow(() -> new IllegalArgumentException(String.format("The shortcut '%s' is not supported",
		                                                                           shortcut)));
	}
	
	public String getArtikel() {
		return artikel;
	}
	
	public String[] getShortcuts() {
		return shortcuts;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", shortcuts, artikel);
	}
}
