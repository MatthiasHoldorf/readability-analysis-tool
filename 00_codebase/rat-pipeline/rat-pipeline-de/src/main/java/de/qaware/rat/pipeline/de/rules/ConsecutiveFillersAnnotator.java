package de.qaware.rat.pipeline.de.rules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.models.RuleParameter;
import de.qaware.rat.common.CollectionUtils;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.statistics.TextStatistic;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * This {@code Java Annotator} class detects if there are consecutive fillers in
 * the text.
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" }, outputs = {
        "de.qaware.rat.type.RatReadabilityAnomaly" })
public class ConsecutiveFillersAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsecutiveFillersAnnotator.class);

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start ConsecutiveFillersAnnotator");

        try {
            String[] fillers = ImporterUtils.readWordlist("word-lists/Fillers.txt");
            List<Token> words = TextStatistic.getWordsInDocument(aJCas);

            int maxSize = words.size() - 1;
            for (int i = 0; i < words.size(); i++) {

                if (i + 1 <= maxSize) {
                    if (Arrays.asList(fillers).contains(words.get(i).getCoveredText().toLowerCase())
                            && Arrays.asList(fillers).contains(words.get(i + 1).getCoveredText().toLowerCase())) {
                        List<String> violations = new ArrayList<String>();
                        violations.add(words.get(i).getCoveredText());
                        violations.add(words.get(i + 1).getCoveredText());

                        UimaUtils.createRatReadabilityAnomaly(aJCas, "ConsecutiveFillers", "ReadabilityAnomaly",
                                severity,
                                "Vermeiden Sie aufeinanderfolgende Füllwörter. ("
                                        + CollectionUtils.printStringList(violations) + ")",
                                violations, words.get(i).getBegin(), words.get(i).getEnd());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("The ConsecutiveFillersAnnotator failed.");
        }
    }
}