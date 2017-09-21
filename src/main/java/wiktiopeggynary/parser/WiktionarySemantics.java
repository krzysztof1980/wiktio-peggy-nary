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

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wiktiopeggynary.model.*;
import wiktiopeggynary.model.markup.*;
import wiktiopeggynary.model.substantiv.*;
import wiktiopeggynary.model.translation.Translation;
import wiktiopeggynary.model.translation.TranslationMeaning;
import wiktiopeggynary.model.visitor.RichTextEvaluator;
import wiktiopeggynary.parser.mouse.SemanticsBase;
import wiktiopeggynary.parser.template.TemplateService;

import java.util.*;
import java.util.stream.IntStream;

class WiktionarySemantics extends SemanticsBase {
	
	private static final Logger logger = LoggerFactory.getLogger(WiktionarySemantics.class);
	private static final Logger erroneousEntriesLogger = LoggerFactory.getLogger("erroneous_wiktionary_entries");
	
	private String lemma;
	private WiktionaryEntry entryWorkingCopy;
	
	private Stack<WiktionaryEntry> wiktionaryEntries = new Stack<>();
	
	private TemplateService templateService;
	
	void setTemplateService(TemplateService templateService) {
		Validate.isTrue(this.templateService == null, "templateService can be initialized only once");
		this.templateService = templateService;
	}
	
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
	
	void DeEintragWithErrors() {
		logger.error("The entry for '{}' is german, but cannot be parsed", lemma);
		erroneousEntriesLogger.error(lemma + " (german, but cannot be parsed)");
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
	
	void SubstantivAttributes_fail() {
		logger.error("Exception parsing Substantiv attribute for lemma '{}': {}", lemma,
		             getFormattedErrorMessageForLogging());
		lhs().errClear();
	}
	
	void substantivGender() {
		((Substantiv) entryWorkingCopy).setGender((MultiGender) rhs(0).get());
	}
	
	//-------------------------------------------------------------------
	// Gender = LT GenderTemplateArgument "."? RT Space
	//-------------------------------------------------------------------
	void Gender() {
		lhs().put(rhs(1).get());
	}
	
	//-------------------------------------------------------------------
	// GenderTemplateArgument = [mnfu]+
	//-------------------------------------------------------------------
	void GenderTemplateArgument() {
		lhs().put(new MultiGender(lhs().text()));
	}
	
	void substantivWortart() {
		((Substantiv) entryWorkingCopy).addAttribute((String) rhs(0).get());
	}
	
	void substantivAdjDeklination() {
		((Substantiv) entryWorkingCopy).addAttribute(Substantiv.ATTR_ADJ_DEKLINATION);
	}
	
	//=====================================================================
	//  Genus = "Genus" (Space Digit)? "=" ([mnfu0x] / "pl") EOL
	//                                            n-2        n-1
	//=====================================================================
	void addGenus() {
		Gender gender = Gender.fromShortcut(rhs(rhsSize() - 2).text());
		if (gender != Gender.PLURAL) {
			((Substantiv) entryWorkingCopy).addFlexionForm(Numerus.Singular,
			                                               new FlexionForm(gender));
		} else {
			((Substantiv) entryWorkingCopy).setGender(new MultiGender(Gender.PLURAL));
		}
	}
	
	//-------------------------------------------------------------------
	//  NomSg = "Nominativ Singular" Space OptDigit "*"? Space "=" Phrase EOL
	//-------------------------------------------------------------------
	void addNomSg() {
		addFlexion(Kasus.Nominativ, Numerus.Singular);
	}
	
	//-------------------------------------------------------------------
	//  NomPl = "Nominativ Plural" Space OptDigit "*"? Space "=" Phrase EOL
	//-------------------------------------------------------------------
	void addNomPl() {
		addFlexion(Kasus.Nominativ, Numerus.Plural);
	}
	
	//-------------------------------------------------------------------
	//  GenSg = "Genitiv Singular" Space OptDigit "*"? Space "=" Phrase EOL
	//-------------------------------------------------------------------
	void addGenSg() {
		addFlexion(Kasus.Genitiv, Numerus.Singular);
	}
	
	//-------------------------------------------------------------------
	//  GenPl = "Genitiv Plural" Space OptDigit "*"? Space "=" Phrase EOL
	//-------------------------------------------------------------------
	void addGenPl() {
		addFlexion(Kasus.Genitiv, Numerus.Plural);
	}
	
	//-------------------------------------------------------------------
	//  DatSg = "Dativ Singular" Space OptDigit "*"? Space "=" Phrase EOL
	//-------------------------------------------------------------------
	void addDatSg() {
		addFlexion(Kasus.Dativ, Numerus.Singular);
	}
	
	//-------------------------------------------------------------------
	//  DatPl = "Dativ Plural" Space OptDigit "*"? Space "=" Phrase EOL
	//-------------------------------------------------------------------
	void addDatPl() {
		addFlexion(Kasus.Dativ, Numerus.Plural);
	}
	
	//-------------------------------------------------------------------
	//  AkkSg = "Akkusativ Singular" Space OptDigit "*"? Space "=" Phrase EOL
	//-------------------------------------------------------------------
	void addAkkSg() {
		addFlexion(Kasus.Akkusativ, Numerus.Singular);
	}
	
	//-------------------------------------------------------------------
	//  AkkPl = "Akkusativ Plural" Space OptDigit "*"? Space "=" Phrase EOL
	//-------------------------------------------------------------------
	void addAkkPl() {
		addFlexion(Kasus.Akkusativ, Numerus.Plural);
	}
	
	private void addFlexion(Kasus kasus, Numerus numerus) {
		String variant = rhs(rhsSize() - 2).text();
		String formNo = rhs(2).text();
		int formIdx = formNo.isEmpty() ? 0 : Integer.valueOf(formNo) - 1;
		List<FlexionForm> flexionForms = ((Substantiv) entryWorkingCopy).getFlexionForms(numerus);
		FlexionForm flexionForm;
		if (formIdx < flexionForms.size()) {
			flexionForm = flexionForms.get(formIdx);
		} else if (numerus == Numerus.Singular) {
			flexionForm = new FlexionForm(((Substantiv) entryWorkingCopy).getGender().getGenders()[0]);
			((Substantiv) entryWorkingCopy).addFlexionForm(Numerus.Singular, flexionForm);
		} else if (numerus == Numerus.Plural) {
			flexionForm = new FlexionForm(Gender.PLURAL);
			((Substantiv) entryWorkingCopy).addFlexionForm(numerus, flexionForm);
		} else {
			throw new IllegalStateException("Singular flexion form does not yet exist for " + entryWorkingCopy.getLemma());
		}
		Optional<Flexion> optExistingFlexion = flexionForm.getFlexions().stream().filter(
				f -> f.getKasus().equals(kasus)).findFirst();
		if (optExistingFlexion.isPresent()) {
			optExistingFlexion.get().addVariant(variant);
		} else {
			Flexion flexion = new Flexion(kasus);
			flexion.addVariant(variant);
			flexionForm.addFlexion(flexion);
		}
	}
	
	//-------------------------------------------------------------------
	//  LangTranslations = LangLvl Lang ":" Space TranslationMeaning* RestOfLine
	//-------------------------------------------------------------------
	void addTranslationsForLanguage() {
		String language = (String) rhs(1).get();
		for (int i = 4; i < rhsSize() - 1; i++) {
			TranslationMeaning meaning = (TranslationMeaning) rhs(i).get();
			if (!meaning.getTranslations().isEmpty())
				wiktionaryEntries.peek().addTranslationMeaning(language, meaning);
		}
	}
	
	//-------------------------------------------------------------------
	//  Lang = LT Word RT
	//-------------------------------------------------------------------
	void Lang() {
		lhs().put(rhs(1).text());
	}
	
	//-------------------------------------------------------------------
	//  TranslationMeaning = ItemNo Translation+
	//-------------------------------------------------------------------
	void TranslationMeaning() {
		TranslationMeaning meaning = new TranslationMeaning((String) rhs(0).get());
		for (int i = 1; i < rhsSize(); i++) {
			Translation translation = (Translation) rhs(i).get();
			if (!translation.getInternalLink().isEmpty())
				meaning.addTranslation(translation);
		}
		lhs().put(meaning);
	}
	
	//-------------------------------------------------------------------
	//  Translation = TranslationDetails (UeTemplate / UetTemplate) Gender?
	//                      0                        1                 2
	//      TranslationDetails (COMMA / SEMICOLON)? Space
	//               3(2)              4(3)
	//-------------------------------------------------------------------
	void Translation() {
		Translation t = (Translation) rhs(1).get();
		if (rhs(2).get() instanceof MultiGender)
			t.setGender((MultiGender) rhs(2).get());
		lhs().put(t);
	}
	
	//-------------------------------------------------------------------
	//  UeTemplate = LT ("Ü" / "Ü?") IT TemplateAttr IT TemplateAttr
	//                0      1        2       3       4       5
	//      (IT TemplateAttr)? (IT TemplateAttr)? RT Space
	//        6        7         8      9        10(6) 11(7)
	//-------------------------------------------------------------------
	void UeTemplate() {
		Translation translation = new Translation();
		translation.setInternalLink(rhs(5).text());
		if (rhsSize() > 6 + 2) // 2 = RT Space
			translation.setLabel(rhs(7).text());
		if (rhsSize() == 12)
			translation.setExternalLink(rhs(9).text());
		lhs().put(translation);
	}
	
	//-------------------------------------------------------------------
	//  UetTemplate = LT ("Üt" / "Üt?") IT TemplateAttr IT TemplateAttr
	//                 0        1        2       3       4       5
	//      IT TemplateAttr (IT TemplateAttr)? (IT TemplateAttr)? RT Space
	//       6        7       8      9          10       11      12(6) 13(7)
	//-------------------------------------------------------------------
	void UetTemplate() {
		Translation translation = new Translation();
		translation.setInternalLink(rhs(5).text());
		translation.setTranscription(rhs(7).text());
		if (rhsSize() > 8 + 2) // 2 = RT Space
			translation.setLabel(rhs(9).text());
		if (rhsSize() == 14)
			translation.setExternalLink(rhs(11).text());
		lhs().put(translation);
	}
	
	//-------------------------------------------------------------------
	//  ItemNo = "[" _++ "]" Space
	//-------------------------------------------------------------------
	void ItemNo() {
		lhs().put(rhsText(1, rhsSize() - 2));
	}
	
	//-------------------------------------------------------------------
	//  Meaning = MeaningLvl RichTextComponent++ EOL
	//-------------------------------------------------------------------
	void Meaning() {
		RichText richText = new RichText();
		processSequenceOfRichTextComponents(1, rhsSize(), richText);
		RichTextEvaluator evaluator = new RichTextEvaluator(templateService, Collections.emptyList());
		evaluator.visit(richText);
		Meaning meaning = new Meaning(evaluator.getResult());
		entryWorkingCopy.addMeaning(meaning);
	}
	
	//=====================================================================
	//  OldSpellingTemplate = LT "..." SEP TextualTParam RestOfLine
	//                        0    1    2         3         n-1
	//=====================================================================
	void OldSpellingTemplate() {
		ReferenceWiktionaryEntry entry = new ReferenceWiktionaryEntry();
		entry.setLemma(lemma);
		entry.setReferenceType(ReferenceWiktionaryEntry.ReferenceType.fromReferenceName(rhs(1).text()));
		entry.setReferenceValue(rhs(3).text());
		wiktionaryEntries.push(entry);
	}
	
	//=====================================================================
	//  AltSpellingTemplate = "*" Space "..." _*+ Link RestOfLine ;
	//                         0    1     2  ...  n-2     n-1
	//=====================================================================
	void AltSpellingTemplate_0() {
		AltSpellingTemplate(rhsSize() - 2);
	}
	
	//-------------------------------------------------------------------
	//  AltSpellingTemplate_1 = LT "..." RT EOL ":" Link
	//                          0    1   2   3   4   5
	//-------------------------------------------------------------------
	void AltSpellingTemplate_1() {
		AltSpellingTemplate(5);
	}
	
	private void AltSpellingTemplate(int linkIndex) {
		ReferenceWiktionaryEntry entry = new ReferenceWiktionaryEntry();
		entry.setLemma(lemma);
		entry.setReferenceType(ReferenceWiktionaryEntry.ReferenceType.ALTERNATIVE_SPELLING);
		InternalLink link = (InternalLink) rhs(linkIndex).get();
		entry.setReferenceValue(link.getPageTitle());
		wiktionaryEntries.push(entry);
	}
	
	//-------------------------------------------------------------------
	//  Template = LT TName (SEP TParam)* RT
	//              0   1     2     3     n-1
	//-------------------------------------------------------------------
	void Template() {
		Template.Builder templateBuilder = new Template.Builder().withName(rhs(1).text());
		for (int i = 3; i < rhsSize() - 1; i += 2) {
			templateBuilder.withParameter((TemplateParameter) rhs(i).get());
		}
		lhs().put(templateBuilder.build());
	}
	
	//-------------------------------------------------------------------
	//  TParam = Number EQ (!(SEP / RT) RichTextComponent)*
	//             0    1                        2
	//-------------------------------------------------------------------
	void TParam_0() {
		RichText richText = new RichText();
		processSequenceOfRichTextComponents(2, rhsSize(), richText);
		lhs().put(new NumberedTemplateParameter(Integer.valueOf(rhs(0).text()), richText));
	}
	
	//-------------------------------------------------------------------
	//  TParam = Name EQ (!(SEP / RT) RichTextComponent)*
	//             0  1                        2
	//-------------------------------------------------------------------
	void TParam_1() {
		RichText richText = new RichText();
		processSequenceOfRichTextComponents(2, rhsSize(), richText);
		lhs().put(new NamedTemplateParameter(rhs(0).text(), richText));
	}
	
	//-------------------------------------------------------------------
	//  TCallParam = (!(SEP / RF) RichTextComponent)*
	//                                     0
	//-------------------------------------------------------------------
	void TParam_2() {
		RichText richText = new RichText();
		processSequenceOfRichTextComponents(0, rhsSize(), richText);
		lhs().put(new AnonymousTemplateParameter(richText));
	}
	
	//-------------------------------------------------------------------
	//  Link = LL LinkAttr (SEP LinkAttr)? RL
	//         0      1      2      3      4(2)
	//-------------------------------------------------------------------
	void Link() {
		InternalLink.Builder linkBuilder = new InternalLink.Builder().withPageTitle(rhs(1).text());
		if (rhsSize() > 3)
			linkBuilder.withLinkText(rhs(3).text());
		lhs().put(linkBuilder.build());
	}
	
	//-------------------------------------------------------------------
	//  CursiveText = "''" RichTextComponent*+ "''"
	//                  0            1          n-1
	//-------------------------------------------------------------------
	void CursiveText() {
		RichText body = new RichText();
		processSequenceOfRichTextComponents(1, rhsSize() - 1, body);
		lhs().put(new CursiveBlock(body));
	}
	
	//-------------------------------------------------------------------
	//  RichTextComponent = Link
	//-------------------------------------------------------------------
	void RichTextComponent_0() {
		lhs().put(rhs(0).get());
	}
	
	//-------------------------------------------------------------------
	//  RichTextComponent = CursiveText
	//-------------------------------------------------------------------
	void RichTextComponent_1() {
		lhs().put(rhs(0).get());
	}
	
	//-------------------------------------------------------------------
	//  RichTextComponent = Template
	//-------------------------------------------------------------------
	void RichTextComponent_2() {
		lhs().put(rhs(0).get());
	}
	
	//-------------------------------------------------------------------
	//  RichTextComponent = !EOL _
	//-------------------------------------------------------------------
	void RichTextComponent_3() {
		lhs().put(new PlainText(rhsText(0, rhsSize())));
	}
	
	private void processSequenceOfRichTextComponents(int startInclusive, int endExclusive, RichText richText) {
		IntStream.range(startInclusive, endExclusive)
		         .mapToObj(i -> rhs(i).get())
		         .filter(Objects::nonNull)
		         .forEach(o -> richText.addComponent((RichTextComponent) o));
	}
	
	private String getFormattedErrorMessageForLogging() {
		return lhs().errMsg().replace("\n", "\\n");
	}
}
