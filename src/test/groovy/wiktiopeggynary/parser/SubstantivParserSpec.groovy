package wiktiopeggynary.parser

import spock.lang.Unroll
import wiktiopeggynary.model.Kasus
import wiktiopeggynary.model.Numerus
import wiktiopeggynary.model.substantiv.Gender
import wiktiopeggynary.model.substantiv.Substantiv

/**
 * @author Krzysztof Witukiewicz
 */
class SubstantivParserSpec extends ParserSpecBase {

    def "gender and other attributes"() {
        when:
        def entries = parseWiktionaryEntryPage("Boot").wiktionaryEntries

        then: "there are 3 entries"
        entries.size() == 3

        and: "first entry is Neutrum"
        Substantiv entry1 = entries[0] as Substantiv
        entry1.gender.isSameAs(Gender.NEUTRUM)
        entry1.attributes.isEmpty()

        and: "second entry is Maskulinum"
        Substantiv entry2 = entries[1] as Substantiv
        entry2.gender.isSameAs(Gender.MASKULINUM)
        entry2.attributes.isEmpty()

        and: "third entry can be Maskulinum or Femininum and is a name"
        Substantiv entry3 = entries[2] as Substantiv
        entry3.gender.genders.size() == 2
        entry3.gender.genders[0] == Gender.MASKULINUM
        entry3.gender.genders[1] == Gender.FEMININUM
        entry3.attributes.size() == 1
        entry3.attributes[0] == "Nachname"
    }

    def "adjektivische Deklination"() {
        when:
        def entries = parseWiktionaryEntryPage("Bahnangestellter").wiktionaryEntries
        Substantiv entry = entries[0] as Substantiv

        then: "it is Maskulinum"
        entry.gender.isSameAs(Gender.MASKULINUM)

        and: "there is attribute for adjektivische Deklination"
        entry.attributes.size() == 1
        entry.attributes[0] == Substantiv.ATTR_ADJ_DEKLINATION
    }

    def "adjektivische Deklination with error"() {
        when:
        def entries = parseWiktionaryEntryPage("Suchende").wiktionaryEntries
        Substantiv entry = entries[0] as Substantiv

        then: "it is Femininum"
        entry.gender.isSameAs(Gender.FEMININUM)

        and: "there is attribute for adjektivische Deklination"
        entry.attributes.size() == 1
        entry.attributes[0] == Substantiv.ATTR_ADJ_DEKLINATION
    }

    def "flexion table with multiple variants of single form"() {
        when:
        def entries = parseWiktionaryEntryPage("Zug").wiktionaryEntries
        Substantiv entry = entries[0] as Substantiv
        def flexionForms = entry.getFlexionForms(Numerus.Singular)

        then:
        flexionForms.size() == 1
        def flexionForm1 = flexionForms[0]
        flexionForm1.gender == Gender.MASKULINUM
        def genitiv = flexionForms[0].flexions[1]
        genitiv.kasus == Kasus.Genitiv
        genitiv.variants.size() == 2
        genitiv.variants[0] == "Zuges"
        genitiv.variants[1] == "Zugs"
    }

    def "flexion table with multiple flexion forms"() {
        when:
        def entries = parseWiktionaryEntryPage("Boot").wiktionaryEntries
        Substantiv entry = entries[0] as Substantiv
        def flexionForms = entry.getFlexionForms(Numerus.Plural)

        then:
        flexionForms.size() == 2

        and: // first form of Nominativ Plural
        def flexionForm1 = flexionForms[0]
        flexionForm1.gender == Gender.PLURAL
        def nominativ1 = flexionForm1.flexions[0]
        nominativ1.kasus == Kasus.Nominativ
        nominativ1.variants.size() == 1
        nominativ1.variants[0] == "Boote"

        and: // second form of Nominativ Plural
        def flexionForm2 = flexionForms[1]
        flexionForm2.gender == Gender.PLURAL
        def nominativ2 = flexionForm2.flexions[0]
        nominativ2.kasus == Kasus.Nominativ
        nominativ2.variants.size() == 1
        nominativ2.variants[0] == "Böte"
    }

    def "flexion table with multiple variants in first of 2 singular forms"() {
        when:
        def entries = parseWiktionaryEntryPage("Monat").wiktionaryEntries
        Substantiv entry = entries[0] as Substantiv
        def flexionForms = entry.getFlexionForms(Numerus.Singular)

        then:
        flexionForms.size() == 2

        and: // first form of Genitiv Singular
        def flexionForm1 = flexionForms[0]
        flexionForm1.gender == Gender.MASKULINUM
        def genitiv1 = flexionForm1.flexions[1]
        genitiv1.kasus == Kasus.Genitiv
        genitiv1.variants.size() == 2
        genitiv1.variants[0] == "Monates"
        genitiv1.variants[1] == "Monats"

        and: // second form of Genitiv Singular
        def flexionForm2 = flexionForms[1]
        flexionForm2.gender == Gender.NEUTRUM
        def genitiv2 = flexionForm2.flexions[1]
        genitiv2.kasus == Kasus.Genitiv
        genitiv2.variants.size() == 1
        genitiv2.variants[0] == "Monats"
    }

    def "no gender defined in the flexion table"() {
        when:
        def entries = parseWiktionaryEntryPage("Oachkatzlschwoaf").wiktionaryEntries
        Substantiv entry = entries[0] as Substantiv
        def flexionForms = entry.getFlexionForms(Numerus.Singular)

        then: "there is one flexion form for Singular"
        flexionForms.size() == 1
        def flexionForm1 = flexionForms[0]

        and: "its gender is set to the gender of Substantiv"
        entry.gender.isSameAs(Gender.MASKULINUM)
        flexionForm1.gender == Gender.MASKULINUM
    }

    @Unroll
    def "#lemma has no singular form - gender not specified in header and #testcase"() {
        when:
        def entries = parseWiktionaryEntryPage(lemma).wiktionaryEntries
        Substantiv entry = entries[0] as Substantiv

        then:
        entry.gender.isSameAs(Gender.PLURAL)

        where:
        lemma        | testcase
        "Eltern"     | "Genus=0 in the flexion table"
        "Everglades" | "Genus=x in the flexion table"
        "Seschellen" | "Genus=pl in the flexion table"
    }
}
