package de.qaware.rat.pipeline.de;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.matetools.MateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateMorphTagger;
import de.tudarmstadt.ukp.dkpro.core.matetools.MateParser;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.performance.Stopwatch;
import de.tudarmstadt.ukp.dkpro.core.readability.ReadabilityAnnotator;

/**
 * The PipelineFactory class creates {@code AnalysisEngineDescriptions} based on
 * the DKPro Core Collection for Apache UIMA.
 * 
 * @author Matthias
 *
 */
public final class PipelineFactory {
    private PipelineFactory() {
    }

    /**
     * Create an {@code AnalysisEngineDescription}.
     * 
     * @param componentClass
     *            the class to generate the {@code AnalysisEngineDescription}
     *            for.
     * @return the {@code AnalysisEngineDescription}.
     * @throws ResourceInitializationException
     *             if an UIMA error occurs.
     */
    public static AnalysisEngineDescription createAnalysisEngineDescription(
            Class<? extends AnalysisComponent> componentClass) throws ResourceInitializationException {
        return createEngineDescription(componentClass);
    }

    /**
     * Get a segmenter NLP-component.
     * 
     * @return the {@code AnalysisEngineDescription} of a segmenter
     *         NLP-component.
     * @throws ResourceInitializationException
     *             if a failure occurred during initialization.
     */
    public static AnalysisEngineDescription getSegmenter() throws ResourceInitializationException {
        return createEngineDescription(OpenNlpSegmenter.class);
    }

    /**
     * Get a stopwatch annotator to measure the time between two points in the
     * pipeline.
     * 
     * @param timerName
     *            the timer name for the stopwatch
     * @return the {@code AnalysisEngineDescription} of a stopwatch
     * @throws ResourceInitializationException
     *             if a failure occurred during initialization.
     */
    public static AnalysisEngineDescription getStopwatch(String timerName) throws ResourceInitializationException {
        return createEngineDescription(Stopwatch.class, Stopwatch.PARAM_TIMER_NAME, timerName);
    }

    /**
     * Get a pos-tagger NLP-component.
     * 
     * @return the {@code AnalysisEngineDescription} of a pos-tagger
     *         NLP-component.
     * @throws ResourceInitializationException
     *             if a failure occurred during initialization.
     */
    public static AnalysisEngineDescription getPosTagger() throws ResourceInitializationException {
        return createEngineDescription(OpenNlpPosTagger.class);
    }

    /**
     * Get a lemmatizer NLP-component.
     * 
     * @return the {@code AnalysisEngineDescription} of a lemmatizer
     *         NLP-component.
     * @throws ResourceInitializationException
     *             if a failure occurred during initialization.
     */
    public static AnalysisEngineDescription getLemmatizer() throws ResourceInitializationException {
        return createEngineDescription(MateLemmatizer.class);
    }

    /**
     * Get a morph-tagger NLP-component.
     * 
     * @return the {@code AnalysisEngineDescription} of the NLP-component.
     * @throws ResourceInitializationException
     *             if a failure occurred during initialization.
     */
    public static AnalysisEngineDescription getMorphTagger() throws ResourceInitializationException {
        return createEngineDescription(MateMorphTagger.class);
    }

    /**
     * Get a dependency parser NLP-component.
     * 
     * @return the {@code AnalysisEngineDescription} of a dependency parser
     *         NLP-component.
     * @throws ResourceInitializationException
     *             if a failure occurred during initialization.
     */
    public static AnalysisEngineDescription getDependencyParser() throws ResourceInitializationException {
        return createEngineDescription(MateParser.class);
    }

    /**
     * Get a readability annotator NLP-component.
     * 
     * @return the {@code AnalysisEngineDescription} of a sgementer
     *         NLP-component.
     * @throws ResourceInitializationException
     *             if a failure occurred during initialization.
     */
    public static AnalysisEngineDescription getReadabilityAnnotator() throws ResourceInitializationException {
        return createEngineDescription(ReadabilityAnnotator.class);
    }
}