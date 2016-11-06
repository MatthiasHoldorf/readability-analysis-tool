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
 * This {@code Java Annotator} class detects if there are too many leading
 * attributes between an article and a noun.
 * 
 * @author Matthias
 *
 */
@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
        "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS" }, outputs = {
                "de.qaware.rat.type.RatReadabilityAnomaly" })
public class LeadingAttributesAnnotator extends JCasAnnotator_ImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeadingAttributesAnnotator.class);

    public static final String LEADING_ATTRIBUTES = RuleParameter.THRESHOLD;
    @ConfigurationParameter(name = RuleParameter.THRESHOLD, mandatory = true, defaultValue = "4")
    protected int leadingAttributes;

    public static final String SEVERITY = RuleParameter.SEVERITY;
    @ConfigurationParameter(name = RuleParameter.SEVERITY, mandatory = true, defaultValue = "Minor")
    protected String severity;

    @Override
    public void process(JCas aJCas) {
        LOGGER.info("Start LeadingAttributesAnnotator");

        Collection<Sentence> sentences = JCasUtil.select(aJCas, Sentence.class);

        for (Sentence sentence : sentences) {
            List<Token> words = TextStatistic.getWordsInSentence(aJCas, sentence);

            for (int i = 0; i < words.size(); i++) {
                int j = 0;
                int count = 0;
                Token nounMatch = null;

                if (words.get(i).getPos().getType().getShortName().equals("ART") && (i + 1) < words.size()) {
                    Token articel = words.get(i);
                    j = i + 1;

                    while (!words.get(j).getPos().getType().getShortName().equals("NN")
                            && !words.get(j).getPos().getType().getShortName().equals("ART")
                            && (j + 1) < words.size()) {
                        count++;
                        j++;
                    }

                    if (words.get(j).getPos().getType().getShortName().equals("NN") && count >= leadingAttributes) {
                        nounMatch = words.get(j);

                        UimaUtils.createRatReadabilityAnomaly(aJCas, "LeadingAttributes", "ReadabilityAnomaly",
                                severity,
                                "Zwischen dem Artikel '" + articel.getCoveredText() + "' und dem Nomen '"
                                        + nounMatch.getCoveredText() + "' stehen mehr als " + (leadingAttributes - 1)
                                        + " (" + count + ") vorangestellte Attribute.",
                                count, articel.getBegin(), articel.getEnd());
                    }
                }
            }
        }
    }
}