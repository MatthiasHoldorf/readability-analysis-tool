package de.qaware.rat.pipeline.de.rules;

import java.io.IOException;
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

import de.qaware.rat.api.models.RuleParameter;
import de.qaware.rat.common.CollectionUtils;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.statistics.TextStatistic;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This {@code Java Annotator} class detects if a sentence has too many
 * abstractive substantives in a sentence.
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
        "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS" }, outputs = {
                "de.qaware.rat.type.RatReadabilityAnomaly" })
public class NominalStyleAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(NominalStyleAnnotator.class);

    public static final String ABSTRACT_SUBSTANTIVES_IN_SENTENCE = RuleParameter.THRESHOLD;
    @ConfigurationParameter(name = RuleParameter.THRESHOLD, mandatory = false, defaultValue = "3")
    protected int abstractSubstantivesInSentence;

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start NominalStyleAnnotator");

        try {
            String[] nominalStyleEndings = ImporterUtils.readWordlist("word-lists/NominalStyle.txt");
            Collection<Sentence> sentences = JCasUtil.select(aJCas, Sentence.class);

            for (Sentence sentence : sentences) {
                List<Token> words = TextStatistic.getWordsInSentence(aJCas, sentence);
                List<Token> nouns = TextStatistic.getTokensByPosType(words, "NN");
                List<Token> matches = new ArrayList<Token>();

                for (Token noun : nouns) {
                    for (String ending : nominalStyleEndings) {
                        if (noun.getCoveredText().endsWith(ending)) {
                            matches.add(noun);
                        }
                    }
                }

                if (matches.size() >= abstractSubstantivesInSentence) {
                    List<String> violations = CollectionUtils.transformToStringList(matches);

                    UimaUtils.createRatReadabilityAnomaly(aJCas, "NominalStyle", "ReadabilityAnomaly", severity,
                            "Dieser Satz enthält " + abstractSubstantivesInSentence + " oder mehr (" + matches.size()
                                    + ") abstrakte Substantive die auf -heit, -keit oder -ung enden. ("
                                    + CollectionUtils.printStringList(violations) + ")",
                            violations, matches.get(0).getBegin(), matches.get(0).getEnd());
                }
            }
        } catch (IOException e) {
            LOGGER.error("The NominalStyleAnnotator failed.");
        }
    }
}