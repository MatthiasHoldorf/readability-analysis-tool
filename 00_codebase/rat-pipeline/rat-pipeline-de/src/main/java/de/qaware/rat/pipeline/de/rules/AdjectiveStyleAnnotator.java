package de.qaware.rat.pipeline.de.rules;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.models.RuleParameter;
import de.qaware.rat.common.CollectionUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.statistics.TextStatistic;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This {@code Java Annotator} class detects if there are too many adjectives in
 * a sentence.
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" }, outputs = {
                "de.qaware.rat.type.RatReadabilityAnomaly" })
public class AdjectiveStyleAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdjectiveStyleAnnotator.class);

    public static final String ADJECTIVES_IN_SENTENCE = RuleParameter.THRESHOLD;
    @ConfigurationParameter(name = ADJECTIVES_IN_SENTENCE, mandatory = true, defaultValue = "5")
    protected int adjectivesInSentence;

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start AdjectiveStyleAnnotator");

        Collection<Sentence> sentences = JCasUtil.select(aJCas, Sentence.class);

        for (Sentence sentence : sentences) {
            List<Token> matches = TextStatistic.getMatchesInSentenceByPosValue(aJCas, sentence,
                    Arrays.asList("ADJA", "ADJD"));

            if (matches.size() >= adjectivesInSentence) {
                List<String> violations = CollectionUtils.transformToStringList(matches);

                UimaUtils.createRatReadabilityAnomaly(aJCas, "AdjectiveStyle", "ReadabilityAnomaly", severity,
                        "Dieser Satz enthält " + adjectivesInSentence + " oder mehr (" + matches.size()
                                + ") Adjektive: " + CollectionUtils.printStringList(violations),
                        violations, matches.get(0).getBegin(), matches.get(0).getEnd());
            }
        }
    }
}