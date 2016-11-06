package de.qaware.rat.pipeline.de.rules;

import java.io.IOException;
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
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.statistics.TextStatistic;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This {@code Java Annotator} class detects if a sentence is written in passive
 * voice.
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" }, outputs = {
                "de.qaware.rat.type.RatReadabilityAnomaly" })
public class PassiveVoiceAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(PassiveVoiceAnnotator.class);

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start PassiveVoiceAnnotator");

        try {
            String[] passiveVoiceIndicator = ImporterUtils.readWordlist("word-lists/PassiveVoice.txt");
            Collection<Sentence> sentences = JCasUtil.select(aJCas, Sentence.class);

            for (Sentence sentence : sentences) {
                List<Token> words = TextStatistic.getWordsInSentence(aJCas, sentence);
                boolean isPassiveSentence = false;

                for (Token word : words) {
                    if (!isPassiveSentence
                            && Arrays.asList(passiveVoiceIndicator).contains(word.getCoveredText().toLowerCase())) {
                        UimaUtils.createRatReadabilityAnomaly(aJCas, "PassiveVoice", "ReadabilityAnomaly", severity,
                                "Vermeiden Sie Sätze im Passiv.", 1, word.getBegin(), word.getEnd());

                        isPassiveSentence = true;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("The PassiveVoiceAnnotator failed.");
        }
    }
}