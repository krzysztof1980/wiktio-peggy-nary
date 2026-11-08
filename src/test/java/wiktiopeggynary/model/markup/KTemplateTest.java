package wiktiopeggynary.model.markup;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * fromTemplate must implement the documented Vorlage:K parameter contract:
 * t1..t6 = separator between positional params N and N+1,
 * t7 = separator before ft (applies even with fewer than 7 positional params),
 * "_" suppresses the separator. Out-of-range/unknown tN must not throw.
 */
class KTemplateTest {

    private static PositionalTemplateParameter pos(String text) {
        return new PositionalTemplateParameter(new RichText(text));
    }

    private static NamedTemplateParameter named(String name, String value) {
        return new NamedTemplateParameter(name, new RichText(value));
    }

    private static String separatorText(KTemplate.Part part) {
        StringBuilder sb = new StringBuilder();
        RichText sep = part.separator();
        if (sep == null)
            return null;
        sep.getComponents().forEach(c -> sb.append(((PlainText) c).getText()));
        return sb.toString();
    }

    @Test
    void t7BeyondPositionalCountSetsSeparatorBeforeFt() {
        // {{K|Biologie|speziell|Physiologie|t7=;|ft=beim Mann}} - crashed with IndexOutOfBounds
        List<TemplateParameter> params = List.of(
                pos("Biologie"), pos("speziell"), pos("Physiologie"), named("t7", ";"), named("ft", "beim Mann"));
        KTemplate t = assertDoesNotThrow(() -> KTemplate.fromTemplate(params));
        assertEquals(";", separatorText(t.parts().get(2)));
        assertEquals(1, t.suffix().getComponents().size());
        assertEquals("beim Mann", ((PlainText) t.suffix().getComponents().get(0)).getText());
    }

    @Test
    void t7UnderscoreSuppressesSeparatorBeforeFt() {
        // {{K|Akronym|t7=_|ft=für ...}} - the common Wiktionary idiom
        List<TemplateParameter> params = List.of(
                pos("Akronym"), named("t7", "_"), named("ft", "für British Broadcast Corporation"));
        KTemplate t = KTemplate.fromTemplate(params);
        assertEquals("", separatorText(t.parts().get(0)));
    }

    @Test
    void tNOutOfRangeAndUnknownNamedParamsIgnored() {
        List<TemplateParameter> singlePart = List.of(pos("Gastronomie"), named("t7", "_"), named("ft", "(arabisch)"));
        KTemplate t = assertDoesNotThrow(() -> KTemplate.fromTemplate(singlePart));
        assertEquals("", separatorText(t.parts().get(0)));

        List<TemplateParameter> junk = List.of(pos("a"), pos("b"), named("t0", "x"), named("t10", "x"), named("typ", "x"));
        KTemplate t2 = assertDoesNotThrow(() -> KTemplate.fromTemplate(junk));
        assertEquals(",", separatorText(t2.parts().get(0)));
    }

    @Test
    void tBetweenPositionalPartsStillWorks() {
        // {{K|trans.|t1=;|besonders|bayrisch}}
        List<TemplateParameter> params = List.of(pos("trans."), named("t1", ";"), pos("besonders"), pos("bayrisch"));
        KTemplate t = KTemplate.fromTemplate(params);
        assertEquals(";", separatorText(t.parts().get(0)));
        assertEquals(",", separatorText(t.parts().get(1)));
    }

    @Test
    void defaultCommaBeforeNonEmptyFt() {
        // {{K|Behörde|ft=seit etwa 2000}} renders "Behörde, seit etwa 2000:"
        List<TemplateParameter> params = List.of(pos("Behörde"), named("ft", "seit etwa 2000"));
        KTemplate t = KTemplate.fromTemplate(params);
        assertEquals(",", separatorText(t.parts().get(0)));
    }

    @Test
    void emptyFtGetsNoDefaultSeparator() {
        // {{K|ft=|ohne Plural}} - ft empty must not produce a trailing comma
        List<TemplateParameter> params = List.of(named("ft", ""), pos("ohne Plural"));
        KTemplate t = KTemplate.fromTemplate(params);
        assertEquals(1, t.parts().size());
        assertTrue(t.parts().get(0).separator() == null || t.parts().get(0).separator().isEmpty());
    }
}
