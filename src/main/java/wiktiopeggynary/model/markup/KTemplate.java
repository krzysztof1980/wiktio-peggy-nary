package wiktiopeggynary.model.markup;

import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Krzysztof Witukiewicz
 */
public record KTemplate(List<Part> parts, RichText suffix) implements RichTextComponent {

    private static final String COMMA_TEXT = ",";
    private static final String SEPARATOR_SUPPRESSION_MARKER = "_";
    /**
     * Vorlage:K: t7 separates the last positional parameter from ft and applies
     * regardless of how many positional parameters there are.
     */
    private static final int SEPARATOR_BEFORE_FREE_TEXT_POSITION = 7;
    private static final Pattern T_SEPARATOR = Pattern.compile("t([1-7])");

    /**
     * Interprets {{K|...}} parameters as documented in
     * <a href="https://de.wiktionary.org/wiki/Vorlage:K">Vorlage:K</a>. The parts render as
     * {@code part1 sep1 part2 sep2 … sepLast freeText}, where
     * <ul>
     *   <li>positional parameters become parts,</li>
     *   <li>{@code t1}..{@code t6} override the separator after the n-th part,
     *       only when another part follows it,</li>
     *   <li>{@code t7} overrides the separator before the free text ({@code ft})
     *       and applies however few parts there are,</li>
     *   <li>a declared separator of {@code "_"} renders as nothing, an undeclared one as {@code ","},</li>
     *   <li>{@code ft} becomes the suffix; when absent, {@code ":"} is used.</li>
     * </ul>
     * For example {{K|trans.|t1=;|besonders|bayrisch}} renders "transitiv; besonders, bayrisch:".
     * Out-of-range and unknown named parameters are ignored.
     */
    public static KTemplate fromTemplate(List<TemplateParameter> params) {
        List<RichText> contextTexts = contextTexts(params);
        Separators separators = declaredSeparators(params, contextTexts.size());
        Optional<RichText> freeText = freeText(params);

        List<Part> parts = joinWithSeparators(contextTexts, separators, freeText);
        return new KTemplate(parts, freeText.orElseGet(() -> new RichText(":")));
    }

    private static List<RichText> contextTexts(List<TemplateParameter> params) {
        return namedAndPositionalParams(params, PositionalTemplateParameter.class).stream()
                                                                                   .map(PositionalTemplateParameter::getValue)
                                                                                   .toList();
    }

    /** The {@code ft} parameter, or empty when absent; last one wins. */
    private static Optional<RichText> freeText(List<TemplateParameter> params) {
        RichText freeText = null;
        for (NamedTemplateParameter param : namedAndPositionalParams(params, NamedTemplateParameter.class))
            if (param.getName().equals("ft"))
                freeText = param.getValue();
        return Optional.ofNullable(freeText);
    }

    private static Separators declaredSeparators(List<TemplateParameter> params, int partCount) {
        Map<Integer, RichText> afterPart = new HashMap<>();
        RichText beforeFreeText = null;
        for (NamedTemplateParameter param : namedAndPositionalParams(params, NamedTemplateParameter.class)) {
            OptionalInt position = separatorPosition(param.getName());
            if (position.isEmpty())
                continue;
            int pos = position.getAsInt();
            RichText declared = declaredSeparator(param.getValue());
            if (pos == SEPARATOR_BEFORE_FREE_TEXT_POSITION)
                beforeFreeText = declared;
            else if (pos < partCount)
                afterPart.put(pos - 1, declared);
        }
        return new Separators(afterPart, beforeFreeText);
    }

    /**
     * @return the declared 1-based position when the parameter name is a separator override
     *         ({@code t1}..{@code t7}, the only ones Vorlage:K defines), empty otherwise
     */
    private static OptionalInt separatorPosition(String name) {
        Matcher matcher = T_SEPARATOR.matcher(name);
        return matcher.matches() ? OptionalInt.of(Integer.parseInt(matcher.group(1))) : OptionalInt.empty();
    }

    /** A declared separator of "_" means: render nothing between the parts. */
    private static RichText declaredSeparator(RichText value) {
        List<RichTextComponent> components = value.getComponents();
        boolean suppressed = components.size() == 1
                             && components.get(0) instanceof PlainText plain
                             && SEPARATOR_SUPPRESSION_MARKER.equals(plain.getText());
        return suppressed ? RichText.empty() : value;
    }

    private static List<Part> joinWithSeparators(List<RichText> contextTexts, Separators separators,
                                                 Optional<RichText> freeText) {
        List<Part> parts = new ArrayList<>();
        for (int partIndex = 0; partIndex < contextTexts.size(); partIndex++) {
            boolean lastPart = partIndex == contextTexts.size() - 1;
            parts.add(new Part(contextTexts.get(partIndex),
                               separatorAfter(separators, partIndex, lastPart, freeText)));
        }
        return parts;
    }

    private static RichText separatorAfter(Separators separators, int partIndex, boolean lastPart,
                                           Optional<RichText> freeText) {
        RichText declared = separators.afterPart(partIndex);
        if (declared != null)
            return declared;
        if (!lastPart)
            return comma();
        boolean rendersFreeText = freeText.filter(text -> !text.isEmpty()).isPresent();
        if (!rendersFreeText)
            return null;
        return separators.beforeFreeText() != null ? separators.beforeFreeText() : comma();
    }

    private static RichText comma() {
        return new RichText(COMMA_TEXT);
    }

    private static <T extends TemplateParameter> List<T> namedAndPositionalParams(List<TemplateParameter> params,
                                                                                   Class<T> type) {
        return params.stream().filter(type::isInstance).map(type::cast).toList();
    }

    /**
     * Separator overrides declared by {@code tN} parameters: {@code afterPart} maps a part index
     * to the separator rendered right after it, {@code beforeFreeText} is {@code t7}.
     */
    private record Separators(Map<Integer, RichText> afterPart, RichText beforeFreeText) {

        RichText afterPart(int partIndex) {
            return afterPart.get(partIndex);
        }
    }

    @Override
    public void accept(RichTextComponentVisitor visitor) {
        visitor.visit(this);
        for (Part part : parts) {
            part.text().accept(visitor);
            if (part.separator() != null)
                part.separator().accept(visitor);
        }
        if (suffix != null)
            suffix.accept(visitor);
    }

    @Override
    public Optional<? extends RichTextComponent> mergeWith(RichTextComponent component) {
        return Optional.empty();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public record Part(RichText text, RichText separator) {
    }
}
