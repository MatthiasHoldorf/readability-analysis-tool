package de.qaware.rat.common;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.models.AnnotationModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.type.RatAnomaly;
import de.qaware.rat.type.RatReadabilityAnomaly;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.textcat.LanguageIdentifier;

/**
 * The UimaUtils class provides utility functions to work with the Apache
 * UIMA-Architecture.
 * 
 * @author Matthias
 *
 */
public final class UimaUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(UimaUtils.class);

    private static final String DEFAULT_LANGUAGE = Locale.GERMAN.toLanguageTag();

    private UimaUtils() {
    }

    /**
     * This function returns an {@code AnnotationModel} by a given type from the
     * annotation result which is stored in the jCas object.
     * 
     * <p>
     * It further abstracts the UIMA annotations into the annotatioModel.
     * 
     * @param jCas
     *            the annotation results of the analysed document.
     * @param type
     *            the type system to search for and return annotations from.
     * @return a list of an abstracted annotationModel containing the results of
     *         the analysis.
     */
    public static List<AnnotationModel> getAnnotationModelsByType(JCas jCas, String type) {
        List<TOP> featureStructures = new ArrayList<TOP>(JCasUtil.selectAll(jCas));
        List<AnnotationModel> annotationModels = new ArrayList<AnnotationModel>();
        Annotation annotation = null;

        for (TOP feature : featureStructures) {
            if (feature.getType().getName().equals(type)) {
                annotation = (Annotation) feature;
                AnnotationModel annotationModel = new AnnotationModel(annotation.getBegin(), annotation.getEnd(),
                        annotation.getCoveredText(), null);

                LOGGER.debug(annotationModel.toString());
                annotationModels.add(annotationModel);
            }
        }

        return annotationModels;
    }

    /**
     * This function returns annotations by a given type from the annotation
     * result which is stored in the jCas object.
     * 
     * <p>
     * It further abstracts the UIMA annotations into the annotatioModel.
     * 
     * @param jCas
     *            the annotation results of the analysed document.
     * @param type
     *            the type system to search for and return annotations from.
     * @return a list of an abstracted annotationModel containing the results of
     *         the analysis.
     */
    public static List<Annotation> getAnnotationsByType(JCas jCas, String type) {
        List<TOP> featureStructures = new ArrayList<TOP>(JCasUtil.selectAll(jCas));
        List<Annotation> annotations = new ArrayList<Annotation>();
        Annotation annotation = null;

        for (TOP feature : featureStructures) {
            if (feature.getType().getName().equals(type)) {
                annotation = (Annotation) feature;
                annotations.add(annotation);
            }
        }

        return annotations;
    }

    /**
     * This function detects the language of a given text.
     * 
     * @param text
     *            the text to detect the language from.
     * @return the language as String
     * @throws ResourceInitializationException
     * @throws AnalysisEngineProcessException
     */
    public static String detectLanguage(String text) {
        try {
            AnalysisEngineDescription analysisEngineDescription = createEngineDescription(LanguageIdentifier.class);
            AnalysisEngine analysisEngine = UIMAFramework.produceAnalysisEngine(analysisEngineDescription);
            JCas jCas = analysisEngine.newJCas();
            jCas.setDocumentText(text);
            analysisEngine.process(jCas);
            DocumentAnnotation annotation = (DocumentAnnotation) getAnnotationsByType(jCas,
                    "uima.tcas.DocumentAnnotation").get(0);
            return annotation.getLanguage();
        } catch (ResourceInitializationException | AnalysisEngineProcessException e) {
            LOGGER.error("The language detection failed, taking default value: " + DEFAULT_LANGUAGE);
            return DEFAULT_LANGUAGE;
        }
    }

    /**
     * Print the results of the analysis stored in the given jCas object.
     * 
     * @param jCas
     *            the annotation results of the analysed document.
     */
    public static void printJCas(JCas jCas) {
        List<TOP> featureStructures = new ArrayList<TOP>(JCasUtil.selectAll(jCas));
        Annotation annotation = null;

        for (TOP feature : featureStructures) {
            annotation = (Annotation) feature;

            LOGGER.info("Text " + annotation.getCoveredText());
            LOGGER.info("Type " + annotation.getType());
            LOGGER.info(annotation.toString());
        }
    }

    /**
     * This method gets a list of {@code RatAnomaly} from a given jCas.
     * 
     * <p>
     * The method further enriches the {@code RatAnomaly} object by the sentence
     * it is marked in the jCas. Additionally, a hashCode of the anomaly is
     * computed taken into account the name, the covered text and the sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the list of {@code RatAnomaly}.
     */
    public static List<RatAnomaly> getRatAnomalies(JCas jCas) {
        return setSentenceFeatureToRatAnomaly(jCas, JCasUtil.select(jCas, RatAnomaly.class));
    }

    private static List<RatAnomaly> setSentenceFeatureToRatAnomaly(JCas jCas, Collection<RatAnomaly> anomalies) {
        List<RatAnomaly> enrichedAnomalies = new ArrayList<RatAnomaly>();
        Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
        Iterator<Sentence> iter = sentences.iterator();

        while (iter.hasNext()) {
            Sentence value = iter.next();
            for (RatAnomaly annotation : anomalies) {
                if (value.getBegin() <= annotation.getBegin() && value.getEnd() >= annotation.getEnd()) {
                    annotation.setSentence(value.getCoveredText());
                    annotation.setHashCode(
                            (value.getCoveredText() + annotation.getCoveredText() + annotation.getAnomalyName())
                                    .hashCode());
                    enrichedAnomalies.add(annotation);
                }
            }
        }

        return enrichedAnomalies;
    }

    /**
     * Gets an {@code Integer} list of hash codes from a given
     * {@code RatAnomaly} collection.
     * 
     * @param anomalies
     *            the collection to retrieve the hash codes from.
     * @return an {@code Integer} list of hash codes from the anomalies.
     */
    public static List<Integer> getHashCodesFromAnomalies(Collection<RatAnomaly> anomalies) {
        List<Integer> hashCodes = new ArrayList<Integer>();

        for (RatAnomaly anomaly : anomalies) {
            hashCodes.add(anomaly.getHashCode());
        }

        return hashCodes;
    }

    /**
     * Converts a {@code RatAnomaly} to a {@code RatAnomalyModel}.
     * 
     * @param anomaly
     *            the {@code RatAnomaly} to convert.
     * @return the converted {@code RatAnomalyModel}.
     */
    public static RatAnomalyModel getRatAnomalyModelFromRatAnomaly(RatAnomaly anomaly) {
        RatAnomalyModel ratAnomalyModel = new RatAnomalyModel();

        ratAnomalyModel.setAnomalyName(anomaly.getAnomalyName());
        ratAnomalyModel.setSeverity(anomaly.getSeverity());
        ratAnomalyModel.setCategory(anomaly.getCategory());
        ratAnomalyModel.setExplanation(anomaly.getExplanation());
        ratAnomalyModel.setSentence(anomaly.getSentence());
        ratAnomalyModel.setCoveredText(anomaly.getCoveredText());
        ratAnomalyModel.setBegin(anomaly.getBegin());
        ratAnomalyModel.setEnd(anomaly.getEnd());
        ratAnomalyModel.setHashCode(anomaly.getHashCode());

        if (anomaly.getViolations() != null) {
            ratAnomalyModel.setViolations(CollectionUtils.transformToStringList(anomaly.getViolations()));
        }

        return ratAnomalyModel;
    }

    /**
     * This function counts the number of anomalies detected in a given list of
     * anomaly.
     * 
     * @param appliedAnomalies
     *            the anomalies to count the different anomaly from.
     * @return a map containing the anomaly name as key and the number of
     *         occurrences as value.
     */
    public static Map<String, Integer> getNumberOfAnomalyFindings(List<RatAnomaly> appliedAnomalies) {
        Map<String, Integer> findings = new HashMap<String, Integer>();

        for (RatAnomaly anomaly : appliedAnomalies) {
            Integer count = findings.get(anomaly.getAnomalyName());
            if (count != null) {
                count++;
                findings.put(anomaly.getAnomalyName(), count);
            } else {
                findings.put(anomaly.getAnomalyName(), 1);
            }
        }

        return findings;
    }

    /**
     * Converts a list of {@code RatAnomaly} to a list of
     * {@code RatAnomalyModel}.
     * 
     * @param anomalies
     *            the list of {@code RatAnomaly} to convert.
     * @return the converted list of {@code RatAnomalyModel}.
     */
    public static List<RatAnomalyModel> getRatAnomalyModelsFromAnomalies(Collection<RatAnomaly> anomalies) {
        List<RatAnomalyModel> ratAnomalyModels = new ArrayList<RatAnomalyModel>();

        for (RatAnomaly anomaly : anomalies) {
            ratAnomalyModels.add(getRatAnomalyModelFromRatAnomaly(anomaly));
        }

        return ratAnomalyModels;
    }

    /**
     * Creates an annotation object of the abstraction of the UIMA type
     * {@code de.qaware.rat.type.RatAnomaly}.
     * 
     * <p>
     * This function is used to apply the annotations from
     * {@code Java Annotators} to the document.
     * 
     * @param jCas
     *            the jCas object to create the annotation for.
     * @param name
     *            the name of the anomaly.
     * @param category
     *            the category of the anomaly, e.g. ReadabilityAnomaly or
     *            GrammarAnomaly.
     * @param severity
     *            the severity of the broken rule.
     * @param explanation
     *            the explanatory text of the anomaly.
     * @param numberOfViolations
     *            the number of violations occurred.
     * @param begin
     *            the offset of the beginning of the coveredText in the document
     *            text.
     * @param end
     *            the offset of the ending of the coveredText in the document
     *            text.
     */
    public static void createRatReadabilityAnomaly(JCas jCas, String name, String category, String severity,
            String explanation, int numberOfViolations, int begin, int end) {
        RatReadabilityAnomaly ratReadabilityAnomaly = new RatReadabilityAnomaly(jCas);

        ratReadabilityAnomaly.setAnomalyName(name);
        ratReadabilityAnomaly.setCategory(category);
        ratReadabilityAnomaly.setSeverity(severity);
        ratReadabilityAnomaly.setExplanation(explanation);
        ratReadabilityAnomaly.setNumberOfViolations(numberOfViolations);
        ratReadabilityAnomaly.setBegin(begin);
        ratReadabilityAnomaly.setEnd(end);

        ratReadabilityAnomaly.addToIndexes();
    }

    /**
     * Creates an annotation object of the abstraction of the UIMA type
     * {@code de.qaware.rat.type.RatAnomaly}.
     * 
     * <p>
     * This function is used to apply the annotations from
     * {@code Java Annotators} to the document.
     * 
     * @param jCas
     *            the jCas object to create the annotation for.
     * @param name
     *            the name of the anomaly.
     * @param category
     *            the category of the anomaly, e.g. ReadabilityAnomaly or
     *            GrammarAnomaly.
     * @param severity
     *            the severity of the broken rule.
     * @param explanation
     *            the explanatory text of the anomaly.
     * @param violations
     *            the violations that cause the anomaly.
     * @param begin
     *            the offset of the beginning of the coveredText in the document
     *            text.
     * @param end
     *            the offset of the ending of the coveredText in the document
     *            text.
     */
    public static void createRatReadabilityAnomaly(JCas jCas, String name, String category, String severity,
            String explanation, List<String> violations, int begin, int end) {
        RatReadabilityAnomaly ratReadabilityAnomaly = new RatReadabilityAnomaly(jCas);

        ratReadabilityAnomaly.setAnomalyName(name);
        ratReadabilityAnomaly.setCategory(category);
        ratReadabilityAnomaly.setSeverity(severity);
        ratReadabilityAnomaly.setExplanation(explanation);
        ratReadabilityAnomaly.setNumberOfViolations(violations.size());
        ratReadabilityAnomaly.setViolations(CollectionUtils.transformToStringArray(jCas, violations));
        ratReadabilityAnomaly.setBegin(begin);
        ratReadabilityAnomaly.setEnd(end);
        ratReadabilityAnomaly.addToIndexes();
    }
}