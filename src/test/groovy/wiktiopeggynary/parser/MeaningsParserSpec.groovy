package wiktiopeggynary.parser

import spock.lang.Specification
import spock.lang.Unroll

import static wiktiopeggynary.parser.util.ResourceUtils.readArticleFromResources

/**
 * @author Krzysztof Witukiewicz
 */
@Unroll
class MeaningsParserSpec extends Specification {

    def "'#lemma' meaning[#meaningIdx] (#notes) = '#meaningDetails': '#meaningDefinition'"() {
        when:
        def entry = ParserService.getInstance().parseWiktionaryPage(
                readArticleFromResources(lemma))[0]
        def meaning = entry.getMeanings().get(meaningIdx)

        then:
        meaning.text.asText() == meaningText

        where:
        notes                             | lemma        | meaningIdx || meaningText
//        "simple case"                     | "Staat"      | 1          || "Gebiet, auf dem ein Staat liegt"
//        "template as details"             | "Staat"      | 2          || "umgangssprachlich, nur Plural: die Vereinigten Staaten von Amerika"
//        "templates as details"            | "Kartoffel"  | 4          || "iron., scherzh.: großes Loch im Strumpf"
//        "citation as details"             | "Boot"       | 1          || "Militär seegehende Einheiten einer bestimmten Größenordnung bei der Marine"
//        "sub-meaning"                     | "Dirham"     | 3          || "in Katar (kleinere Einheit des Katar-Riyals)"
//        "sub-meaning (with '*' in front)" | "Aberration" | 1          || "jährliche Aberration: aufgr und des Erdumlaufs um die Sonne"
    }
}