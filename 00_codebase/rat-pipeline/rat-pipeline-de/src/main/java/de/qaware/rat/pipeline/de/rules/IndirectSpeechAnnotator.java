package de.qaware.rat.pipeline.de.rules;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.models.RuleParameter;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.statistics.TextStatistic;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This {@code Java Annotator} class detects if there is indirect speech used in
 * the text.
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" }, outputs = {
        "de.qaware.rat.type.RatReadabilityAnomaly" })
public class IndirectSpeechAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndirectSpeechAnnotator.class);

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start IndirectSpeechAnnotator");

        try {
            String[] indirectSpeechInidcator = ImporterUtils.readWordlist("word-lists/IndirectSpeech.txt");
            Collection<Token> words = TextStatistic.getWordsInDocument(aJCas);

            for (Token word : words) {
                if (Arrays.asList(indirectSpeechInidcator).contains(word.getCoveredText().toLowerCase())) {
                    UimaUtils.createRatReadabilityAnomaly(aJCas, "IndirectSpeech", "ReadabilityAnomaly", severity,
                            "Vermeiden Sie die indirekte Anrede.", 1, word.getBegin(), word.getEnd());
                }
            }
        } catch (IOException e) {
            LOGGER.error("The IndirectSpeechAnnotator failed.");
        }
    }
}