package wiktiopeggynary.model.substantiv;

import wiktiopeggynary.model.WiktionaryEntry;

/**
 * @author Krzysztof Witukiewicz
 */
public class Substantiv extends WiktionaryEntry {

    // TODO: use enum
    private String gender;
    private FlexionTable flexionTable = new FlexionTable();

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public FlexionTable getFlexionTable() {
        return flexionTable;
    }

    @Override
    public String toString() {
        return "Substantiv{" +
                "gender='" + gender + '\'' +
                ", flexionTable=" + flexionTable +
                "} " + super.toString();
    }
}
