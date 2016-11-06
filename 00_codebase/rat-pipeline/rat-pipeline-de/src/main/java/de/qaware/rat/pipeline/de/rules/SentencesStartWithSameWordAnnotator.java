package de.qaware.rat.pipeline.de.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import de.qaware.rat.api.models.RuleParameter;
import de.qaware.rat.common.CollectionUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.statistics.TextStatistic;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This {@code Java Annotator} class detects if multiple sentences start with
 * the same word.
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" }, outputs = {
                "de.qaware.rat.type.RatReadabilityAnomaly" })
public class SentencesStartWithSameWordAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(SentencesStartWithSameWordAnnotator.class);

    public static final String SENTENCE_THRESHOLD = RuleParameter.THRESHOLD;
    @ConfigurationParameter(name = RuleParameter.THRESHOLD, mandatory = false, defaultValue = "2")
    protected int sentenceThreshold;

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start SentencesStartWithSameWordAnnotator");

        Collection<Sentence> sentences = JCasUtil.select(aJCas, Sentence.class);
        int maxSize = sentences.size();

        try {
            for (int i = 0; i < maxSize; i++) {
                List<Token> words = TextStatistic.getWordsInSentence(aJCas, Iterables.get(sentences, i));

                if (i + sentenceThreshold <= maxSize) {
                    List<String> startingWords = new ArrayList<String>();

                    for (int j = 0; j < sentenceThreshold; j++) {
                        startingWords.add(TextStatistic.getWordsInSentence(aJCas, Iterables.get(sentences, j + i))
                                .get(0).getCoveredText().toLowerCase());
                    }

                    if (CollectionUtils.allEqual(startingWords)) {
                        int violationsCount = startingWords.size();

                        if (violationsCount == 2) {
                            UimaUtils.createRatReadabilityAnomaly(aJCas, "SentenceWithSameWords", "ReadabilityAnomaly",
                                    severity,
                                    "Der nachfolgende Satz beginnt mit dem selben Wort: '"
                                            + words.get(0).getCoveredText() + "'.",
                                    violationsCount, words.get(0).getBegin(), words.get(0).getEnd());
                        } else {
                            UimaUtils.createRatReadabilityAnomaly(aJCas, "SentenceWithSameWords", "ReadabilityAnomaly",
                                    severity,
                                    "Die " + startingWords.size()
                                            + " nachfolgenden Sätze beginnen mit dem selben Wort: '"
                                            + words.get(0).getCoveredText() + "'.",
                                    violationsCount, words.get(0).getBegin(), words.get(0).getEnd());
                        }
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.info("SentencesStartWithSameWordAnnotator failed");
        }
    }
}