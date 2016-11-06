package de.qaware.rat.pipeline.de.rules;

import static java.lang.String.format;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.qaware.rat.api.models.AnnotatorRuleModel;

/**
 * The {@code Rule} ENUM is used to configure and instantiate the
 * {@code Java Annotator} classes for the analysis.
 * 
 * @author Matthias
 *
 */
public enum Rule {
    ADJECTIVE_STYLE("AdjectiveStyle",
            (anomalyRule) -> createEngineDescription(AdjectiveStyleAnnotator.class,
                    AdjectiveStyleAnnotator.ADJECTIVES_IN_SENTENCE, anomalyRule.getThreshold(),
                    AdjectiveStyleAnnotator.SEVERITY, anomalyRule.getSeverity())),

    AMBIGUUOS_ADJECTIVES_AND_ADVERBS("AmbiguousAdjectivesAndAdverbs",
            (anomalyRule) -> createEngineDescription(AmbiguousAdjectivesAndAdverbsAnnotator.class,
                    AmbiguousAdjectivesAndAdverbsAnnotator.SEVERITY, anomalyRule.getSeverity())),

    CONSECUTIVE_FILLERS("ConsecutiveFillers",
            (anomalyRule) -> createEngineDescription(ConsecutiveFillersAnnotator.class,
                    ConsecutiveFillersAnnotator.SEVERITY, anomalyRule.getSeverity())),

    CONSECUTIVE_PREPOSITIONS("ConsecutivePrepositions",
            (anomalyRule) -> createEngineDescription(ConsecutivePrepositionsAnnotator.class,
                    ConsecutivePrepositionsAnnotator.SEVERITY, anomalyRule.getSeverity())),

    DOUBLE_NEGATIVE("DoubleNegative",
            (anomalyRule) -> createEngineDescription(DoubleNegativeAnnotator.class,
                    DoubleNegativeAnnotator.NEGATIONS_IN_SENTENCE, anomalyRule.getThreshold(),
                    DoubleNegativeAnnotator.SEVERITY, anomalyRule.getSeverity())),

    FILLER("Filler", (anomalyRule) -> createEngineDescription(FillerAnnotator.class, FillerAnnotator.SEVERITY,
            anomalyRule.getSeverity())),

    FILLER_IN_SENTENCE("FillerSentence",
            (anomalyRule) -> createEngineDescription(FillerSentenceAnnotator.class,
                    FillerSentenceAnnotator.FILLERS_IN_SENTENCE, anomalyRule.getThreshold(),
                    FillerSentenceAnnotator.SEVERITY, anomalyRule.getSeverity())),

    INDIRECT_SPEECH("IndirectSpeech", (anomalyRule) -> createEngineDescription(IndirectSpeechAnnotator.class,
            IndirectSpeechAnnotator.SEVERITY, anomalyRule.getSeverity())),

    LEADING_ATTRIBUTES("LeadingAttributes",
            (anomalyRule) -> createEngineDescription(LeadingAttributesAnnotator.class,
                    LeadingAttributesAnnotator.LEADING_ATTRIBUTES, anomalyRule.getThreshold(),
                    LeadingAttributesAnnotator.SEVERITY, anomalyRule.getSeverity())),

    LONG_SENTENCES("LongSentence",
            (anomalyRule) -> createEngineDescription(LongSentenceAnnotator.class,
                    LongSentenceAnnotator.WORDS_IN_SENTENCE_CRITICAL, anomalyRule.getThreshold(),
                    LongSentenceAnnotator.SEVERITY, anomalyRule.getSeverity())),

    LONG_WORD("LongWord",
            (anomalyRule) -> createEngineDescription(LongWordAnnotator.class, LongWordAnnotator.SYLLABLES_IN_WORD,
                    anomalyRule.getThreshold(), LongWordAnnotator.SEVERITY, anomalyRule.getSeverity())),

    MODAL_VERB("ModalVerb", (anomalyRule) -> createEngineDescription(ModalVerbAnnotator.class,
            ModalVerbAnnotator.SEVERITY, anomalyRule.getSeverity())),

    MODAL_VERB_SENTENCE("ModalVerbSentence",
            (anomalyRule) -> createEngineDescription(ModalVerbSentenceAnnotator.class,
                    ModalVerbSentenceAnnotator.MODAL_VERB_IN_SENTENCE, anomalyRule.getThreshold(),
                    ModalVerbSentenceAnnotator.SEVERITY, anomalyRule.getSeverity())),

    NESTED_SENTENCE("NestedSentence",
            (anomalyRule) -> createEngineDescription(NestedSentenceAnnotator.class,
                    NestedSentenceAnnotator.COMPOUNDS_IN_SENTENCE, anomalyRule.getThreshold(),
                    NestedSentenceAnnotator.SEVERITY, anomalyRule.getSeverity())),

    NESTED_SENTENCE_CONJUNCTION("NestedSentenceConjunction",
            (anomalyRule) -> createEngineDescription(NestedSentenceDelimiterAnnotator.class,
                    NestedSentenceDelimiterAnnotator.DELIMITERS_IN_SENTENCE, anomalyRule.getThreshold(),
                    NestedSentenceDelimiterAnnotator.SEVERITY, anomalyRule.getSeverity())),

    NESTED_SENTENCE_DELIMITER("NestedSentenceDelimiter",
            (anomalyRule) -> createEngineDescription(NestedSentenceConjunctionAnnotator.class,
                    NestedSentenceConjunctionAnnotator.CONJUNCTIONS_IN_SENTENCE, anomalyRule.getThreshold(),
                    NestedSentenceConjunctionAnnotator.SEVERITY, anomalyRule.getSeverity())),

    NOMINAL_STYLE("NominalStyle",
            (anomalyRule) -> createEngineDescription(NominalStyleAnnotator.class,
                    NominalStyleAnnotator.ABSTRACT_SUBSTANTIVES_IN_SENTENCE, anomalyRule.getThreshold(),
                    NominalStyleAnnotator.SEVERITY, anomalyRule.getSeverity())),

    PASSIVE_VOICE("PassiveVoice", (anomalyRule) -> createEngineDescription(PassiveVoiceAnnotator.class,
            PassiveVoiceAnnotator.SEVERITY, anomalyRule.getSeverity())),

    SENTENCES_START_WITH_SAME_WORD("SentencesStartWithSameWord",
            (anomalyRule) -> createEngineDescription(SentencesStartWithSameWordAnnotator.class,
                    SentencesStartWithSameWordAnnotator.SENTENCE_THRESHOLD, anomalyRule.getThreshold(),
                    SentencesStartWithSameWordAnnotator.SEVERITY, anomalyRule.getSeverity())),

    SUBJECTIVE_LANGUAGE("SubjectiveLanguage",
            (anomalyRule) -> createEngineDescription(SubjectiveLanguageAnnotator.class,
                    SubjectiveLanguageAnnotator.SEVERITY, anomalyRule.getSeverity())),


    SUPERLATIVE("Superlative",
            (anomalyRule) -> createEngineDescription(SuperlativeAnnotator.class,
                    SubjectiveLanguageAnnotator.SEVERITY, anomalyRule.getSeverity())),

    
    UNNECESSARY_SYLLABLES("UnnecessarySyllables",
            (anomalyRule) -> createEngineDescription(UnnecessarySyllablesAnnotator.class,
                    UnnecessarySyllablesAnnotator.SEVERITY, anomalyRule.getSeverity()));

    private static final Map<String, Rule> BY_NAME = createByName();

    private final String name;
    private final EngineDescriptionFactory engineDescriptionFactory;

    /**
     * Set the properties of a Rule ENUM field.
     * 
     * @param name
     *            the name of the {@code Java Annotator}.
     * @param engineDescriptionFactory
     *            the function to create the {@code AnalysisEngineDescription}
     *            for the UIMA pipeline.
     */
    Rule(String name, EngineDescriptionFactory engineDescriptionFactory) {
        this.name = name;
        this.engineDescriptionFactory = engineDescriptionFactory;
    }

    /**
     * Create an {@code AnalysisEngineDescription} for the UIMA pipeline based
     * on an {@code AnnotatorRuleModel}.
     * 
     * @param anomalyRule
     *            an {@code AnnotatorRuleModel} as abstraction of the
     *            configuration of the {@code Java Annotator}.
     * @return the {@code AnalysisEngineDescription} of the
     *         {@code JavaAnnotator}.
     * @throws ResourceInitializationException
     *             if the {@code JavaAnnotator} cannot be created.
     */
    public static AnalysisEngineDescription create(AnnotatorRuleModel anomalyRule)
            throws ResourceInitializationException {
        Rule rule = BY_NAME.get(anomalyRule.getName());

        if (rule == null) {
            throw new IllegalArgumentException(format("Unknown rule %s.", anomalyRule.getName()));
        }

        return rule.engineDescriptionFactory.createEngineDescription(anomalyRule);
    }

    private static Map<String, Rule> createByName() {
        Map<String, Rule> result = new HashMap<>(values().length);

        for (Rule rule : values()) {
            result.put(rule.name, rule);
        }

        return result;
    }
}
