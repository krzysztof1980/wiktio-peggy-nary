package wiktiopeggynary.model.substantiv;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Krzysztof Witukiewicz
 */
public class FlexionTable {

    private final List<FlexionForm> flexionForms = new ArrayList<>();

    public void addFlexionForm(FlexionForm flexionForm) {
        flexionForms.add(flexionForm);
    }

    public Collection<FlexionForm> getFlexionForms() {
        return Collections.unmodifiableCollection(flexionForms);
    }

    @Override
    public String toString() {
        return "FlexionTable {\n"
                + flexionForms.stream().map(FlexionForm::toString).collect(Collectors.joining("\n"))
                + "\n}";
    }
}
