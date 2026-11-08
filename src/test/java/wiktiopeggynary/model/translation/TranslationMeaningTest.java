package wiktiopeggynary.model.translation;

import org.junit.jupiter.api.Test;
import wiktiopeggynary.model.markup.CursiveBlock;
import wiktiopeggynary.model.markup.ItemNumber;
import wiktiopeggynary.model.markup.PlainText;
import wiktiopeggynary.model.markup.RichText;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Krzysztof Witukiewicz
 */
class TranslationMeaningTest {

    @Test
    void translationInsideCursiveBlock() {
        TranslationMeaning transMeaning = new TranslationMeaning(List.of(ItemNumber.singleNumber("1")),
                                                                 new RichText(new CursiveBlock(new RichText(new Translation()))));

        assertEquals(1, transMeaning.getTranslations().size());
    }

    @Test
    void textWithTranslationsInVariousPlaces() {
        TranslationMeaning transMeaning = new TranslationMeaning(List.of(ItemNumber.singleNumber("1")),
                                                                 new RichText(new PlainText("text "),
                                                                              new Translation(),
                                                                              new CursiveBlock(new RichText(new Translation()))));

        assertEquals(2, transMeaning.getTranslations().size());
    }
}
