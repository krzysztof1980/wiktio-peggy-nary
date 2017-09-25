package wiktiopeggynary.model

import org.apache.commons.lang3.StringUtils
import spock.lang.Subject
import wiktiopeggynary.model.markup.ItemNumber
import wiktiopeggynary.model.substantiv.Substantiv
import wiktiopeggynary.parser.ParserSpecBase

/**
 * @author Krzysztof Witukiewicz
 */
class WiktionaryEntrySerializerSpec extends ParserSpecBase {

    @Subject
    def serializer = new WiktionaryEntrySerializer()

    def "generic entry"() {
        when:
        def entry = getWiktionaryEntry("Kartoffel", 0, Substantiv)

        then: "lemma is set"
        !StringUtils.isBlank(entry.lemma)

        and: "translations are set"
        !entry.translations.isEmpty()
        def enTransMeaning0 = entry.translations["en"][0]
        enTransMeaning0.meaningNumbers == [ItemNumber.singleNumber("1")]
        enTransMeaning0.translations[0].internalLink == "potato"

        and: "meanings are set"
        !entry.meanings.isEmpty()
        def meaning = entry.meanings[4]
        meaning.numbers == [ItemNumber.singleNumber("5")]
        meaning.kontext.parts.size() == 2
        meaning.text.components.size() == 4
    }

    def "complex substantiv"() {
        when:
        def entry = getWiktionaryEntry("Boot", 0, Substantiv)

        then: "gender is set"
        entry.gender.genders.length > 0

        and: "Singular flexion forms are set"
        def sgFlexionForms = entry.getFlexionForms(Numerus.Singular)
        !sgFlexionForms.isEmpty()
        def sgDativ = sgFlexionForms[0].flexions.find {f -> f.kasus == Kasus.Dativ}
        sgDativ.variants.size() == 2
        sgDativ.variants[1] == "Boote"

        and: "Plural flexion forms are set"
        def plFlexionForms = entry.getFlexionForms(Numerus.Plural)
        !plFlexionForms.isEmpty()
        plFlexionForms.size() == 2
        def plDativ = plFlexionForms[1].flexions.find {f -> f.kasus == Kasus.Dativ}
        plDativ.variants.size() == 1
        plDativ.variants[0] == "Böten"
    }

    def "substantiv with attributes"() {
        when:
        def entry = getWiktionaryEntry("Kim", 0, Substantiv)

        then:
        !entry.attributes.isEmpty()
        entry.attributes[0] == "Vorname"
    }

    private <T extends WiktionaryEntry> T getWiktionaryEntry(String lemma, int idx, Class<T> valueType) {
        def entry = parseWiktionaryEntryPage(lemma).wiktionaryEntries[idx] as T
        def jsonString = serializer.serializeWiktionaryEntry(entry)
        serializer.deserializeWiktionaryEntry(jsonString, valueType)
    }
}
