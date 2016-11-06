package de.qaware.rat.executor.cmd;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.enums.RuleEngine;
import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.interfaces.ImporterService;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.serviceregistry.ServiceLocator;
import de.qaware.rat.statistics.StatisticImporter;

public class CommandLineExecutorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineExecutorTest.class);

    private static final String DEFAULT_CONFIGURATION_PATH_TEST = "src/test/resources/documents/rat-config.xml";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testApplyAnnotationToMultipleDocuments() throws IOException, ImportException {
        // Arrange
        String outputDirectory = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
        LOGGER.info("" + Files.isDirectory(Paths.get(outputDirectory)));

        String[] testArguments = new String[] { "-o " + outputDirectory,
                "src/test/resources/documents/multiple-documents/45-page-9500-words-assignment.docx",
                "src/test/resources/documents/multiple-documents/large-document.docx" };
        ImporterService importer = ServiceLocator.getService(ImporterService.class, "docx");

        // Act
        new CommandLineExecutor().execute(testArguments, RuleEngine.RUTA, DEFAULT_CONFIGURATION_PATH_TEST);

        // Assert
        String filePath1 = outputDirectory + "large-document-rat.docx";
        String filePath2 = outputDirectory + "45-page-9500-words-assignment-rat.docx";

        DocumentModel document1 = importer.getDocumentModel(ImporterUtils.readFile(filePath1));
        DocumentModel document2 = importer.getDocumentModel(ImporterUtils.readFile(filePath2));

        LOGGER.info("Applied comment hash codes: " + document1.getAppliedCommentsHashCodes().size());
        LOGGER.info("Applied comment hash codes: " + document2.getAppliedCommentsHashCodes().size());

        assertEquals(4, document1.getAppliedCommentsHashCodes().size());
        assertEquals(122, document2.getAppliedCommentsHashCodes().size());
    }

    @Test
    public void testHtmlReportExport() throws IOException {
        // Arrange
        String outputDirectory = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
        String[] testArguments = new String[] { "-c " + DEFAULT_CONFIGURATION_PATH_TEST, "-o " + outputDirectory,
                "src/test/resources/documents/html-report/large-document.docx" };

        // Act
        new CommandLineExecutor().execute(testArguments, RuleEngine.RUTA, DEFAULT_CONFIGURATION_PATH_TEST);

        // Assert
        String filePath = outputDirectory + "large-document-rat.html";
        List<RatAnomalyModel> importedAnomalies = StatisticImporter
                .getCurrentReadabilityAnomaliesFromHTML(ImporterUtils.readFileAsString(filePath));

        LOGGER.info("Imported Anomalies: " + importedAnomalies.size());

        assertEquals(4, importedAnomalies.size());
    }

    @Test
    public void testRedundantCommentDetection() throws ImportException, IOException {
        // Arrange
        String outputDirectory = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
        String[] testArguments = new String[] { "-o " + outputDirectory,
                "src/test/resources/documents/redundant-comment/large-document.docx" };
        ImporterService importer = ServiceLocator.getService(ImporterService.class, "docx");

        // Act
        new CommandLineExecutor().execute(testArguments, RuleEngine.RUTA, DEFAULT_CONFIGURATION_PATH_TEST);

        // Assert
        String filePath = outputDirectory + "large-document-rat.docx";
        DocumentModel document = importer.getDocumentModel(ImporterUtils.readFile(filePath));

        LOGGER.info("Previous applied comments: " + document.getPreviousAppliedComments().size());
        LOGGER.info("Applied comment hash codes: " + document.getAppliedCommentsHashCodes().size());

        // Assert
        assertEquals(2, document.getPreviousAppliedComments().size());
        assertEquals(2, document.getAppliedCommentsHashCodes().size());
    }

    @Test
    public void testFalsePositiveDetection() throws ImportException, IOException {
        // Arrange
        String outputDirectory = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
        String[] testArguments = new String[] { "-o " + outputDirectory,
                "src/test/resources/documents/false-positive/large-document.docx" };
        ImporterService importer = ServiceLocator.getService(ImporterService.class, "docx");

        // Act
        new CommandLineExecutor().execute(testArguments, RuleEngine.RUTA, DEFAULT_CONFIGURATION_PATH_TEST);

        // Assert
        String filePath = outputDirectory + "large-document-rat.docx";
        DocumentModel document = importer.getDocumentModel(ImporterUtils.readFile(filePath));

        LOGGER.info("Previous applied comments: " + document.getPreviousAppliedComments().size());
        LOGGER.info("False Posities: " + document.getFalsePositives().size());

        // Assert
        assertEquals(1, document.getPreviousAppliedComments().size());
        assertEquals(3, document.getFalsePositives().size());
    }

    @Test
    public void testIncorporatedProposal() throws ImportException, IOException {
        // Arrange
        String outputDirectory = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
        String[] testArguments = new String[] { "-o " + outputDirectory,
                "src/test/resources/documents/incorporated-proposal/incorporated-proposal.docx" };
        ImporterService importer = ServiceLocator.getService(ImporterService.class, "docx");

        // Act
        new CommandLineExecutor().execute(testArguments, RuleEngine.RUTA, DEFAULT_CONFIGURATION_PATH_TEST);

        // Assert
        String filePath = outputDirectory + "incorporated-proposal-rat.docx";
        DocumentModel document = importer.getDocumentModel(ImporterUtils.readFile(filePath));

        LOGGER.info(String.format("False positives: \"%s\".", document.getFalsePositives().size()));
        LOGGER.info(String.format("Incorporated Proposals: \"%s\".", document.getIncorporatedProposals().size()));
        LOGGER.info(String.format("Previous applied comments: \"%s\".", document.getPreviousAppliedComments().size()));

        // Assert
        assertEquals(2, document.getFalsePositives().size());
        assertEquals(1, document.getIncorporatedProposals().size());
        assertEquals(3, document.getPreviousAppliedComments().size());
    }

    @Test
    public void testEntireWorkflow() throws ImportException, IOException {
        // Arrange
        String outputDirectory = ImporterUtils.getDirectoryPathFromFilePath(folder.newFile().getAbsolutePath(), "\\");
        String[] testArguments = new String[] { "-o " + outputDirectory,
                "src/test/resources/documents/entire-workflow/entire-workflow.docx" };
        ImporterService importer = ServiceLocator.getService(ImporterService.class, "docx");

        // Act
        new CommandLineExecutor().execute(testArguments, RuleEngine.RUTA, DEFAULT_CONFIGURATION_PATH_TEST);

        // Assert
        String filePath = outputDirectory + "entire-workflow-rat.docx";
        DocumentModel document = importer.getDocumentModel(ImporterUtils.readFile(filePath));

        LOGGER.info(
                String.format("Applied comment hash codes:  \"%s\".", document.getAppliedCommentsHashCodes().size()));
        LOGGER.info(String.format("False positives: \"%s\".", document.getFalsePositives().size()));
        LOGGER.info(String.format("Incorporated Proposals: \"%s\".", document.getIncorporatedProposals().size()));
        LOGGER.info(String.format("Previous applied comments: \"%s\".", document.getPreviousAppliedComments().size()));

        // Assert
        assertEquals(3, document.getAppliedCommentsHashCodes().size());
        assertEquals(2, document.getFalsePositives().size());
        assertEquals(1, document.getIncorporatedProposals().size());
        assertEquals(3, document.getPreviousAppliedComments().size());
    }

    @Test
    public void testJavaRuleEngine() throws ImportException, IOException {
        // Arrange
        String filePath = "src/test/resources/documents/java-rule-engine/45-page-9500-words-assignment.docx";
        String[] testArguments = new String[] { filePath };

        // Act
        new CommandLineExecutor().execute(testArguments, RuleEngine.JAVA, DEFAULT_CONFIGURATION_PATH_TEST);
    }
}