package de.qaware.rat.integrationtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.enums.LinguisticEngineSteps;
import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.exceptions.ParserException;
import de.qaware.rat.api.exceptions.PipelineException;
import de.qaware.rat.api.interfaces.PipelineService;
import de.qaware.rat.api.models.AnnotationModel;
import de.qaware.rat.api.models.ConfigurationModel;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.codec.docx.Docx4jImporter;
import de.qaware.rat.common.ConfigurationUtils;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.common.serviceregistry.ServiceLocator;

public class PipelineTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineTest.class);

    @Test
    public void testCreatePipeline() throws ResourceInitializationException, IOException, PipelineException {
        // Arrange
        PipelineService pipeline = ServiceLocator.getService(PipelineService.class, "de");

        // Act
        AnalysisEngineDescription linguisticEngine = pipeline
                .createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = pipeline.createRutaRuleEngine();
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Assert
        assertNotNull(analysisEngine);
    }
    
    @Test
    public void testCreateJavaRuleEngine() throws ResourceInitializationException, IOException, PipelineException, ParserException {
        // Arrange
        PipelineService pipeline = ServiceLocator.getService(PipelineService.class, "de");
        
        String config = ImporterUtils.readFileAsString("src/test/resources/rat-config.xml");
        ConfigurationModel configurationModel = ConfigurationUtils.parseConfiguration(config);
       
        // Act
        AnalysisEngineDescription ruleEngine = pipeline.createJavaRuleEngine(configurationModel.getAnomalyRuleModels());
        
        // Assert
        assertNotNull(ruleEngine);
    }

    @Test
    public void testSentenceAnnotationType() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/pipeline-test/large-document.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        PipelineService pipeline = ServiceLocator.getService(PipelineService.class, "de");

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline
                .createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = pipeline.createRutaRuleEngine();
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        List<AnnotationModel> annotationModels = UimaUtils.getAnnotationModelsByType(jCas,
                "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence");

        for (AnnotationModel annotationModel : annotationModels) {
            LOGGER.info(annotationModel.getCoveredText());
        }

        // Assert
        assertEquals(20, annotationModels.size());
    }

    @Test
    public void testNounsAnnotationTypeForLargeDocument() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/pipeline-test/large-document.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        PipelineService pipeline = ServiceLocator.getService(PipelineService.class, "de");

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline
                .createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = pipeline.createRutaRuleEngine();
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        List<AnnotationModel> annotationModels = UimaUtils.getAnnotationModelsByType(jCas,
                "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.NN");

        // Assert
        assertEquals(37, annotationModels.size());
    }

    @Test
    public void testNounsAnnotationTypeForFiftyPageDocument() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/pipeline-test/45-page-9500-words-assignment.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        PipelineService pipeline = ServiceLocator.getService(PipelineService.class, "de");

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline
                .createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = pipeline.createRutaRuleEngine();
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        List<AnnotationModel> annotationModels = UimaUtils.getAnnotationModelsByType(jCas,
                "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.NN");

        // Assert
        assertEquals(2162, annotationModels.size());
    }
}