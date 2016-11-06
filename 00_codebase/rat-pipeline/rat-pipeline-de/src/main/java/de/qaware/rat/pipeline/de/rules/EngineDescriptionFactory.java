package de.qaware.rat.pipeline.de.rules;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.qaware.rat.api.models.AnnotatorRuleModel;

/**
 * The {@code EngineDescriptionFactory} interfaces describes the contract to
 * function to create an {@code AnalysisEngineDescription} for the UIMA
 * pipeline.
 * 
 * @author Matthias
 *
 */
public interface EngineDescriptionFactory {
    /**
     * Create an {@code AnalysisEngineDescription} for the UIMA pipeline.
     * 
     * @param anomalyRule
     *            an {@code AnnotatorRuleModel} as abstraction of the
     *            configuration of the {@code Java Annotator}.
     * @return the {@code AnalysisEngineDescription} of the {@code JavaAnnotator}.
     * @throws ResourceInitializationException
     *             if the {@code JavaAnnotator} cannot be created.
     */
    AnalysisEngineDescription createEngineDescription(AnnotatorRuleModel anomalyRule)
            throws ResourceInitializationException;
}
