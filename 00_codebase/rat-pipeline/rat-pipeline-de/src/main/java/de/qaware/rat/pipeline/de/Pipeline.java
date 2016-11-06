package de.qaware.rat.pipeline.de;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.ruta.engine.RutaEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.enums.LinguisticEngineSteps;
import de.qaware.rat.api.enums.RuleEngine;
import de.qaware.rat.api.exceptions.PipelineException;
import de.qaware.rat.api.interfaces.PipelineService;
import de.qaware.rat.api.models.AnnotatorRuleModel;
import de.qaware.rat.api.models.ConfigurationModel;
import de.qaware.rat.common.ClassPathUtils;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.pipeline.de.rules.Rule;

/**
 * The Pipeline class combines {@code AnalysisEngineDescription} from the
 * linguistic engine and the rule engine into a single executable pipeline.
 * 
 * <p>
 * Additionally, the class processes a given analysisEngine on a given text.
 * 
 * @author Matthias
 *
 */
public final class Pipeline implements PipelineService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);

    private static final Locale LOCALE_DE = Locale.GERMAN;
    private static final String DEFAULT_RUTA_SCRIPTS_LOCATION = "ruta-script/" + LOCALE_DE.toLanguageTag();

    private AnalysisEngine analysisEngineCache = null;

    @Override
    public AnalysisEngineDescription createJavaRuleEngine(List<AnnotatorRuleModel> anomalyRuleModels)
            throws ResourceInitializationException {
        List<AnalysisEngineDescription> analysisEngineDescriptions = new ArrayList<AnalysisEngineDescription>();

        for (AnnotatorRuleModel anomalyRule : anomalyRuleModels) {
            if (anomalyRule.isEnabled()) {
                analysisEngineDescriptions.add(Rule.create(anomalyRule));
            }
        }

        AnalysisEngineDescription[] aes = new AnalysisEngineDescription[analysisEngineDescriptions.size()];
        aes = analysisEngineDescriptions.toArray(aes);
        return createEngineDescription(aes);
    }

    @Override
    public AnalysisEngineDescription createLinguisticEngine() throws ResourceInitializationException {
        return createLinguisticEngine(LinguisticEngineSteps.POS);
    }

    @Override
    public AnalysisEngineDescription createLinguisticEngine(LinguisticEngineSteps linguisticEngineSteps)
            throws ResourceInitializationException {
        AnalysisEngineDescription analsisEngineDescription = null;

        switch (linguisticEngineSteps) {
        case TOKENIZING:
            analsisEngineDescription = createEngineDescription(PipelineFactory.getSegmenter());
            break;
        case LEMMA:
            analsisEngineDescription = createEngineDescription(PipelineFactory.getSegmenter(),
                    PipelineFactory.getLemmatizer());
            break;
        case MORPH:
            analsisEngineDescription = createEngineDescription(PipelineFactory.getSegmenter(),
                    PipelineFactory.getLemmatizer(), PipelineFactory.getMorphTagger());
            break;
        case POS:
            analsisEngineDescription = createEngineDescription(PipelineFactory.getSegmenter(),
                    PipelineFactory.getPosTagger());
            break;
        case DEPENDENCY:
            analsisEngineDescription = createEngineDescription(PipelineFactory.getSegmenter(),
                    PipelineFactory.getPosTagger(), PipelineFactory.getDependencyParser());
            break;
        default:
            break;
        }

        return analsisEngineDescription;
    }

    @Override
    public AnalysisEngine createPipeline(AnalysisEngineDescription... analysisEngineDescriptions)
            throws ResourceInitializationException {
        AnalysisEngineDescription analysisEngineDescription = createEngineDescription(analysisEngineDescriptions);
        AnalysisEngine analysisEngine = UIMAFramework.produceAnalysisEngine(analysisEngineDescription);

        return analysisEngine;
    }

    @Override
    public AnalysisEngineDescription createRutaRuleEngine()
            throws IOException, PipelineException, ResourceInitializationException {
        List<String> rutaScriptPaths = new ArrayList<String>();

        List<String> fileNames = ClassPathUtils.getFileNamesFromDirectory(DEFAULT_RUTA_SCRIPTS_LOCATION);

        for (String fileName : fileNames) {
            try {
                rutaScriptPaths.add(ClassPathUtils.getPath(DEFAULT_RUTA_SCRIPTS_LOCATION + "/" + fileName));
            } catch (URISyntaxException e) {
                LOGGER.error("Could load the ruta script %s from class path.", fileName);
            }
        }

        List<String> rutaScriptNames = new ArrayList<String>();
        String rutaScriptLocation = ImporterUtils.getDirectoryPathFromFilePath(rutaScriptPaths.get(0));
        for (String rutaScriptPath : rutaScriptPaths) {
            rutaScriptNames.add(ImporterUtils.getFileNameFromPath(rutaScriptPath));
        }

        List<AnalysisEngineDescription> analysisEngineDescriptions = new ArrayList<AnalysisEngineDescription>();
        for (String rutaScriptName : rutaScriptNames) {
            try {
                analysisEngineDescriptions.add(
                        createEngineDescription(RutaEngine.class, RutaEngine.PARAM_SCRIPT_PATHS, rutaScriptLocation,
                                RutaEngine.PARAM_MAIN_SCRIPT, rutaScriptName, RutaEngine.PARAM_DEBUG, true));
            } catch (ResourceInitializationException e) {
                throw new PipelineException("The ruta-script " + rutaScriptName + " is not valid.", e);
            }
        }

        AnalysisEngineDescription[] aes = new AnalysisEngineDescription[analysisEngineDescriptions.size()];
        aes = analysisEngineDescriptions.toArray(aes);
        return createEngineDescription(aes);
    }

    @Override
    public String getCapability() {
        return LOCALE_DE.toLanguageTag();
    }

    @Override
    public JCas process(AnalysisEngine analysisEngine, String text) throws PipelineException {
        LOGGER.info("START ANALYSIS");

        JCas jCas = null;
        try {
            jCas = JCasFactory.createJCas();
            jCas.setDocumentLanguage("de");
            jCas.setDocumentText(text);

            long startTime = System.nanoTime();
            analysisEngine.process(jCas);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);

            LOGGER.info("Analysis took " + (duration / 1000000) + " milliseconds.");
            LOGGER.info("END ANALYSIS");
        } catch (UIMAException e) {
            throw new PipelineException("The analysis of the document failed. " + e.getMessage(), e);
        }

        return jCas;
    }

    @Override
    public JCas process(String text, RuleEngine ruleEngine, ConfigurationModel configurationModel)
            throws PipelineException {
        JCas jCas = null;

        switch (ruleEngine) {
        case RUTA:
            jCas = processPipelineWithRutaScripts(text);
            break;
        case JAVA:
            jCas = processPipelineWithJavaAnnotators(text, configurationModel);
            break;
        case ALL:
            jCas = processPipelineWithAllRules(text, configurationModel);
            break;
        }

        return jCas;
    }

    @Override
    public JCas processPipelineWithAllRules(String text, ConfigurationModel configurationModel)
            throws PipelineException {
        if (analysisEngineCache == null) {
            try {
                AnalysisEngineDescription linguisticEngine = createLinguisticEngine(LinguisticEngineSteps.POS);
                AnalysisEngineDescription rutaRuleEngine = createRutaRuleEngine();
                AnalysisEngineDescription javaRuleEngine = createJavaRuleEngine(
                        configurationModel.getAnomalyRuleModels());
                analysisEngineCache = createPipeline(linguisticEngine, rutaRuleEngine, javaRuleEngine);
            } catch (ResourceInitializationException | IOException e) {
                throw new PipelineException("The creation of the analysis engine failed. " + e.getMessage(), e);
            }
        }

        return process(analysisEngineCache, text);
    }

    /**
     * Executes the given analysisEngine on the given text parameter.
     * 
     * @param analysisEngine
     *            the analysis engine to run.
     * @param text
     *            the text to analyse.
     * @return a jCas object containing the results of the analysis.
     * @throws IOException
     *             if an I/O error occurs.
     * @throws PipelineException
     */
    @Override
    public JCas processPipelineWithJavaAnnotators(String text, ConfigurationModel configurationModel)
            throws PipelineException {
        if (analysisEngineCache == null) {
            try {
                AnalysisEngineDescription linguisticEngine = createLinguisticEngine(LinguisticEngineSteps.POS);
                AnalysisEngineDescription javaRuleEngine = createJavaRuleEngine(
                        configurationModel.getAnomalyRuleModels());
                analysisEngineCache = createPipeline(linguisticEngine, javaRuleEngine);
            } catch (ResourceInitializationException e) {
                throw new PipelineException("The creation of the analysis engine failed. " + e.getMessage(), e);
            }
        }

        return process(analysisEngineCache, text);
    }

    @Override
    public JCas processPipelineWithRutaScripts(String text) throws PipelineException {
        if (analysisEngineCache == null) {
            try {
                AnalysisEngineDescription linguisticEngine = createLinguisticEngine(LinguisticEngineSteps.POS);
                AnalysisEngineDescription ruleEngine = createRutaRuleEngine();
                analysisEngineCache = createPipeline(linguisticEngine, ruleEngine);
            } catch (ResourceInitializationException | IOException e) {
                throw new PipelineException("The creation of the analysis engine failed. " + e.getMessage(), e);
            }
        }

        return process(analysisEngineCache, text);
    }
}