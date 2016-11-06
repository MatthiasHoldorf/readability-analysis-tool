package de.qaware.rat.integrationtest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
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
import de.qaware.rat.pipeline.de.rules.AdjectiveStyleAnnotator;
import de.qaware.rat.pipeline.de.rules.AmbiguousAdjectivesAndAdverbsAnnotator;
import de.qaware.rat.pipeline.de.rules.ConsecutiveFillersAnnotator;
import de.qaware.rat.pipeline.de.rules.ConsecutivePrepositionsAnnotator;
import de.qaware.rat.pipeline.de.rules.DoubleNegativeAnnotator;
import de.qaware.rat.pipeline.de.rules.FillerAnnotator;
import de.qaware.rat.pipeline.de.rules.FillerSentenceAnnotator;
import de.qaware.rat.pipeline.de.rules.IndirectSpeechAnnotator;
import de.qaware.rat.pipeline.de.rules.LeadingAttributesAnnotator;
import de.qaware.rat.pipeline.de.rules.LongSentenceAnnotator;
import de.qaware.rat.pipeline.de.rules.LongWordAnnotator;
import de.qaware.rat.pipeline.de.rules.ModalVerbAnnotator;
import de.qaware.rat.pipeline.de.rules.ModalVerbSentenceAnnotator;
import de.qaware.rat.pipeline.de.rules.NestedSentenceAnnotator;
import de.qaware.rat.pipeline.de.rules.NestedSentenceConjunctionAnnotator;
import de.qaware.rat.pipeline.de.rules.NestedSentenceDelimiterAnnotator;
import de.qaware.rat.pipeline.de.rules.NominalStyleAnnotator;
import de.qaware.rat.pipeline.de.rules.PassiveVoiceAnnotator;
import de.qaware.rat.pipeline.de.rules.SentencesStartWithSameWordAnnotator;
import de.qaware.rat.pipeline.de.rules.SubjectiveLanguageAnnotator;
import de.qaware.rat.pipeline.de.rules.UnnecessarySyllablesAnnotator;
import de.tudarmstadt.ukp.dkpro.core.performance.type.TimerAnnotation;

public class JavaAnnotatorPerformanceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaAnnotatorPerformanceTest.class);

    private static DocumentModel documentModel = null;
    private static Map<String, Long> performanceResults = new HashMap<String, Long>();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        byte[] data = ImporterUtils.readFile(
                "src/test/resources/documents/performance-test/45-page-9500-words-assignment.docx");
        documentModel = new Docx4jImporter().getDocumentModel(data);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        for (Map.Entry<String, Long> entry : performanceResults.entrySet()) {
            String clazzName = entry.getKey();
            Long processingTime = entry.getValue();
            LOGGER.info(String.format("The processing time of the %s annotator was %s milliseconds.", clazzName, processingTime));
        }
    }

    private static void performanceTest(Class<? extends JCasAnnotator_ImplBase> clazz)
            throws ResourceInitializationException, PipelineException {
        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription stopwatchStart = PipelineFactory.getStopwatch("TimerName");
        AnalysisEngineDescription ruleEngine = PipelineFactory.createAnalysisEngineDescription(clazz);
        AnalysisEngineDescription stopwatchEnd = PipelineFactory.getStopwatch("TimerName");
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, stopwatchStart, ruleEngine,
                stopwatchEnd);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        // Performance
        TimerAnnotation timer = (TimerAnnotation) UimaUtils
                .getAnnotationsByType(jCas, "de.tudarmstadt.ukp.dkpro.core.performance.type.TimerAnnotation").get(0);
        long pipelineDuration = (timer.getEndTime() - timer.getStartTime());

        // Add results
        performanceResults.put(clazz.getName(), pipelineDuration);
    }

    @Test
    public void testNominalStyleDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(NominalStyleAnnotator.class);
    }

    @Test
    public void testLeadingAttributesDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(LeadingAttributesAnnotator.class);
    }

    @Test
    public void testLongSentenceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(LongSentenceAnnotator.class);
    }

    @Test
    public void testAdjectiveStyleDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(AdjectiveStyleAnnotator.class);
    }

    @Test
    public void testNestedSentenceConjunctionDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(NestedSentenceConjunctionAnnotator.class);
    }

    @Test
    public void testNestedSentenceDelimiterDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(NestedSentenceDelimiterAnnotator.class);
    }

    @Test
    public void testNestedSentenceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(NestedSentenceAnnotator.class);
    }

    @Test
    public void testConsecutiveFillersDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(ConsecutiveFillersAnnotator.class);
    }

    @Test
    public void testConsecutivePrepositionsDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(ConsecutivePrepositionsAnnotator.class);
    }

    @Test
    public void testModalVerbSentenceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(ModalVerbSentenceAnnotator.class);
    }

    @Test
    public void testFillerDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(FillerAnnotator.class);
    }

    @Test
    public void testModalVerbDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(ModalVerbAnnotator.class);
    }

    @Test
    public void testSentencesStartWithSameWordDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(SentencesStartWithSameWordAnnotator.class);
    }

    @Test
    public void testPassiveVoiceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(PassiveVoiceAnnotator.class);
    }

    @Test
    public void testSubjectiveLanguage() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(SubjectiveLanguageAnnotator.class);
    }

    @Test
    public void testDoubleNegativeDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(DoubleNegativeAnnotator.class);
    }

    @Test
    public void testIndirectSpeechDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(IndirectSpeechAnnotator.class);
    }

    @Test
    public void testUnnecessarySyllableDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(UnnecessarySyllablesAnnotator.class);
    }

    @Test
    public void testLongWordDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(LongWordAnnotator.class);
    }

    @Test
    public void testAmbiguousAdjectivesAndAdverbsDetection()
            throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(AmbiguousAdjectivesAndAdverbsAnnotator.class);
    }

    @Test
    public void testFillerSentenceDetection() throws IOException, UIMAException, ImportException, PipelineException {
        performanceTest(FillerSentenceAnnotator.class);
    }
}