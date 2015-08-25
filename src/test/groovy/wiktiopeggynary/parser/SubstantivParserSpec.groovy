package wiktiopeggynary.parser

import spock.lang.Specification
import wiktiopeggynary.model.Kasus
import wiktiopeggynary.model.Numerus
import wiktiopeggynary.model.substantiv.Gender
import wiktiopeggynary.model.substantiv.Substantiv

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
class SubstantivParserSpec extends Specification {

    def "gender and other attributes"() {
        when:
        def entries = ParserService.getInstance().parseWiktionaryEntryPage(
                readArticleFromResources("Boot"))

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
        def entries = ParserService.getInstance().parseWiktionaryEntryPage(
                readArticleFromResources("Bahnangestellter"))
        Substantiv entry = entries[0] as Substantiv

        then: "it is Maskulinum"
        entry.gender.isSameAs(Gender.MASKULINUM)

        and: "there is attribute for adjektivische Deklination"
        entry.attributes.size() == 1
        entry.attributes[0] == Substantiv.ATTR_ADJ_DEKLINATION
    }

    def "adjektivische Deklination with error"() {
        when:
        def entries = ParserService.getInstance().parseWiktionaryEntryPage(
                readArticleFromResources("Suchende"))
        Substantiv entry = entries[0] as Substantiv

        then: "it is Femininum"
        entry.gender.isSameAs(Gender.FEMININUM)

        and: "there is attribute for adjektivische Deklination"
        entry.attributes.size() == 1
        entry.attributes[0] == Substantiv.ATTR_ADJ_DEKLINATION
    }

    def "flexion table with line breaks"() {
        when:
        def entries = ParserService.getInstance().parseWiktionaryEntryPage(
                readArticleFromResources("Zug"))
        Substantiv entry = entries[0] as Substantiv
        def flexionForms = entry.getFlexionTable().getFlexionForms()

        then:
        flexionForms[2].kasus == Kasus.Genitiv
        flexionForms[2].numerus == Numerus.Singular
        flexionForms[2].variants.size() == 2
        flexionForms[2].variants[0] == "des Zuges"
        flexionForms[2].variants[1] == "des Zugs"
    }

    def "flexion table without plural"() {
        when:
        def entries = ParserService.getInstance().parseWiktionaryEntryPage(
                readArticleFromResources("Zug"))
        Substantiv entry = entries[1] as Substantiv
        def flexionForms = entry.getFlexionTable().getFlexionForms()

        then: "forms for Plural are empty"
        flexionForms.size() == 8
        flexionForms.stream().allMatch {
            ff -> ff.numerus == Numerus.Singular || (ff.variants.size() == 0 && !ff.unparsed)
        }
    }

    def "flexion table with unknown format"() {
        when:
        def entries = ParserService.getInstance().parseWiktionaryEntryPage(
                readArticleFromResources("Zug"))
        Substantiv entry = entries[1] as Substantiv
        def flexionForms = entry.getFlexionTable().getFlexionForms()

        then: "forms for Singular have all unknown format"
        flexionForms.size() == 8
        flexionForms.stream().allMatch {
            ff -> ff.numerus == Numerus.Plural || (ff.variants.size() == 0 && ff.unparsed)
        }
    }
}
