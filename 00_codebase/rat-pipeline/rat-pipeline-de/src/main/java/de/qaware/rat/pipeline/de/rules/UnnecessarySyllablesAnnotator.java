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
 * This {@code Java Annotator} class detects if words are used that have
 * unncessary syllables.
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" }, outputs = {
        "de.qaware.rat.type.RatReadabilityAnomaly" })
public class UnnecessarySyllablesAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnnecessarySyllablesAnnotator.class);

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start UnnecessarySyllablesAnnotator");

        try {
            String[] unnecessarySyllableWords = ImporterUtils.readWordlist("word-lists/UnnecessarySyllableWords.txt");
            Collection<Token> words = TextStatistic.getWordsInDocument(aJCas);

            for (Token word : words) {
                if (Arrays.asList(unnecessarySyllableWords).contains(word.getCoveredText().toLowerCase())) {
                    UimaUtils.createRatReadabilityAnomaly(aJCas, "UnnecessarySyllableWords", "ReadabilityAnomaly",
                            severity, "Vermeiden Sie Wörter mit unnötigen Silben.", 1, word.getBegin(), word.getEnd());
                }
            }
        } catch (IOException e) {
            LOGGER.error("The UnnecessarySyllablesAnnotator failed.");
        }
    }
}