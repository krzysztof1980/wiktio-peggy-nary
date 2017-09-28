package wiktiopeggynary.model.translation

import spock.lang.Specification
import wiktiopeggynary.model.markup.CursiveBlock
import wiktiopeggynary.model.markup.ItemNumber
import wiktiopeggynary.model.markup.PlainText
import wiktiopeggynary.model.markup.RichText

/**
 * @author Krzysztof Witukiewicz
 */
class TranslationMeaningSpec extends Specification {

    def "translation inside cursive-block"() {
        given:
        def transMeaning = new TranslationMeaning([ItemNumber.singleNumber("1")],
                                                  new RichText(new CursiveBlock(new RichText(new Translation()))))

        expect:
        transMeaning.translations.size() == 1
    }

    def "text with translations in various places"() {
        given:
        def transMeaning = new TranslationMeaning([ItemNumber.singleNumber("1")],
                                                  new RichText(new PlainText("text "),
                                                               new Translation(),
                                                               new CursiveBlock(new RichText(new Translation()))))

        expect:
        transMeaning.translations.size() == 2
    }
}
