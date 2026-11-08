package wiktiopeggynary.model.markup;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Krzysztof Witukiewicz
 */
class RichTextComponentTest {

    @Test
    void merge2PlainTextComponents() {
        Optional<PlainText> optMergeResult = new PlainText("Hello, ").mergeWith(new PlainText("World!"));

        assertTrue(optMergeResult.isPresent());
        assertEquals("Hello, World!", optMergeResult.get().getText());
    }

    @Test
    void mergePlainTextWithComponentOfDifferentType() {
        PlainText textComp = new PlainText("She said: ");

        Optional<PlainText> optMergeResult = textComp.mergeWith(new CursiveBlock(new RichText("Hello, stranger!")));

        assertFalse(optMergeResult.isPresent());
    }

    @Test
    void merge2CursiveBlockComponents() {
        CursiveBlock cBlock = new CursiveBlock(new RichText("a part of..."));

        Optional<CursiveBlock> optMergeResult = cBlock.mergeWith(new CursiveBlock(new RichText(" a longer citation")));

        assertTrue(optMergeResult.isPresent());
        assertEquals(new RichText("a part of... a longer citation"), optMergeResult.get().getBody());
    }
}
