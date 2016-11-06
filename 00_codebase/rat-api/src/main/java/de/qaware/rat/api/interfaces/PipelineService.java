package de.qaware.rat.api.interfaces;

import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.qaware.rat.api.enums.LinguisticEngineSteps;
import de.qaware.rat.api.enums.RuleEngine;
import de.qaware.rat.api.exceptions.PipelineException;
import de.qaware.rat.api.models.AnnotatorRuleModel;
import de.qaware.rat.api.models.ConfigurationModel;

/**
 * The PipelineService interface is implemented by classes which provide
 * functionality to analyse linguistic properties of text and detection of
 * readability anomalies.
 * 
 * @author Matthias
 *
 */
public interface PipelineService extends ServiceProvider {
    /**
     * Builds an analysis engine description based on the configuration in the
     * {@code anomalyRuleModels}.
     * 
     * @param anomalyRuleModels
     *            the configuration for the {@code JavaAnnotator} classes.
     * @return an analysis engine description.
     * @throws ResourceInitializationException
     *             if an UIMA error occurs.
     */
    AnalysisEngineDescription createJavaRuleEngine(List<AnnotatorRuleModel> anomalyRuleModels)
            throws ResourceInitializationException;

    /**
     * Builds an analysis engine description of the default linguistic analysis.
     * 
     * @return an analysis engine description of the default linguistic
     *         analysis.
     * @throws ResourceInitializationException
     *             if an UIMA error occurs.
     */
    AnalysisEngineDescription createLinguisticEngine() throws ResourceInitializationException;

    /**
     * Builds an analysis engine description of a linguistic analysis specified
     * by the linguisticEngineSteps.
     * 
     * @return an analysis engine description of the linguistic analysis.
     * @throws ResourceInitializationException
     *             if an UIMA error occurs.
     */
    AnalysisEngineDescription createLinguisticEngine(LinguisticEngineSteps linguisticEngineSteps)
            throws ResourceInitializationException;

    /**
     * Build an analysis engine combined from multiple analysisEngineDesription.
     * 
     * @return the analysis engine.
     * @throws ResourceInitializationException
     *             if the analysis engine could not be produced.
     */
    AnalysisEngine createPipeline(AnalysisEngineDescription... analysisEngineDescriptions)
            throws ResourceInitializationException;

    /**
     * Builds an analysis engine description from the given path to UIMA Ruta
     * scripts.
     * 
     * @return an analysis engine description of the UIMA RUTA rules.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PipelineException
     *             if the pipeline cannot be build.
     * @throws ResourceInitializationException
     *             if the analysis engine could not be produced.
     */
    AnalysisEngineDescription createRutaRuleEngine()
            throws IOException, PipelineException, ResourceInitializationException;

    /**
     * Executes the given analysis engine on the provided text.
     * 
     * @param analysisEngine
     *            the analysis engine to run.
     * @param text
     *            the text to analyse.
     * @return a jCas object containing the results of the analysis.
     * @throws PipelineException
     *             if a pipeline error occurs.
     */
    JCas process(AnalysisEngine analysisEngine, String text) throws PipelineException;

    /**
     * 
     * 
     * @param text
     *            the text to analyse.
     * @param ruleEngine
     *            an ENUM specifying which rule set to apply.
     * @param configurationModel
     *            the configuration for the pipeline.
     * @return a jCas object containing the results of the analysis.
     * @throws PipelineException
     *             if a pipeline error occurs.
     */
    JCas process(String text, RuleEngine ruleEngine, ConfigurationModel configurationModel) throws PipelineException;

    /**
     * Creates an analysis engine with the entire rule set based on the
     * configurationModel and executes its on the provided text.
     * 
     * @param text
     *            the text to analyse.
     * @param configurationModel
     *            the configuration for the pipeline.
     * @return a jCas object containing the results of the analysis.
     * @throws PipelineException
     *             if a pipeline error occurs.
     */
    JCas processPipelineWithAllRules(String text, ConfigurationModel configurationModel) throws PipelineException;

    /**
     * Creates an analysis engine with the {@code Java Annotator} classes as
     * rule set based on the configurationModel and executes its on the provided
     * text.
     * 
     * @param text
     *            the text to analyse.
     * @param configurationModel
     *            the configuration for the pipeline.
     * @return a jCas object containing the results of the analysis.
     * @throws PipelineException
     *             if a pipeline error occurs.
     */
    JCas processPipelineWithJavaAnnotators(String text, ConfigurationModel configurationModel) throws PipelineException;

    /**
     * Creates an analysis engine with the UIMA Ruta rule set executes its on
     * the provided text.
     * 
     * @param text
     *            the text to analyse.
     * @return a jCas object containing the results of the analysis.
     * @throws PipelineException
     *             if a pipeline error occurs.
     */
    JCas processPipelineWithRutaScripts(String text) throws PipelineException;

}