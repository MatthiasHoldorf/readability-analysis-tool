package de.qaware.rat.pipeline.de.rules;

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
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.statistics.TextStatistic;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This {@code Java Annotator} class detects if a sentence is too long (by word
 * count).
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" }, outputs = {
                "de.qaware.rat.type.RatReadabilityAnomaly" })
public class LongSentenceAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(LongSentenceAnnotator.class);

    public static final String WORDS_IN_SENTENCE_CRITICAL = RuleParameter.THRESHOLD;
    @ConfigurationParameter(name = WORDS_IN_SENTENCE_CRITICAL, mandatory = true, defaultValue = "35")
    protected int wordsInSentenceCritical;

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start LongSentenceAnnotator");

        Collection<Sentence> sentences = JCasUtil.select(aJCas, Sentence.class);

        for (Sentence sentence : sentences) {
            List<Token> words = TextStatistic.getWordsInSentence(aJCas, sentence);

            if (words.size() > wordsInSentenceCritical) {
                UimaUtils.createRatReadabilityAnomaly(aJCas, "LongSentence",
                        "ReadabilityAnomaly", "Critical", "Dieser Satz besteht aus " + wordsInSentenceCritical
                                + " oder mehr (" + words.size() + ") Wörtern.",
                        words.size(), words.get(0).getBegin(), words.get(0).getEnd());
            }
        }
    }
}