package de.qaware.rat.pipeline.de.rules;

import java.io.IOException;
import java.util.ArrayList;
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
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.statistics.TextStatistic;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This {@code Java Annotator} class detects if there are too many fillers in a
 * sentence.
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" }, outputs = {
                "de.qaware.rat.type.RatReadabilityAnomaly" })
public class FillerSentenceAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(FillerSentenceAnnotator.class);

    public static final String FILLERS_IN_SENTENCE = RuleParameter.THRESHOLD;
    @ConfigurationParameter(name = RuleParameter.THRESHOLD, mandatory = true, defaultValue = "3")
    protected int fillersInSentence;

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start FillerSentenceAnnotator");

        try {
            String[] fillers = ImporterUtils.readWordlist("word-lists/Fillers.txt");
            Collection<Sentence> sentences = JCasUtil.select(aJCas, Sentence.class);

            for (Sentence sentence : sentences) {
                List<Token> words = TextStatistic.getWordsInSentence(aJCas, sentence);
                List<Token> matches = new ArrayList<Token>();

                for (Token word : words) {
                    if (Arrays.asList(fillers).contains(word.getCoveredText().toLowerCase())) {
                        matches.add(word);
                    }
                }

                if (matches.size() >= fillersInSentence) {
                    List<String> violations = CollectionUtils.transformToStringList(matches);

                    UimaUtils.createRatReadabilityAnomaly(aJCas, "FillersInSentence", "ReadabilityAnomaly", severity,
                            "Vermeiden sie " + fillersInSentence + " oder mehr (" + matches.size()
                                    + ") Füllwörter in einem Satz. (" + CollectionUtils.printStringList(violations)
                                    + ")",
                            violations, matches.get(0).getBegin(), matches.get(0).getEnd());
                }
            }
        } catch (IOException e) {
            LOGGER.error("The FillerSentenceAnnotator failed.");
        }
    }
}