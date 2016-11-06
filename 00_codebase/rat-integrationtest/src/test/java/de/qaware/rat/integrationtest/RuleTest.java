package de.qaware.rat.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import de.qaware.rat.api.enums.LinguisticEngineSteps;
import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.exceptions.PipelineException;
import de.qaware.rat.api.models.ConfigurationModel;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.codec.docx.Docx4jImporter;
import de.qaware.rat.common.ConfigurationUtils;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.pipeline.de.Pipeline;
import de.qaware.rat.pipeline.de.rules.Rule;
import de.qaware.rat.type.RatReadabilityAnomaly;

public class RuleTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaAnnotatorTest.class);
    
    private static final String DEFAULT_CONFIGURATION_PATH_TEST = "src/test/resources/rat-config.xml";
    
    private static ConfigurationModel configurationModel;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        String xml = ConfigurationUtils.loadConfiguration(DEFAULT_CONFIGURATION_PATH_TEST);
        configurationModel = ConfigurationUtils.parseConfiguration(xml);
    }

    @Test
    public void testNominalStyleDetection() throws IOException, UIMAException, ImportException, PipelineException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/java-annotator/NominalStyle.docx");
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Act
        Pipeline pipeline = new Pipeline();

        // Create Pipeline
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = Rule.create(configurationModel.getAnomalyRuleModel("NominalStyle"));
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        // Process document
        JCas jCas = pipeline.process(analysisEngine, documentModel.getText());

        Collection<RatReadabilityAnomaly> annotations = JCasUtil.select(jCas, RatReadabilityAnomaly.class);

        // Assert
        LOGGER.info(Iterables.get(annotations, 0).toString());

        assertEquals(1, annotations.size());
    }
}