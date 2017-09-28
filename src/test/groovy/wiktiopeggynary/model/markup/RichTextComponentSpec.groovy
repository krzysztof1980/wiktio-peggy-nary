package wiktiopeggynary.model.markup

import spock.lang.Specification

/**
 * @author Krzysztof Witukiewicz
 */
class RichTextComponentSpec extends Specification {

    def "merge 2 PlainText components"() {
        when:
        def optMergeResult = new PlainText("Hello, ").mergeWith(new PlainText("World!"))

        then:
        optMergeResult.isPresent()
        optMergeResult.get().text == "Hello, World!"
    }

    def "merge PlainText with component of different type"() {
        given:
        def textComp = new PlainText("She said: ")

        when:
        def optMergeResult = textComp.mergeWith(new CursiveBlock(new RichText("Hello, stranger!")))

        then:
        !optMergeResult.isPresent()
    }

    def "merge 2 CursiveBlock components"() {
        given:
        def cBlock = new CursiveBlock(new RichText("a part of..."))

        when:
        def optMergeResult = cBlock.mergeWith(new CursiveBlock(new RichText(" a longer citation")))

        then:
        optMergeResult.isPresent()
        optMergeResult.get().body == new RichText("a part of... a longer citation")
    }
}
