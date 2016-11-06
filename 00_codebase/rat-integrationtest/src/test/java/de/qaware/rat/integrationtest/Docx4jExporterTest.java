package de.qaware.rat.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.exceptions.ExportException;
import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.exceptions.ParserException;
import de.qaware.rat.api.exceptions.PipelineException;
import de.qaware.rat.api.interfaces.ExporterService;
import de.qaware.rat.api.interfaces.ImporterService;
import de.qaware.rat.api.interfaces.PipelineService;
import de.qaware.rat.api.models.ConfigurationModel;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.common.AnomalyValidator;
import de.qaware.rat.common.ConfigurationUtils;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.common.serviceregistry.ServiceLocator;
import de.qaware.rat.statistics.StatisticExporter;
import de.qaware.rat.statistics.StatisticImporter;
import de.qaware.rat.type.RatAnomaly;

public class Docx4jExporterTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(Docx4jExporterTest.class);

    private static final String DEFAULT_CONFIGURATION_PATH_TEST = "src/test/resources/rat-config.xml";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testApplyAnnotationToMultipleDocuments()
            throws IOException, UIMAException, Docx4JException, ExportException, ImportException, PipelineException {
        // Arrange
        List<String> filePaths = ImporterUtils
                .getFilePathsFromDirectory("src/test/resources/documents/multiple-documents/");

        ImporterService importer = null;
        PipelineService pipeline = null;
        ExporterService exporter = null;

        for (String filePath : filePaths) {
            LOGGER.info(String.format("File path: \"%s\".", filePath));

            // Import
            String directoryPath = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
            String fileName = ImporterUtils.getFileNameFromPath(filePath);
            String fileExtension = ImporterUtils.detectFileExtension(filePath);

            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            importer = ServiceLocator.getService(ImporterService.class, fileExtension);
            if (importer == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }

            byte[] data = ImporterUtils.readFile(filePath);
            DocumentModel documentModel = importer.getDocumentModel(data);

            // Analysis
            String language = UimaUtils.detectLanguage(documentModel.getText());
            LOGGER.info(String.format("Language: \"%s\".", language));
            pipeline = ServiceLocator.getService(PipelineService.class, language);
            if (pipeline == null) {
                LOGGER.warn(String.format("Language \"%s\" is not supported.", language));
                continue;
            }

            JCas jCas = pipeline.processPipelineWithRutaScripts(documentModel.getText());
            Collection<RatAnomaly> anomalies = UimaUtils.getRatAnomalies(jCas);
            documentModel.setjCas(jCas);

            // Export
            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            exporter = ServiceLocator.getService(ExporterService.class, fileExtension);
            if (exporter == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }
            List<RatAnomaly> appliedAnomalies = exporter.applyAnnotations(documentModel, anomalies);

            exporter.addRatAnomaliesToCustomXmlPart(documentModel,
                    UimaUtils.getRatAnomalyModelsFromAnomalies(appliedAnomalies), new ArrayList<RatAnomalyModel>(),
                    new ArrayList<RatAnomalyModel>());

            exporter.exportDocument(documentModel, directoryPath, fileName, fileExtension);

            // Get generated document
            String generatedDocumentPath = null;
            if (fileName.endsWith("-rat")) {
                generatedDocumentPath = directoryPath + fileName + "." + fileExtension;
            } else {
                generatedDocumentPath = directoryPath + fileName + "-rat." + fileExtension;
            }
            byte[] generatedData = ImporterUtils.readFile(generatedDocumentPath);
            DocumentModel generatedDocumentModel = importer.getDocumentModel(generatedData);

            // Assert
            LOGGER.info("Number of applied anomalies: " + appliedAnomalies.size());
            LOGGER.info(
                    "Anomaly hash codes in document: " + generatedDocumentModel.getAppliedCommentsHashCodes().size());
            assertEquals(appliedAnomalies.size(), generatedDocumentModel.getAppliedCommentsHashCodes().size());
        }
    }

    @Test
    public void testHtmlReportExport() throws IOException, UIMAException, Docx4JException, ExportException,
            ImportException, PipelineException, ParserException {
        // Arrange
        List<String> filePaths = new ArrayList<String>();
        filePaths.add("src/test/resources/documents/html-report/large-document-rat.docx");

        ImporterService importer = null;
        PipelineService pipeline = null;
        ExporterService exporter = null;

        for (String filePath : filePaths) {
            LOGGER.info(String.format("File path: \"%s\".", filePath));

            // Import
            String directoryPath = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
            String fileName = ImporterUtils.getFileNameFromPath(filePath);
            String fileExtension = ImporterUtils.detectFileExtension(filePath);

            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            importer = ServiceLocator.getService(ImporterService.class, fileExtension);
            if (importer == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }

            byte[] data = ImporterUtils.readFile(filePath);
            DocumentModel documentModel = importer.getDocumentModel(data);

            String xml = ConfigurationUtils.loadConfiguration(DEFAULT_CONFIGURATION_PATH_TEST);
            ConfigurationModel configurationModel = ConfigurationUtils.parseConfiguration(xml);

            // Analysis
            String language = UimaUtils.detectLanguage(documentModel.getText());
            LOGGER.info(String.format("Language: \"%s\".", language));
            pipeline = ServiceLocator.getService(PipelineService.class, language);
            if (pipeline == null) {
                LOGGER.warn(String.format("Language \"%s\" is not supported.", language));
                continue;
            }

            JCas jCas = pipeline.processPipelineWithRutaScripts(documentModel.getText());
            Collection<RatAnomaly> anomalies = UimaUtils.getRatAnomalies(jCas);
            documentModel.setjCas(jCas);

            // Export
            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            exporter = ServiceLocator.getService(ExporterService.class, fileExtension);
            if (exporter == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }
            List<RatAnomaly> appliedAnomalies = exporter.applyAnnotations(documentModel, anomalies);
            exporter.exportDocument(documentModel, directoryPath, fileName, fileExtension);

            // Create HTML report
            StatisticExporter.exportStatisticHTML(documentModel.getjCas(),
                    UimaUtils.getRatAnomalyModelsFromAnomalies(appliedAnomalies), new ArrayList<RatAnomalyModel>(),
                    new ArrayList<RatAnomalyModel>(), configurationModel, "html/statistic-template.html", directoryPath,
                    fileName, fileExtension);

            // Get HTML report
            String reportPath = null;
            if (fileName.endsWith("-rat")) {
                reportPath = directoryPath + fileName + ".html";
            } else {
                reportPath = directoryPath + fileName + "-rat.html";
            }
            LOGGER.info(reportPath);
            String html = ImporterUtils.readFileAsString(reportPath);
            List<RatAnomalyModel> importedAnomalies = StatisticImporter.getCurrentReadabilityAnomaliesFromHTML(html);

            // Assert
            LOGGER.info("Number of applied anomalies: " + appliedAnomalies.size());
            LOGGER.info("Number of anomalies in report: " + importedAnomalies.size());
            assertEquals(appliedAnomalies.size(), importedAnomalies.size());
        }
    }

    @Test
    public void testRedundantCommentDetection()
            throws IOException, UIMAException, Docx4JException, ExportException, ImportException, PipelineException {
        // Arrange
        List<String> filePaths = new ArrayList<String>();
        filePaths.add("src/test/resources/documents/redundant-comment/large-document-rat.docx");

        ImporterService importer = null;
        PipelineService pipeline = null;

        for (String filePath : filePaths) {
            LOGGER.info(String.format("File path: \"%s\".", filePath));

            // Import
            String fileExtension = ImporterUtils.detectFileExtension(filePath);

            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            importer = ServiceLocator.getService(ImporterService.class, fileExtension);
            if (importer == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }

            byte[] data = ImporterUtils.readFile(filePath);
            DocumentModel documentModel = importer.getDocumentModel(data);

            // Analysis
            String language = UimaUtils.detectLanguage(documentModel.getText());
            LOGGER.info(String.format("Language: \"%s\".", language));
            pipeline = ServiceLocator.getService(PipelineService.class, language);
            if (pipeline == null) {
                LOGGER.warn(String.format("Language \"%s\" is not supported.", language));
                continue;
            }

            JCas jCas = pipeline.processPipelineWithRutaScripts(documentModel.getText());
            documentModel.setjCas(jCas);
            List<RatAnomaly> detectedAnomalies = UimaUtils.getRatAnomalies(jCas);

            AnomalyValidator anomalyValidator = new AnomalyValidator();
            anomalyValidator.validateAnomalies(documentModel, detectedAnomalies);

            LOGGER.info(String.format("Redundant Anomalies: \"%s\".", anomalyValidator.getRedundantAnomalies().size()));
            LOGGER.info(String.format("Detected anomalies : \"%s\".", detectedAnomalies.size()));

            // Assert
            assertEquals(2, anomalyValidator.getRedundantAnomalies().size());
            assertEquals(0, anomalyValidator.getDetectedAnomaliesToApply().size());
        }
    }

    @Test
    public void testFalsePositiveDetection()
            throws IOException, UIMAException, Docx4JException, ExportException, ImportException, PipelineException {
        // Arrange
        List<String> filePaths = ImporterUtils
                .getFilePathsFromDirectory("src/test/resources/documents/false-positive/");

        ImporterService importer = null;
        PipelineService pipeline = null;
        ExporterService exporter = null;

        for (String filePath : filePaths) {
            LOGGER.info(String.format("File path: \"%s\".", filePath));

            // Import
            String fileExtension = ImporterUtils.detectFileExtension(filePath);

            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            importer = ServiceLocator.getService(ImporterService.class, fileExtension);
            if (importer == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }

            byte[] data = ImporterUtils.readFile(filePath);
            DocumentModel documentModel = importer.getDocumentModel(data);

            // Analysis
            String language = UimaUtils.detectLanguage(documentModel.getText());
            LOGGER.info(String.format("Language: \"%s\".", language));
            pipeline = ServiceLocator.getService(PipelineService.class, language);
            if (pipeline == null) {
                LOGGER.warn(String.format("Language \"%s\" is not supported.", language));
                continue;
            }

            JCas jCas = pipeline.processPipelineWithRutaScripts(documentModel.getText());
            documentModel.setjCas(jCas);
            List<RatAnomaly> detectedAnomalies = UimaUtils.getRatAnomalies(jCas);

            AnomalyValidator anomalyValidator = new AnomalyValidator();
            anomalyValidator.validateAnomalies(documentModel, detectedAnomalies);

            // Export
            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            exporter = ServiceLocator.getService(ExporterService.class, fileExtension);
            if (exporter == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }
            List<RatAnomaly> appliedAnomalies = exporter.applyAnnotations(documentModel, anomalyValidator.getDetectedAnomaliesToApply());

            // Assert
            assertEquals(3, anomalyValidator.getFalsePositiveAnomalies().size());
            assertEquals(0, appliedAnomalies.size());
        }
    }

    @Test
    public void testIncorporatedProposal()
            throws IOException, UIMAException, Docx4JException, ExportException, ImportException, PipelineException {
        // Arrange
        List<String> filePaths = ImporterUtils
                .getFilePathsFromDirectory("src/test/resources/documents/incorporated-proposal/");

        ImporterService importer = null;
        PipelineService pipeline = null;
        ExporterService exporter = null;

        for (String filePath : filePaths) {
            LOGGER.info(String.format("File path: \"%s\".", filePath));

            // Import
            String fileExtension = ImporterUtils.detectFileExtension(filePath);

            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            importer = ServiceLocator.getService(ImporterService.class, fileExtension);
            if (importer == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }

            byte[] data = ImporterUtils.readFile(filePath);
            DocumentModel documentModel = importer.getDocumentModel(data);

            // Analysis
            String language = UimaUtils.detectLanguage(documentModel.getText());
            LOGGER.info(String.format("Language: \"%s\".", language));
            pipeline = ServiceLocator.getService(PipelineService.class, language);
            if (pipeline == null) {
                LOGGER.warn(String.format("Language \"%s\" is not supported.", language));
                continue;
            }

            JCas jCas = pipeline.processPipelineWithRutaScripts(documentModel.getText());
            documentModel.setjCas(jCas);
            List<RatAnomaly> detectedAnomalies = UimaUtils.getRatAnomalies(jCas);

            AnomalyValidator anomalyValidator = new AnomalyValidator();
            anomalyValidator.validateAnomalies(documentModel, detectedAnomalies);

            // Export
            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            exporter = ServiceLocator.getService(ExporterService.class, fileExtension);
            if (exporter == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }
            List<RatAnomaly> appliedAnomalies = exporter.applyAnnotations(documentModel, anomalyValidator.getDetectedAnomaliesToApply());

            anomalyValidator.prepareForNextAnalysis(documentModel, appliedAnomalies);
            List<RatAnomalyModel> newPreviousAppliedComments = anomalyValidator.getNewPreviousAppliedAnomalies();
            List<RatAnomalyModel> falsePositives = anomalyValidator.getFalsePositiveAnomalies();
            List<RatAnomalyModel> incorporatedImprovementProposals = anomalyValidator
                    .getIncorporatedAnomalies();
            List<RatAnomalyModel> redundantAnomalies = anomalyValidator.getRedundantAnomalies();

            LOGGER.info(String.format("Applied anomalies: \"%s\".", appliedAnomalies.size()));
            LOGGER.info(String.format("False positives: \"%s\".", falsePositives.size()));
            LOGGER.info(String.format("Incorporated proposals: \"%s\".", incorporatedImprovementProposals.size()));
            LOGGER.info(String.format("Redundant anomalies: \"%s\".", redundantAnomalies.size()));
            LOGGER.info(String.format("New previous applied comments: \"%s\".", newPreviousAppliedComments.size()));

            // Assert
            assertEquals(2, appliedAnomalies.size());
            assertEquals(2, falsePositives.size());
            assertEquals(1, incorporatedImprovementProposals.size());
            assertEquals(1, redundantAnomalies.size());
            assertEquals(3, newPreviousAppliedComments.size());
        }
    }

    @Test
    public void testEntireWorkflow()
            throws IOException, UIMAException, Docx4JException, ExportException, ImportException, PipelineException {
        // Arrange
        List<String> filePaths = ImporterUtils
                .getFilePathsFromDirectory("src/test/resources/documents/entire-workflow/");
    
        ImporterService importer = null;
        PipelineService pipeline = null;
        ExporterService exporter = null;
        for (String filePath : filePaths) {
            LOGGER.info(String.format("File path: \"%s\".", filePath));
    
            // Import
            String directoryPath = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
            String fileName = ImporterUtils.getFileNameFromPath(filePath);
            String fileExtension = ImporterUtils.detectFileExtension(filePath);
    
            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            importer = ServiceLocator.getService(ImporterService.class, fileExtension);
            if (importer == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }
    
            byte[] data = ImporterUtils.readFile(filePath);
            DocumentModel documentModel = importer.getDocumentModel(data);
    
            // Analysis
            String language = UimaUtils.detectLanguage(documentModel.getText());
            LOGGER.info(String.format("Language: \"%s\".", language));
            pipeline = ServiceLocator.getService(PipelineService.class, language);
            if (pipeline == null) {
                LOGGER.warn(String.format("Language \"%s\" is not supported.", language));
                continue;
            }
    
            JCas jCas = pipeline.processPipelineWithRutaScripts(documentModel.getText());
            documentModel.setjCas(jCas);
            List<RatAnomaly> detectedAnomalies = UimaUtils.getRatAnomalies(jCas);
    
            AnomalyValidator anomalyValidator = new AnomalyValidator();
            anomalyValidator.validateAnomalies(documentModel, detectedAnomalies);
    
            // Export
            LOGGER.info(String.format("File extension: \"%s\".", fileExtension));
            exporter = ServiceLocator.getService(ExporterService.class, fileExtension);
            if (exporter == null) {
                LOGGER.warn(String.format("File extension \"%s\" is not supported", fileExtension));
                continue;
            }
            List<RatAnomaly> appliedAnomalies = exporter.applyAnnotations(documentModel, anomalyValidator.getDetectedAnomaliesToApply());
    
            anomalyValidator.prepareForNextAnalysis(documentModel, appliedAnomalies);
            List<RatAnomalyModel> newPreviousAppliedComments = anomalyValidator.getNewPreviousAppliedAnomalies();
            List<RatAnomalyModel> falsePositives = anomalyValidator.getFalsePositiveAnomalies();
            List<RatAnomalyModel> incorporatedImprovementProposals = anomalyValidator
                    .getIncorporatedAnomalies();
            List<RatAnomalyModel> redundantAnomalies = anomalyValidator.getRedundantAnomalies();
    
            exporter.addRatAnomaliesToCustomXmlPart(documentModel, newPreviousAppliedComments, falsePositives,
                    incorporatedImprovementProposals);
    
            exporter.exportDocument(documentModel, directoryPath, fileName, fileExtension);
    
            // Get generated document
            String generatedDocumentPath = directoryPath + fileName + "-rat." + fileExtension;
            LOGGER.info("Generated document path: " + generatedDocumentPath);
    
            byte[] generatedData = ImporterUtils.readFile(generatedDocumentPath);
            DocumentModel generatedDocumentModel = importer.getDocumentModel(generatedData);
    
            LOGGER.info(String.format("Loaded Document: Applied comments hashcodes: \"%s\".",
                    documentModel.getAppliedCommentsHashCodes().size()));
            LOGGER.info(String.format("Loaded Document: Previous applied comments: \"%s\".",
                    documentModel.getPreviousAppliedComments().size()));
            LOGGER.info(String.format("Loaded Document: False positives: \"%s\".",
                    documentModel.getFalsePositives().size()));
            LOGGER.info(String.format("Loaded Document: Incorporated proposals: \"%s\".",
                    documentModel.getIncorporatedProposals().size()));
    
            LOGGER.info(String.format("Applied anomalies: \"%s\".", appliedAnomalies.size()));
            LOGGER.info(String.format("False positives: \"%s\".", falsePositives.size()));
            LOGGER.info(String.format("Incorporated proposals: \"%s\".", incorporatedImprovementProposals.size()));
            LOGGER.info(String.format("Redundant anomalies: \"%s\".", redundantAnomalies.size()));
            LOGGER.info(String.format("New previous applied comments: \"%s\".", newPreviousAppliedComments.size()));
    
            LOGGER.info(String.format("Generated Document: Applied comments hashcodes: \"%s\".",
                    generatedDocumentModel.getAppliedCommentsHashCodes().size()));
            LOGGER.info(String.format("Generated Document: Previous applied comments: \"%s\".",
                    generatedDocumentModel.getPreviousAppliedComments().size()));
            LOGGER.info(String.format("Generated Document: False positives: \"%s\".",
                    generatedDocumentModel.getFalsePositives().size()));
            LOGGER.info(String.format("Generated Document: Incorporated proposals: \"%s\".",
                    generatedDocumentModel.getIncorporatedProposals().size()));
    
            // Assert
            assertEquals(1, documentModel.getAppliedCommentsHashCodes().size());
            assertEquals(4, documentModel.getPreviousAppliedComments().size());
            assertEquals(0, documentModel.getFalsePositives().size());
            assertEquals(0, documentModel.getIncorporatedProposals().size());
    
            assertEquals(2, appliedAnomalies.size());
            assertEquals(2, falsePositives.size());
            assertEquals(1, incorporatedImprovementProposals.size());
            assertEquals(1, redundantAnomalies.size());
            assertEquals(3, newPreviousAppliedComments.size());
    
            assertEquals(3, generatedDocumentModel.getAppliedCommentsHashCodes().size());
            assertEquals(3, generatedDocumentModel.getPreviousAppliedComments().size());
            assertEquals(2, generatedDocumentModel.getFalsePositives().size());
            assertEquals(1, generatedDocumentModel.getIncorporatedProposals().size());
        }
    }
}