package de.qaware.rat.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.exceptions.ParserException;
import de.qaware.rat.api.models.AnnotatorRuleModel;
import de.qaware.rat.api.models.ConfigurationModel;
import de.qaware.rat.api.models.QualityGateConfigModel;

/**
 * The ConfigurationImporter class loads and parses the configuration for the
 * analysis and the report of the application.
 * 
 * @author Matthias
 *
 */
public final class ConfigurationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationUtils.class);

    // String variables
    private static final String WARNING_THRESHOLD_STRING = "warning-threshold";
    private static final String ERROR_THRESHOLD_STRING = "error-threshold";

    private static final String EASY_WARNING_THRESHOLD_STRING = "easy-warning-threshold";
    private static final String HARD_WARNING_THRESHOLD_STRING = "hard-warning-threshold";
    private static final String EASY_ERROR_THRESHOLD_STRING = "easy-error-threshold";
    private static final String HARD_ERROR_THRESHOLD_STRING = "hard-error-threshold";

    // Required information for the quality gate
    private static final List<String> REQUIRED_ANOMALY_SEVERITIES = Arrays.asList("minor", "major", "critical");
    private static final List<String> REQUIRED_FORMULAS = Arrays.asList("flesch-reading-ease-amstad",
            "wiener-sachtextformel");
    private static final List<String> REQUIRED_STATISTICS = Arrays.asList("average-number-of-words-per-sentence",
            "average-number-of-syllables-per-word");

    private ConfigurationUtils() {
    }

    /**
     * This method loads the default configuration for the analysis and
     * statistic report.
     * 
     * @return the content of the configuration file as String.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static String loadConfiguration() throws IOException {
        return loadConfiguration(null, null, "config/rat-config.xml");
    }

    /**
     * This method loads the configuration from the given path.
     * 
     * @path the path to load the configuration from.
     * @return the content of the configuration file as String.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static String loadConfiguration(String path) throws IOException {
        return loadConfiguration(null, null, path);
    }

    /**
     * This method loads the configuration for the analysis and report.
     * 
     * <p>
     * First, the method looks for the configuration at the provided configPath
     * parameter. If this parameter is not provided, e.g. is null, or there is
     * no valid file at the location, the method will look in the directory path
     * of the file that is currently analysed for a file named "rat-config.xml".
     * 
     * <p>
     * If both ways fail to obtain a configuration file, the defaultConfig
     * parameter provided by the executor is considered.
     * 
     * <p>
     * In case the default configuration is not a regular file, the rat internal
     * configuration will be loaded.
     * 
     * @param configPath
     *            the path to the configuration file.
     * @param filePath
     *            the file path of the document that is analysed.
     * @param defaultPath
     *            the default path to the configuration file.
     * @return the content of the configuration file as String.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static String loadConfiguration(String configPath, String filePath, String defaultPath) throws IOException {
        File config;

        if (configPath != null) {
            config = new File(configPath);

            if (config.isFile()) {
                LOGGER.info("Load configuration from configPath.");
                return ImporterUtils.readFileAsString(config.getAbsolutePath());
            }
        }

        if (filePath != null) {
            config = new File(ImporterUtils.getDirectoryPathFromFilePath(filePath) + "rat-config.xml");

            if (config.isFile()) {
                LOGGER.info("Load configuration from the directoryPath.");
                return ImporterUtils.readFileAsString(config.getAbsolutePath());
            }
        }

        if (defaultPath != null) {
            config = new File(defaultPath);

            if (config.isFile()) {
                LOGGER.info(String.format("Load configuration from defaultPath: %s", defaultPath));
                return ImporterUtils.readFileAsString(config.getAbsolutePath());
            }
        }

        LOGGER.warn("No configuration file is supplied! The RAT internal configuration is loaded.");
        return ClassPathUtils.loadAsString("rat-config.xml");
    }

    /**
     * This method parses the content of a configuration file.
     * 
     * @param config
     *            the String content of a configuration file.
     * @return the configuration parameters encapsulated in the model class
     *         ConfigurationModel
     * @throws ParserException
     *             if an error occurs during the parsing of the configuration.
     */
    public static ConfigurationModel parseConfiguration(String config) throws ParserException {
        Document xml = Jsoup.parse(config);
        ConfigurationModel configurationModel = new ConfigurationModel();

        configurationModel = parseParameters(configurationModel, xml);
        configurationModel = parseQualityGate(configurationModel, xml);

        return configurationModel;
    }

    private static ConfigurationModel parseParameters(ConfigurationModel configurationModel, Document xml)
            throws ParserException {
        List<AnnotatorRuleModel> anomalyRuleModels = new ArrayList<AnnotatorRuleModel>();
        List<String> keywordList = new ArrayList<String>();

        // Keywords
        try {
            Elements keywords = xml.select("keyword");

            for (Element keyword : keywords) {
                keywordList.add(keyword.text());
            }
        } catch (Exception e) {
            throw new ParserException("Parsing the keywords failed.", e);
        }

        // Set keyword list
        configurationModel.setKeywords(keywordList);

        // Anomalies
        try {
            Elements anomalyRules = xml.select("anomaly-rule");

            for (Element anomalyRule : anomalyRules) {

                String name = anomalyRule.select("name").text();
                String severity = "";
                String severityFromConfig = anomalyRule.select("severity").text();
                if (REQUIRED_ANOMALY_SEVERITIES.contains(severityFromConfig.toLowerCase())) {
                    severity = severityFromConfig;
                } else {
                    throw new ParserException(
                            String.format("The severity: '%s' is not supported.", severityFromConfig));
                }
                int threshold;
                try {
                    threshold = Integer.parseInt(anomalyRule.select("threshold").text());
                } catch (NumberFormatException e) {
                    threshold = 0;
                }
                boolean enabled = true;
                if (!anomalyRule.select("enabled").text().equals("true")) {
                    enabled = false;
                }

                LOGGER.debug("Rule " + name + " enabled: " + enabled);

                anomalyRuleModels.add(new AnnotatorRuleModel(name, severity, threshold, enabled));
            }
        } catch (Exception e) {
            throw new ParserException("Parsing the anomaly rules failed.", e);
        }

        // Set anomaly rule model
        configurationModel.setAnomalyRuleModels(anomalyRuleModels);

        return configurationModel;
    }

    private static ConfigurationModel parseQualityGate(ConfigurationModel configurationModel, Document xml)
            throws ParserException {
        Map<String, QualityGateConfigModel> measurementModels = new HashMap<String, QualityGateConfigModel>();

        // Anomalies
        try {
            Elements anomalies = xml.select("quality-gate anomaly");

            for (Element anomaly : anomalies) {
                QualityGateConfigModel qualityGate = new QualityGateConfigModel(anomaly.select("severity").text(), 0,
                        Double.parseDouble(anomaly.select(WARNING_THRESHOLD_STRING).text()), 0,
                        Double.parseDouble(anomaly.select(ERROR_THRESHOLD_STRING).text()));

                measurementModels.put(qualityGate.getName(), qualityGate);
            }
        } catch (Exception e) {
            throw new ParserException("Parsing the anomaly quality gate information failed.", e);
        }

        // Formulas
        try {
            Elements formulas = xml.select("formula");

            for (Element formula : formulas) {
                QualityGateConfigModel qualityGate = new QualityGateConfigModel(formula.select("name").text(),
                        Double.parseDouble(formula.select(EASY_WARNING_THRESHOLD_STRING).text()),
                        Double.parseDouble(formula.select(HARD_WARNING_THRESHOLD_STRING).text()),
                        Double.parseDouble(formula.select(EASY_ERROR_THRESHOLD_STRING).text()),
                        Double.parseDouble(formula.select(HARD_ERROR_THRESHOLD_STRING).text()));

                measurementModels.put(qualityGate.getName(), qualityGate);
            }
        } catch (Exception e) {
            throw new ParserException("Parsing the formula quality gate information failed.", e);
        }

        // Statistics
        try {
            Elements statistics = xml.select("statistic");

            for (Element statistic : statistics) {
                QualityGateConfigModel qualityGate = new QualityGateConfigModel(statistic.select("name").text(),
                        Double.parseDouble(statistic.select(EASY_WARNING_THRESHOLD_STRING).text()),
                        Double.parseDouble(statistic.select(HARD_WARNING_THRESHOLD_STRING).text()),
                        Double.parseDouble(statistic.select(EASY_ERROR_THRESHOLD_STRING).text()),
                        Double.parseDouble(statistic.select(HARD_ERROR_THRESHOLD_STRING).text()));

                measurementModels.put(qualityGate.getName(), qualityGate);
            }
        } catch (Exception e) {
            throw new ParserException("Parsing the statistics quality gate information failed.", e);
        }

        // Set measurement model
        configurationModel.setQualityGateInformation(measurementModels);

        // Validate if required information are present
        validateQualityGateInformation(configurationModel);

        return configurationModel;
    }

    private static void validateQualityGateInformation(ConfigurationModel configurationModel) throws ParserException {
        // Validate anomaly severities
        for (String severity : REQUIRED_ANOMALY_SEVERITIES) {
            if (configurationModel.getQualityGateInformation().get(severity) == null) {
                throw new ParserException(
                        String.format("The quality gate information for '%s' severity was not found.", severity));
            }
        }

        // Validate formulas
        for (String formula : REQUIRED_FORMULAS) {
            if (configurationModel.getQualityGateInformation().get(formula) == null) {
                throw new ParserException(
                        String.format("The quality gate information for '%s' formula was not found.", formula));
            }
        }

        // Validate statistics
        for (String statistic : REQUIRED_STATISTICS) {
            if (configurationModel.getQualityGateInformation().get(statistic) == null) {
                throw new ParserException(
                        String.format("The quality gate information for '%s' statistic was not found.", statistic));
            }
        }
    }
}