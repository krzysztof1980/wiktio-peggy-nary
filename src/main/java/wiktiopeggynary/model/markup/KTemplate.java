package wiktiopeggynary.model.markup;

import wiktiopeggynary.model.visitor.RichTextComponentVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Krzysztof Witukiewicz
 */
public record KTemplate(List<Part> parts, RichText suffix) implements RichTextComponent {

    public static KTemplate fromTemplate(List<TemplateParameter> params) {
        List<Part> parts = new ArrayList<>();
        params.stream()
              .filter(PositionalTemplateParameter.class::isInstance)
              .map(PositionalTemplateParameter.class::cast)
              .forEach(p -> parts.add(new Part(p.getValue(), null)));

        RichText suffix = null;
        RichText beforeSuffix = null;
        for (TemplateParameter param : params) {
            if (!(param instanceof NamedTemplateParameter p))
                continue;
            String name = p.getName();
            if (name.equals("ft")) {
                suffix = p.getValue();
            } else if (name.matches("t[0-9]+")) {
                // Vorlage:K: t1..t6 = separator between positional parameter N and N+1;
                // t7 = separator between the last positional parameter and ft, which applies
                // even with fewer than seven positional parameters. A value of "_" suppresses
                // the separator. Out-of-range tN is ignored instead of throwing.
                int pos = Integer.parseInt(name.substring(1));
                RichText separator = separatorOf(p.getValue());
                if (pos == 7) {
                    beforeSuffix = separator;
                } else if (pos >= 1 && pos < parts.size()) {
                    Part part = parts.get(pos - 1);
                    parts.set(pos - 1, new Part(part.text(), separator));
                }
            }
        }
        // set default value for separator in all but the last Part, if null
        for (int i = 0; i < parts.size() - 1; i++) {
            Part part = parts.get(i);
            if (part.separator() == null)
                parts.set(i, new Part(part.text(), new RichText(",")));
        }
        // separator between the last Part and the ft text: comma by default, t7 overrides it
        if (!parts.isEmpty() && suffix != null && !suffix.isEmpty()) {
            int last = parts.size() - 1;
            Part lastPart = parts.get(last);
            if (lastPart.separator() == null)
                parts.set(last, new Part(lastPart.text(), beforeSuffix != null ? beforeSuffix : new RichText(",")));
        }
        // set default value for suffix, if null
        if (suffix == null)
            suffix = new RichText(":");

        return new KTemplate(List.copyOf(parts), suffix);
    }

    private static RichText separatorOf(RichText value) {
        List<RichTextComponent> components = value.getComponents();
        if (components.size() == 1 && components.get(0) instanceof PlainText plain && "_".equals(plain.getText()))
            return RichText.empty();   // "_" suppresses the separator (Vorlage:K)
        return value;
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
