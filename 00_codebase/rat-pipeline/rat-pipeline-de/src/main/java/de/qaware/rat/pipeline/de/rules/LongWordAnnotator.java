package de.qaware.rat.pipeline.de.rules;

import java.util.Collection;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.models.RuleParameter;
import de.qaware.rat.statistics.RatSyllablesCounter;
import de.qaware.rat.statistics.TextStatistic;
import de.qaware.rat.type.RatReadabilityAnomaly;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This {@code Java Annotator} class detects if words are too long (by syllables
 * count).
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" }, outputs = {
        "de.qaware.rat.type.RatReadabilityAnomaly" })
public class LongWordAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(LongWordAnnotator.class);

    public static final String SYLLABLES_IN_WORD = RuleParameter.THRESHOLD;
    @ConfigurationParameter(name = RuleParameter.THRESHOLD, mandatory = false, defaultValue = "7")
    protected int syllablesInWord;

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start LongWordAnnotator");

        Collection<Token> words = TextStatistic.getWordsInDocument(aJCas);

        for (Token word : words) {
            if (!word.getCoveredText().contains("/")) {
                int syllables = new RatSyllablesCounter("de").countSyllables(word.getCoveredText());

                if (syllables >= syllablesInWord) {
                    RatReadabilityAnomaly annotation = new RatReadabilityAnomaly(aJCas);
                    annotation.setAnomalyName("LongWord");
                    annotation.setCategory("ReadabilityAnomaly");
                    annotation.setSeverity(severity);
                    annotation.setExplanation(
                            "Vermeiden Sie Wörter mit " + syllablesInWord + " oder mehr Silben (" + syllables + ").");
                    annotation.setNumberOfViolations(syllables);
                    annotation.setBegin(word.getBegin());
                    annotation.setEnd(word.getEnd());

                    annotation.addToIndexes();
                }
            }
        }
    }
}