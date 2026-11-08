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
        for (TemplateParameter param : params) {
            if (!(param instanceof NamedTemplateParameter p))
                continue;
            if (p.getName().startsWith("t")) {
                int pos = Integer.parseInt(p.getName().substring(1));
                Part part = parts.get(pos - 1);
                parts.set(pos - 1, new Part(part.text(), p.getValue()));
            } else if (p.getName().equals("ft")) {
                suffix = p.getValue();
            }
        }
        // set default value for separator in all but the last Part, if null
        for (int i = 0; i < parts.size() - 1; i++) {
            Part part = parts.get(i);
            if (part.separator() == null)
                parts.set(i, new Part(part.text(), new RichText(",")));
        }
        // set default value for suffix, if null
        if (suffix == null)
            suffix = new RichText(":");

        return new KTemplate(List.copyOf(parts), suffix);
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
