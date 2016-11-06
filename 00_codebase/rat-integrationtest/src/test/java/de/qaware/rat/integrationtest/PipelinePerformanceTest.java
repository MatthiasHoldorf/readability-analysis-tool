package de.qaware.rat.integrationtest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.enums.LinguisticEngineSteps;
import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.exceptions.PipelineException;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.codec.docx.Docx4jImporter;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.pipeline.de.Pipeline;
import de.qaware.rat.pipeline.de.PipelineFactory;
import de.tudarmstadt.ukp.dkpro.core.performance.type.TimerAnnotation;

public class PipelinePerformanceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PipelinePerformanceTest.class);

    private static DocumentModel documentModel = null;
    private static Map<String, Long> performanceResults = new HashMap<String, Long>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        byte[] data = ImporterUtils
                .readFile("src/test/resources/documents/performance-test/large-document.docx");
        documentModel = new Docx4jImporter().getDocumentModel(data);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        for (Map.Entry<String, Long> entry : performanceResults.entrySet()) {
            String clazzName = entry.getKey();
            Long processingTime = entry.getValue();
            LOGGER.info(String.format("The processing time of the %s was %s milliseconds.", clazzName, processingTime));
        }
    }

    private static void performanceTest(LinguisticEngineSteps linguisticEngineStep)
            throws ResourceInitializationException, PipelineException {
        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription stopwatchStart = PipelineFactory.getStopwatch("TimerName");
        AnalysisEngineDescription linguisticEngine = new Pipeline().createLinguisticEngine(linguisticEngineStep);
        AnalysisEngineDescription stopwatchEnd = PipelineFactory.getStopwatch("TimerName");
        AnalysisEngine analysisEngine = pipeline.createPipeline(stopwatchStart, linguisticEngine, stopwatchEnd);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        // Performance
        TimerAnnotation timer = (TimerAnnotation) UimaUtils
                .getAnnotationsByType(jCas, "de.tudarmstadt.ukp.dkpro.core.performance.type.TimerAnnotation").get(0);
        long pipelineDuration = (timer.getEndTime() - timer.getStartTime());

        // Add results
        performanceResults.put(linguisticEngineStep.toString(), pipelineDuration);
    }

    @Test
    public void testSegmenter() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(LinguisticEngineSteps.TOKENIZING);
    }

    @Test
    public void testPosTagger() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(LinguisticEngineSteps.POS);
    }
}