package de.qaware.rat.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.enums.LinguisticEngineSteps;
import de.qaware.rat.api.exceptions.ParserException;
import de.qaware.rat.api.exceptions.PipelineException;
import de.qaware.rat.api.models.ConfigurationModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.common.ConfigurationUtils;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.pipeline.de.Pipeline;
import de.qaware.rat.statistics.StatisticExporter;
import de.qaware.rat.statistics.StatisticImporter;
import de.qaware.rat.type.RatAnomaly;

public class StatisticImporterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticImporterTest.class);

    private static final String DEFAULT_CONFIGURATION_PATH_TEST = "src/test/resources/rat-config.xml";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testGetReadabilityAnomaliesFromHtml() throws IOException, UIMAException, PipelineException, ParserException {
        // Arrange
        String text = ImporterUtils.readFileAsString(
                "src/test/resources/documents/statistic-importer-test/averageNumberOfCharactersPerSentence.txt");
        
        String directoryPath = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
        String fileName = "averageNumberOfCharactersPerSentence";
        String fileExtension = "docx";

        String xml = ConfigurationUtils.loadConfiguration(DEFAULT_CONFIGURATION_PATH_TEST);
        ConfigurationModel configurationModel = ConfigurationUtils.parseConfiguration(xml);
        
        Pipeline pipeline = new Pipeline();
        AnalysisEngineDescription linguisticEngine = pipeline.createLinguisticEngine(LinguisticEngineSteps.POS);
        AnalysisEngineDescription ruleEngine = pipeline.createRutaRuleEngine();
        AnalysisEngine analysisEngine = pipeline.createPipeline(linguisticEngine, ruleEngine);

        JCas jCas = pipeline.process(analysisEngine, text);
        List<RatAnomaly> anomalies = new ArrayList<RatAnomaly>(UimaUtils.getRatAnomalies(jCas));
        StatisticExporter.exportStatisticHTML(jCas, UimaUtils.getRatAnomalyModelsFromAnomalies(anomalies),
                UimaUtils.getRatAnomalyModelsFromAnomalies(anomalies), UimaUtils.getRatAnomalyModelsFromAnomalies(anomalies),
                configurationModel, "html/statistic-template.html", directoryPath, fileName,
                fileExtension);

        // Act
        String reportPath = directoryPath + fileName + "-rat.html";
        String html = ImporterUtils.readFileAsString(reportPath);
        List<RatAnomalyModel> currentAnomaliesInHtml = StatisticImporter.getCurrentReadabilityAnomaliesFromHTML(html);
        List<RatAnomalyModel> falsePositiveAnomaliesInHtml = StatisticImporter
                .getFalsePositiveReadabilityAnomaliesFromHTML(html);
        List<RatAnomalyModel> incorporatedAnomaliesInHtml = StatisticImporter
                .getFalsePositiveReadabilityAnomaliesFromHTML(html);

        // Assert
        assertEquals(anomalies.size(), currentAnomaliesInHtml.size());
        assertEquals(anomalies.size(), falsePositiveAnomaliesInHtml.size());
        assertEquals(anomalies.size(), incorporatedAnomaliesInHtml.size());
        
        for (int i = 0; i < currentAnomaliesInHtml.size(); i++) {
            LOGGER.info("Imported anomalies hash code: " + currentAnomaliesInHtml.get(i).getHashCode());
            LOGGER.info("Anomalies hash code: " + anomalies.get(i).getHashCode());
            assertEquals(currentAnomaliesInHtml.get(i).getHashCode(), anomalies.get(i).getHashCode());
        }

        for (int i = 0; i < falsePositiveAnomaliesInHtml.size(); i++) {
            LOGGER.info("Imported anomalies hash code: " + falsePositiveAnomaliesInHtml.get(i).getHashCode());
            LOGGER.info("Anomalies hash code: " + anomalies.get(i).getHashCode());
            assertEquals(falsePositiveAnomaliesInHtml.get(i).getHashCode(), anomalies.get(i).getHashCode());
        }

        for (int i = 0; i < incorporatedAnomaliesInHtml.size(); i++) {
            LOGGER.info("Imported anomalies hash code: " + incorporatedAnomaliesInHtml.get(i).getHashCode());
            LOGGER.info("Anomalies hash code: " + anomalies.get(i).getHashCode());
            assertEquals(incorporatedAnomaliesInHtml.get(i).getHashCode(), anomalies.get(i).getHashCode());
        }
    }
}