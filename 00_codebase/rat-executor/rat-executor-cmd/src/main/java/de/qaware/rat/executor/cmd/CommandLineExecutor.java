package de.qaware.rat.executor.cmd;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.enums.RuleEngine;
import de.qaware.rat.api.exceptions.ExportException;
import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.exceptions.ParserException;
import de.qaware.rat.api.exceptions.PipelineException;
import de.qaware.rat.api.interfaces.ExporterService;
import de.qaware.rat.api.interfaces.ImporterService;
import de.qaware.rat.api.interfaces.PipelineService;
import de.qaware.rat.api.models.ArgumentsModel;
import de.qaware.rat.api.models.ConfigurationModel;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.codec.docx.utils.Docx4jUtils;
import de.qaware.rat.common.AnomalyValidator;
import de.qaware.rat.common.ConfigurationUtils;
import de.qaware.rat.common.ImporterUtils;
import de.qaware.rat.common.UimaUtils;
import de.qaware.rat.common.serviceregistry.ServiceLocator;
import de.qaware.rat.statistics.StatisticExporter;
import de.qaware.rat.type.RatAnomaly;

/**
 * This class executes the RAT application in the context of command line
 * environment.
 * 
 * @author Matthias
 *
 */
public final class CommandLineExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLineExecutor.class);

    private static final String DEFAULT_CONFIGURATION_PATH = "config/rat-config.xml";
    private static final String FILE_EXTENSION_NOT_SUPPORTED = "File extension \"%s\" is not supported";

    /**
     * The main entry point of the application.
     * 
     * @param args
     *            the passed arguments.
     */
    public static void main(String[] args) {
        new CommandLineExecutor().execute(args, RuleEngine.JAVA);
    }

    /**
     * This function performs the parsing, importing, analysing and exporting of
     * a document via the command line by the RAT application.
     * 
     * <p>
     * The function uses the default configuration file, supplied by the
     * assembly.
     * 
     * @param args
     *            the command line arguments
     * @param ruleEngine
     *            specifies which rule sets are taken into account.
     */
    public void execute(String[] args, RuleEngine ruleEngine) {
        execute(args, ruleEngine, DEFAULT_CONFIGURATION_PATH);
    }

    /**
     * This function performs the parsing, importing, analysing and exporting of
     * a document via the command line by the RAT application.
     * 
     * @param args
     *            the command line arguments
     * @param ruleEngine
     *            specifies which rule sets are taken into account.
     * @param defaultConfigurationPath
     *            the default configuration path to the configuration file.
     */
    public void execute(String[] args, RuleEngine ruleEngine, String defaultConfigurationPath) {
        try {
            ArgumentsModel arguments = CommandLineParser.parse(args);

            if (arguments != null) {
                String configPath = arguments.getConfigurationPath();

                for (String filePath : arguments.getFilePaths()) {
                    LOGGER.info(String.format("The current file path is: %s", filePath));

                    String directoryPath = arguments.getOutputDirectory() != null ? arguments.getOutputDirectory()
                            : ImporterUtils.getDirectoryPathFromFilePath(filePath);
                    String fileName = ImporterUtils.getFileNameFromPath(filePath);
                    String fileExtension = ImporterUtils.detectFileExtension(filePath);

                    LOGGER.info(String.format("The current directory path is: %s", directoryPath));

                    // Import
                    ImporterService importer = ServiceLocator.getService(ImporterService.class, fileExtension);
                    if (importer == null) {
                        LOGGER.warn(String.format(FILE_EXTENSION_NOT_SUPPORTED, fileExtension));
                        continue;
                    }

                    byte[] data = ImporterUtils.readFile(filePath);
                    DocumentModel documentModel = importer.getDocumentModel(data);

                    if (documentModel == null) {
                        continue;
                    }

                    String xml = ConfigurationUtils.loadConfiguration(configPath, directoryPath,
                            defaultConfigurationPath);
                    ConfigurationModel configurationModel = ConfigurationUtils.parseConfiguration(xml);

                    // Pipeline
                    String language = UimaUtils.detectLanguage(documentModel.getText());
                    LOGGER.info(String.format("%s was detected as language.", language));
                    PipelineService pipeline = ServiceLocator.getService(PipelineService.class, language);
                    if (pipeline == null) {
                        LOGGER.warn(String.format("Language \"%s\" is not supported.", language));
                        continue;
                    }

                    JCas jCas = pipeline.process(documentModel.getText(), ruleEngine, configurationModel);

                    documentModel.setjCas(jCas);
                    List<RatAnomaly> detectedAnomalies = UimaUtils.getRatAnomalies(jCas);

                    // Check for redundancy and false positives
                    AnomalyValidator anomalyValidator = new AnomalyValidator();
                    anomalyValidator.filterAnomalies(detectedAnomalies);
                    anomalyValidator.validateAnomalies(documentModel, anomalyValidator.getDetectedAnomaliesToApply());

                    // Export document
                    ExporterService exporter = ServiceLocator.getService(ExporterService.class, fileExtension);
                    if (exporter == null) {
                        LOGGER.warn(String.format(FILE_EXTENSION_NOT_SUPPORTED, fileExtension));
                        continue;
                    }
                    List<RatAnomaly> appliedAnomalies = exporter.applyAnnotations(documentModel,
                            anomalyValidator.getDetectedAnomaliesToApply());

                    // Print findings
                    LOGGER.info(String.format("Applied anomalies: %s", appliedAnomalies.size()));
                    Map<String, Integer> findings = UimaUtils.getNumberOfAnomalyFindings(appliedAnomalies);

                    for (Map.Entry<String, Integer> entry : findings.entrySet()) {
                        String anomalyName = entry.getKey();
                        Integer occurrences = entry.getValue();
                        LOGGER.info(anomalyName + ": " + occurrences);
                    }

                    // Determine which anomalies to store in the document
                    anomalyValidator.prepareForNextAnalysis(documentModel, appliedAnomalies);
                    List<RatAnomalyModel> newPreviousAppliedComments = anomalyValidator
                            .getNewPreviousAppliedAnomalies();
                    List<RatAnomalyModel> falsePositives = anomalyValidator.getFalsePositiveAnomalies();
                    List<RatAnomalyModel> incorporatedImprovementProposals = anomalyValidator
                            .getIncorporatedAnomalies();

                    exporter.addRatAnomaliesToCustomXmlPart(documentModel, newPreviousAppliedComments, falsePositives,
                            incorporatedImprovementProposals);

                    // Ensure comments are deleted
                    Docx4jUtils.deleteCommentsByHashCode(documentModel.getWml(), incorporatedImprovementProposals);

                    exporter.exportDocument(documentModel, directoryPath, fileName, fileExtension);

                    // Export HTML report
                    StatisticExporter.exportStatisticHTML(jCas, newPreviousAppliedComments, falsePositives,
                            incorporatedImprovementProposals, configurationModel, directoryPath, fileName,
                            fileExtension);
                }
            }
        } catch (ParserException | ImportException | PipelineException | ExportException | IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}