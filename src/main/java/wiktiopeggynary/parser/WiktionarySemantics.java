//=========================================================================
//
//  This skeleton was generated by Mouse 1.6.1 at 2015-07-07 23:04:29 GMT
//  from grammar
//    '/home/krzysiek/dev/projects/fiszki/wiktionary-parser/src/main/java/p
//    l/kwitukiewicz/wiktionary/parser/../../../../../resources/grammar.txt
//    '.
//
//=========================================================================

package wiktiopeggynary.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.model.Kasus;
import wiktiopeggynary.model.Numerus;
import wiktiopeggynary.model.WiktionaryEntry;
import wiktiopeggynary.model.substantiv.FlexionForm;
import wiktiopeggynary.model.substantiv.MultiGender;
import wiktiopeggynary.model.substantiv.Substantiv;
import wiktiopeggynary.model.translation.Translation;
import wiktiopeggynary.model.translation.TranslationMeaning;
import wiktiopeggynary.parser.mouse.Phrase;
import wiktiopeggynary.parser.mouse.SemanticsBase;

import java.util.*;

class WiktionarySemantics extends SemanticsBase {

    private static Logger logger = LoggerFactory.getLogger(WiktionarySemantics.class);

    private String lemma;
    private WiktionaryEntry entryWorkingCopy;

    private Stack<WiktionaryEntry> wiktionaryEntries = new Stack<>();

    public Collection<WiktionaryEntry> getWiktionaryEntries() {
        return Collections.unmodifiableCollection(wiktionaryEntries);
    }

    void saveEintrag() {
        wiktionaryEntries.push(entryWorkingCopy);
    }

    void WortartBody_fail() {
        logger.error("[lemma={}] DeWortart_fail: {}", lemma, getFormattedErrorMessageForLogging());
        lhs().errClear();
    }

    //-------------------------------------------------------------------
    //  WortartTemplate = LT "Wortart" IT TemplateAttr IT "Deutsch" RT
    //-------------------------------------------------------------------
    void WortartTemplate() {
        lhs().put(rhs(3).text());
    }

    //-------------------------------------------------------------------
    //  Lemma = Letter+ Space
    //-------------------------------------------------------------------
    void Lemma() {
        lemma = rhsText(0, rhsSize() - 1);
    }

    void createSubstantiv() {
        entryWorkingCopy = new Substantiv();
        entryWorkingCopy.setLemma(lemma);
    }

    void SubstantivAttributes_0_fail() {
        logger.error("Exception parsing Substantiv attribute for lemma '{}': {}", lemma, getFormattedErrorMessageForLogging());
        lhs().errClear();
    }

    void substantivGender() {
        ((Substantiv) entryWorkingCopy).setGender((MultiGender) rhs(0).get());
    }

    void substantivWortart() {
        ((Substantiv) entryWorkingCopy).addAttribute((String) rhs(0).get());
    }

    void substantivAdjDeklination() {
        ((Substantiv) entryWorkingCopy).addAttribute(Substantiv.ATTR_ADJ_DEKLINATION);
    }

    //-------------------------------------------------------------------
    //  Gender = LT Letter+ RT Space
    //-------------------------------------------------------------------
    void Gender() {
        String genderText = rhsText(1, rhsSize() - 2);
        try {
            lhs().put(new MultiGender(genderText));
        } catch (IllegalArgumentException e) {
            logger.error("Exception parsing gender '{}' for lemma '{}'", genderText, lemma);
            throw new ParseException(String.format("Exception parsing article for lemma '%s'", lemma), e);
        }
    }

    //-------------------------------------------------------------------
    //  NomSg = "Nominativ Singular" Space Digit Space "="
    //    FlexionVariantList EOL
    //-------------------------------------------------------------------
    void addNomSg() {
        addFlexionForm(Kasus.Nominativ, Numerus.Singular);
    }

    //-------------------------------------------------------------------
    //  NomPl = "Nominativ Plural" Space Digit Space "="
    //    FlexionVariantList EOL
    //-------------------------------------------------------------------
    void addNomPl() {
        addFlexionForm(Kasus.Nominativ, Numerus.Plural);
    }

    //-------------------------------------------------------------------
    //  GenSg = "Genitiv Singular" Space Digit Space "="
    //    FlexionVariantList EOL
    //-------------------------------------------------------------------
    void addGenSg() {
        addFlexionForm(Kasus.Genitiv, Numerus.Singular);
    }

    //-------------------------------------------------------------------
    //  GenPl = "Genitiv Plural" Space Digit Space "=" FlexionVariantList
    //    EOL
    //-------------------------------------------------------------------
    void addGenPl() {
        addFlexionForm(Kasus.Genitiv, Numerus.Plural);
    }

    //-------------------------------------------------------------------
    //  DatSg = "Dativ Singular" Space Digit Space "=" FlexionVariantList
    //    EOL
    //-------------------------------------------------------------------
    void addDatSg() {
        addFlexionForm(Kasus.Dativ, Numerus.Singular);
    }

    //-------------------------------------------------------------------
    //  DatPl = "Dativ Plural" Space Digit Space "=" FlexionVariantList
    //    EOL
    //-------------------------------------------------------------------
    void addDatPl() {
        addFlexionForm(Kasus.Dativ, Numerus.Plural);
    }

    //-------------------------------------------------------------------
    //  AkkSg = "Akkusativ Singular" Space Digit Space "="
    //    FlexionVariantList EOL
    //-------------------------------------------------------------------
    void addAkkSg() {
        addFlexionForm(Kasus.Akkusativ, Numerus.Singular);
    }

    //-------------------------------------------------------------------
    //  AkkPl = "Akkusativ Plural" Space Digit Space "="
    //    FlexionVariantList EOL
    //-------------------------------------------------------------------
    void addAkkPl() {
        addFlexionForm(Kasus.Akkusativ, Numerus.Plural);
    }

    private void addFlexionForm(Kasus kasus, Numerus numerus) {
        FlexionForm flexionForm = new FlexionForm(kasus, numerus);
        Phrase flexionVariantListPhrase = rhs(5);
        if (flexionVariantListPhrase.get() != null) {
            ((Iterable<String>) flexionVariantListPhrase.get()).forEach(v -> flexionForm.addVariant(v));
        } else {
            flexionForm.setUnparsedForm(flexionVariantListPhrase.text());
        }
        ((Substantiv) wiktionaryEntries.peek()).getFlexionTable().addFlexionForm(flexionForm);
    }

    //-------------------------------------------------------------------
    //  FlexionVariantList = Space Phrase (BR Phrase)*
    //-------------------------------------------------------------------
    void FlexionVariantList_0() {
        List<String> variants = new ArrayList<>();
        for (int i = 1; i < rhsSize(); i += 2) {
            variants.add(rhs(i).text());
        }
        lhs().put(variants);
    }

    //-------------------------------------------------------------------
    //  FlexionVariantList = Space "-" Space
    //-------------------------------------------------------------------
    void FlexionVariantList_1() {
        lhs().put(new ArrayList<>());
    }

    //-------------------------------------------------------------------
    //  LangTranslations = LangLvl Lang ":" Space TranslationMeaning
    //      (";" Space TranslationMeaning)* EOL
    //-------------------------------------------------------------------
    void addTranslationsForLanguage() {
        String language = (String) rhs(1).get();
        for (int i = 4; i < rhsSize(); i += 3)
            wiktionaryEntries.peek().addTranslationMeaning(language, (TranslationMeaning) rhs(i).get());
    }

    //-------------------------------------------------------------------
    //  Lang = LT Word RT
    //-------------------------------------------------------------------
    void Lang() {
        lhs().put(rhs(1).text());
    }

    //-------------------------------------------------------------------
    //  TranslationMeaning = TranslationMeaningNo Translation
    //      ("," Space Translation)*
    //-------------------------------------------------------------------
    void TranslationMeaning() {
        TranslationMeaning meaning = new TranslationMeaning((String) rhs(0).get());
        for (int i = 1; i < rhsSize(); i += 3)
            meaning.addTranslation((Translation) rhs(i).get());
        lhs().put(meaning);
    }

    //-------------------------------------------------------------------
    //  TranslationMeaningNo = "[" _++ "]" Space
    //-------------------------------------------------------------------
    void TranslationMeaningNo() {
        lhs().put(rhsText(1, rhsSize() - 2));
    }

    //-------------------------------------------------------------------
    //  Translation = LT (UeTemplate / UetTemplate) RT Space Gender?
    //-------------------------------------------------------------------
    void Translation() {
        Translation t = (Translation) rhs(1).get();
        if (rhsSize() == 5)
            t.setGender((MultiGender) rhs(4).get());
        lhs().put(t);
    }

    //-------------------------------------------------------------------
    //  UeTemplate = ("Ü" / "Ü?") IT TemplateAttr IT TemplateAttr
    //      (IT TemplateAttr)? (IT TemplateAttr)?
    //-------------------------------------------------------------------
    void UeTemplate() {
        Translation translation = new Translation();
        translation.setInternalLink(rhs(4).text());
        if (rhsSize() >= 7)
            translation.setLabel(rhs(6).text());
        if (rhsSize() == 9)
            translation.setExternalLink(rhs(8).text());
        lhs().put(translation);
    }

    //-------------------------------------------------------------------
    //  UetTemplate = ("Ü" / "Ü?") IT TemplateAttr IT TemplateAttr
    //      IT TemplateAttr (IT TemplateAttr)? (IT TemplateAttr)?
    //-------------------------------------------------------------------
    void UetTemplate() {
        Translation translation = new Translation();
        translation.setInternalLink(rhs(4).text());
        translation.setTranscription(rhs(6).text());
        if (rhsSize() >= 9)
            translation.setLabel(rhs(8).text());
        if (rhsSize() == 11)
            translation.setExternalLink(rhs(10).text());
        lhs().put(translation);
    }

    private String getFormattedErrorMessageForLogging() {
        return lhs().errMsg().replace("\n", "\\n");
    }
}
