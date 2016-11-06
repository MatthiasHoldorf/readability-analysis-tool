package de.qaware.rat.statistics;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.jcas.JCas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.models.ConfigurationModel;
import de.qaware.rat.api.models.QualityGateConfigModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.common.ClassPathUtils;
import de.qaware.rat.common.CollectionUtils;

/**
 * The {@code StatisticExporter} class provides functions to create the HTML
 * report yielding statistics of the analysed document.
 * 
 * @author Matthias
 *
 */
public final class StatisticExporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticExporter.class);

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String DEFAULT_LOCATION_INPUT = "html/statistic-template.html";

    private static final String PERCENTAGE_FORMAT = "%.2f";
    private static final String TD_HTML_TAG = "<td>%s</td>";

    private static final String FLESCH_LINK = "https://de.wikipedia.org/wiki/Lesbarkeitsindex#Flesch-Reading-Ease";
    private static final String WIENER_LINK = "https://de.wikipedia.org/wiki/Lesbarkeitsindex#Wiener_Sachtextformel";

    // Red colour
    private static final String FAILED = "data:image/gif;base64,R0lGODlhAQABAPAA AP8AAP ///yH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==";
    // Orange colour
    private static final String WARNING = "data:image/gif;base64,R0lGODlhAQABAPA1 0P60BF ///yH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==";
    // Blue colour
    private static final String PASSED = "data:image/gif;base64,R0lGODlhAQABAPA FPC91m ////yH5BAAAAAAALAAAAAABAAEAAAICRAEAOw==";

    private static String formulasQualityGate = FAILED;
    private static String statisticQualityGate = FAILED;
    private static String anomaliesQualityGate = FAILED;

    private StatisticExporter() {
    }

    /**
     * Export the calculated text statistics into an HTML-Report.
     * 
     * <p>
     * The name of the report is composed of the filePath and a "-rat" suffix.
     * 
     * <p>
     * The .html-File is stored in the same directory as the original document.
     * 
     * @param jCas
     *            the jCas storing the analysis of the document text.
     * @param currentAnomalies
     *            currently applied anomalies.
     * @param falsePositives
     *            as false positive marked anomalies.
     * @param incorporatedAnomalies
     *            anomalies that were incorporated.
     * @param configurationModel
     *            the configuration for the quality gate and thresholds for the
     *            statistics.
     * @param directoryName
     *            the directory name of the analysed original document.
     * @param fileName
     *            the file name of the analysed original document.
     * @param fileExtension
     *            the file extension of the analysed original document.
     * @throws IOException
     *             if an I/O error occurs during the export.
     */
    public static void exportStatisticHTML(JCas jCas, List<RatAnomalyModel> currentAnomalies,
            List<RatAnomalyModel> falsePositives, List<RatAnomalyModel> incorporatedAnomalies,
            ConfigurationModel configurationModel, String directoryName, String fileName, String fileExtension)
            throws IOException {
        exportStatisticHTML(jCas, currentAnomalies, falsePositives, incorporatedAnomalies, configurationModel,
                DEFAULT_LOCATION_INPUT, directoryName, fileName, fileExtension);
    }

    /**
     * Export the calculated text statistics into an HTML-Report.
     * 
     * <p>
     * The name of the report is composed of the filePath and a "-rat" suffix.
     * 
     * <p>
     * The .html-File is stored in the same directory as the original document.
     * 
     * @param jCas
     *            the jCas storing the analysis of the document text.
     * @param currentAnomalies
     *            currently applied anomalies.
     * @param falsePositives
     *            as false positive marked anomalies.
     * @param incorporatedAnomalies
     *            anomalies that were incorporated.
     * @param configurationModel
     *            the configuration for the quality gate and thresholds for the
     *            statistics.
     * @param templateLocation
     *            the location of the HTML template to build the report.
     * @param directoryName
     *            the directory name of the analysed original document.
     * @param fileName
     *            the file name of the analysed original document.
     * @param fileExtension
     *            the file extension of the analysed original document.
     * @throws IOException
     *             if an I/O error occurs during the export.
     */
    public static void exportStatisticHTML(JCas jCas, List<RatAnomalyModel> currentAnomalies,
            List<RatAnomalyModel> falsePositives, List<RatAnomalyModel> incorporatedAnomalies,
            ConfigurationModel configurationModel, String templateLocation, String directoryName, String fileName,
            String fileExtension) throws IOException {
        String htmlString = ClassPathUtils.loadAsString(templateLocation, DEFAULT_CHARSET);

        htmlString = htmlString.replace("$file-name", "<small>" + fileName + "." + fileExtension + "</small>");
        htmlString = exportReadabilityFormulas(htmlString, jCas, configurationModel);
        htmlString = exportReadabilityStatistics(htmlString, jCas, configurationModel);
        htmlString = exportReadabilityAnomalies(htmlString, currentAnomalies, falsePositives, incorporatedAnomalies,
                configurationModel);
        // htmlString = exportDocumentText(htmlString, jCas, currentAnomalies);
        htmlString = qualityGateCheck(htmlString);

        String exportFilePath = null;
        if (fileName.endsWith("-rat")) {
            exportFilePath = directoryName + fileName + ".html";
        } else {
            exportFilePath = directoryName + fileName + "-rat.html";
        }

        FileUtils.writeStringToFile(new File(exportFilePath), htmlString, DEFAULT_CHARSET);
        LOGGER.info("Export report to: " + exportFilePath);
        LOGGER.info("REPORT EXPORT DONE");
    }

    private static String qualityGateCheck(String htmlString) {
        String newHtmlString = htmlString;
        String qualityGate = FAILED;
        String qualityGateMessage = "";

        newHtmlString = newHtmlString.replace("$readability-anomalies-circle", anomaliesQualityGate);
        newHtmlString = newHtmlString.replace("$readability-formulas-circle", formulasQualityGate);
        newHtmlString = newHtmlString.replace("$readability-statistics-circle", statisticQualityGate);

        if (anomaliesQualityGate.equals(FAILED) || formulasQualityGate.equals(FAILED)
                || statisticQualityGate.equals(FAILED)) {
            qualityGate = FAILED;
            qualityGateMessage = "The text failed the quality gate!";
        } else if (anomaliesQualityGate.equals(WARNING) || formulasQualityGate.equals(WARNING)
                || statisticQualityGate.equals(WARNING)) {
            qualityGate = WARNING;
            qualityGateMessage = "The text passed the quality gate with warnings!";
        } else {
            qualityGate = PASSED;
            qualityGateMessage = "The text passed the quality gate!";
        }

        newHtmlString = newHtmlString.replace("$quality-gate-circle", qualityGate);
        newHtmlString = newHtmlString.replace("$value.quality-gate-passed", qualityGateMessage);

        return newHtmlString;
    }

    private static String exportReadabilityFormulas(String htmlString, JCas jCas,
            ConfigurationModel configurationModel) {
        List<String> formulas = new ArrayList<String>();
        String newHtmlString = htmlString;

        double fleschScore = ReadabilityFormula.calculateFleschReadingEaseAmstad(jCas);
        double wienerScore = ReadabilityFormula.calculateWienerSachtextformel(jCas);

        QualityGateConfigModel fleschScoreMeasure = configurationModel.getQualityGateInformation()
                .get("flesch-reading-ease-amstad");
        QualityGateConfigModel wienerScoreMeasure = configurationModel.getQualityGateInformation()
                .get("wiener-sachtextformel");

        formulas.add(createHtmlTableEntryString(true,
                "<a href='" + FLESCH_LINK + "' target='_blank'>Flesch-Reading-Ease-Amstad</a>",
                String.format(PERCENTAGE_FORMAT, fleschScore),
                "(" + fleschScoreMeasure.getEasyWarningThreshold() + ") " + fleschScoreMeasure.getEasyErrorThreshold(),
                fleschScoreMeasure.getHardErrorThreshold() + " (" + fleschScoreMeasure.getHardWarningThreshold()
                        + ")"));
        formulas.add(createHtmlTableEntryString(true,
                "<a href='" + WIENER_LINK + "' target='_blank'>1. Wiener Sachtextformel</a>",
                String.format(PERCENTAGE_FORMAT, wienerScore),
                wienerScoreMeasure.getEasyErrorThreshold() + " (" + wienerScoreMeasure.getEasyWarningThreshold() + ")",
                "(" + wienerScoreMeasure.getHardWarningThreshold() + ") "
                        + wienerScoreMeasure.getHardErrorThreshold()));

        newHtmlString = newHtmlString.replace("$value.flesch", String.format(PERCENTAGE_FORMAT, fleschScore));
        newHtmlString = newHtmlString.replace("$value.wiener", String.format(PERCENTAGE_FORMAT, wienerScore));

        // Quality Gate
        if ((fleschScore < fleschScoreMeasure.getHardErrorThreshold()
                || wienerScore > wienerScoreMeasure.getHardErrorThreshold())
                || (fleschScore > fleschScoreMeasure.getEasyErrorThreshold()
                        || wienerScore < wienerScoreMeasure.getEasyErrorThreshold())) {
            formulasQualityGate = FAILED;
        } else if ((fleschScore < fleschScoreMeasure.getHardWarningThreshold()
                || wienerScore > wienerScoreMeasure.getHardWarningThreshold())
                || (fleschScore > fleschScoreMeasure.getEasyWarningThreshold()
                        || wienerScore < wienerScoreMeasure.getEasyWarningThreshold())) {
            formulasQualityGate = WARNING;
        } else {
            formulasQualityGate = PASSED;
        }

        return newHtmlString.replace("$readability-formulas-table-body", addIndexToHtmlTableString(formulas));
    }

    private static String exportReadabilityStatistics(String htmlString, JCas jCas,
            ConfigurationModel configurationModel) {
        List<String> statistics = new ArrayList<String>();
        String newHtmlString = htmlString;
        QualityGateConfigModel avgWordsPerSentenceMeasures = configurationModel.getQualityGateInformation()
                .get("average-number-of-words-per-sentence");
        QualityGateConfigModel avgSyllablesPerWordMeasure = configurationModel.getQualityGateInformation()
                .get("average-number-of-syllables-per-word");

        String readingTime = TextStatistic.getReadingTime(jCas);
        String speakingTime = TextStatistic.getSpeakingTime(jCas);
        int numberOfSentecesInDocument = TextStatistic.numberOfSentencesInDocument(jCas);
        int numberOfWordsInDocument = TextStatistic.numberOfWordsInDocument(jCas);
        int numberOfSyllablesInDocument = TextStatistic.numberOfSyllablesInDocument(jCas);
        int numberOfCharactersInDocument = TextStatistic.numberOfCharactersInDocument(jCas);
        double avgWordsPerSentence = TextStatistic.averageNumberOfWordsPerSentence(jCas);
        double avgSyllablesPerSentence = TextStatistic.averageNumberOfSyllablesPerSentence(jCas);
        double avgCharactersPerSentence = TextStatistic.averageNumberOfCharactersPerSentence(jCas);
        double avgSyllablesPerWord = TextStatistic.averageNumberOfSyllablesPerWord(jCas);
        double avgCharactersPerWord = TextStatistic.averageNumberOfCharactersPerWord(jCas);
        // String longestSentence = TextStatistic.getLongestSentence(jCas);
        // String longestWordBySyllables =
        // TextStatistic.getLongestWordBySyllables(jCas);
        // String longestWordByCharacters =
        // TextStatistic.getLongestWordByCharacters(jCas);
        Map<String, Float> mostUsedNouns = TextStatistic.getXMostUsedWordsByPOSValueType(jCas, "NN", 3);
        Map<String, Float> mostUsedVerbs = TextStatistic.getXMostUsedWordsByPOSValueType(jCas, "VV", 3);
        Map<String, Float> mostUsedAdjectives = TextStatistic.getXMostUsedWordsByPOSValueType(jCas, "ADJ", 3);
        Map<String, Float> mostUsedConjunctions = TextStatistic.getXMostUsedWordsByPOSValueType(jCas, "CONJ", 3);
        float percentageOfNounsInText = TextStatistic.getPercentageOfUsedWordsByPOSType(jCas, "NN");
        float percentageOfVerbsInText = TextStatistic.getPercentageOfUsedWordsByPOSType(jCas, "V");
        float percentageOfAdjectivesInText = TextStatistic.getPercentageOfUsedWordsByPOSType(jCas, "ADJ");
        float percentageOfConjunctionsInText = TextStatistic.getPercentageOfUsedWordsByPOSType(jCas, "CONJ");

        statistics.add(createHtmlTableEntryString(true, "Reading Time", readingTime, "", ""));
        statistics.add(createHtmlTableEntryString(true, "Speaking Time", speakingTime, "", ""));
        statistics.add(createHtmlTableEntryString(true, "Number of Sentences",
                String.valueOf(numberOfSentecesInDocument), "", ""));
        statistics.add(
                createHtmlTableEntryString(true, "Number of Words", String.valueOf(numberOfWordsInDocument), "", ""));
        statistics.add(createHtmlTableEntryString(true, "Number of Syllables",
                String.valueOf(numberOfSyllablesInDocument), "", ""));
        statistics.add(createHtmlTableEntryString(true, "Number of Characters",
                String.valueOf(numberOfCharactersInDocument), "", ""));
        statistics.add(createHtmlTableEntryString(true, "Average Number of Words per Sentence",
                String.format(PERCENTAGE_FORMAT, avgWordsPerSentence),
                avgWordsPerSentenceMeasures.getEasyErrorThreshold() + " ("
                        + avgWordsPerSentenceMeasures.getEasyWarningThreshold() + ")",
                "(" + avgWordsPerSentenceMeasures.getHardWarningThreshold() + ") "
                        + avgWordsPerSentenceMeasures.getHardErrorThreshold()));
        statistics.add(createHtmlTableEntryString(true, "Average Number of Syllables per Sentence",
                String.format(PERCENTAGE_FORMAT, avgSyllablesPerSentence), "", ""));
        statistics.add(createHtmlTableEntryString(true, "Average Number of Characters per Sentence",
                String.format(PERCENTAGE_FORMAT, avgCharactersPerSentence), "", ""));
        statistics.add(createHtmlTableEntryString(true, "Average Number of Syllables per Word",
                String.format(PERCENTAGE_FORMAT, avgSyllablesPerWord),
                avgSyllablesPerWordMeasure.getEasyErrorThreshold() + " ("
                        + avgSyllablesPerWordMeasure.getEasyWarningThreshold() + ")",
                "(" + avgSyllablesPerWordMeasure.getHardWarningThreshold() + ") "
                        + avgSyllablesPerWordMeasure.getHardErrorThreshold()));
        statistics.add(createHtmlTableEntryString(true, "Average Number of Characters per Word",
                String.format(PERCENTAGE_FORMAT, avgCharactersPerWord), "", ""));
        // statistics.add(createHtmlTableEntryString("Longest Sentence", "",
        // longestSentence, ""));
        // statistics.add(createHtmlTableEntryString("Longest Word by
        // Syllables", "", longestWordBySyllables, ""));
        // statistics.add(createHtmlTableEntryString("Longest Word by
        // Characters", "", longestWordByCharacters, ""));
        statistics.add(createHtmlTableEntryString(false, "Most used Nouns",
                createStringForMostUsedWord(mostUsedNouns, "Noun"), "", ""));
        statistics.add(createHtmlTableEntryString(false, "Most used Verbs",
                createStringForMostUsedWord(mostUsedVerbs, "Verb"), "", ""));
        statistics.add(createHtmlTableEntryString(false, "Most used Adjectives",
                createStringForMostUsedWord(mostUsedAdjectives, "Adjective"), "", ""));
        statistics.add(createHtmlTableEntryString(false, "Most used Conjunctions",
                createStringForMostUsedWord(mostUsedConjunctions, "Conjunction"), "", ""));
        statistics.add(createHtmlTableEntryString(true, "Percentage of Nouns in Text",
                String.format(PERCENTAGE_FORMAT, percentageOfNounsInText * 100) + "%", "", ""));
        statistics.add(createHtmlTableEntryString(true, "Percentage of Verbs in Text",
                String.format(PERCENTAGE_FORMAT, percentageOfVerbsInText * 100) + "%", "", ""));
        statistics.add(createHtmlTableEntryString(true, "Percentage of Adjectives in Text",
                String.format(PERCENTAGE_FORMAT, percentageOfAdjectivesInText * 100) + "%", "", ""));
        statistics.add(createHtmlTableEntryString(true, "Percentage of Conjunctions in Text",
                String.format(PERCENTAGE_FORMAT, percentageOfConjunctionsInText * 100) + "%", "", ""));

        for (String keyword : configurationModel.getKeywords()) {
            statistics.add(createHtmlTableEntryString(true, "Percentage of the keyword <b>" + keyword + "</b>",
                    String.format(PERCENTAGE_FORMAT, TextStatistic.getPercentageOfKeyword(jCas, keyword) * 100) + "%",
                    "", ""));
        }

        newHtmlString = newHtmlString.replace("$value.readingtime", readingTime);
        newHtmlString = newHtmlString.replace("$value.average-words-per-sentence",
                String.format(PERCENTAGE_FORMAT, avgWordsPerSentence));

        // Quality Gate
        if (avgWordsPerSentence > avgWordsPerSentenceMeasures.getHardErrorThreshold()
                || avgSyllablesPerWord > avgSyllablesPerWordMeasure.getHardErrorThreshold()) {
            statisticQualityGate = FAILED;
        } else if (avgWordsPerSentence > avgWordsPerSentenceMeasures.getHardWarningThreshold()
                || avgSyllablesPerWord > avgSyllablesPerWordMeasure.getHardWarningThreshold()) {
            statisticQualityGate = WARNING;
        } else {
            statisticQualityGate = PASSED;
        }

        return newHtmlString.replace("$readability-statistics-table-body", addIndexToHtmlTableString(statistics));
    }

    private static String exportReadabilityAnomalies(String htmlString, List<RatAnomalyModel> currentAnomalies,
            List<RatAnomalyModel> falsePositives, List<RatAnomalyModel> incorporatedAnomalies,
            ConfigurationModel configurationModel) {
        String newHtmlString = htmlString;
        List<String> anomalies = new ArrayList<String>();

        int amountCriticals = 0;
        int amountMajors = 0;
        int amountMinors = 0;

        for (RatAnomalyModel anomaly : currentAnomalies) {
            switch (anomaly.getSeverity()) {
            case "Critical":
                amountCriticals++;
                break;
            case "Major":
                amountMajors++;
                break;
            case "Minor":
                amountMinors++;
                break;
            default:
                break;
            }
        }

        newHtmlString = newHtmlString.replace("$readability-current-anomalies-table-body",
                getHtmlTableStringFromRatAnomalyModels(currentAnomalies));
        newHtmlString = newHtmlString.replace("$readability-false-positive-anomalies-table-body",
                getHtmlTableStringFromRatAnomalyModels(falsePositives));
        newHtmlString = newHtmlString.replace("$readability-incorporated-anomalies-table-body",
                getHtmlTableStringFromRatAnomalyModels(incorporatedAnomalies));

        newHtmlString = newHtmlString.replace("$amount.critical", String.valueOf(amountCriticals));
        newHtmlString = newHtmlString.replace("$amount.major", String.valueOf(amountMajors));
        newHtmlString = newHtmlString.replace("$amount.minor", String.valueOf(amountMinors));

        // Get quality gate information
        QualityGateConfigModel criticalThresholds = configurationModel.getQualityGateInformation().get("critical");
        QualityGateConfigModel majorThresholds = configurationModel.getQualityGateInformation().get("major");
        QualityGateConfigModel minorThresholds = configurationModel.getQualityGateInformation().get("minor");

        anomalies.add(createHtmlTableEntryString(true, "Criticals", String.valueOf(amountCriticals),
                String.valueOf(criticalThresholds.getHardWarningThreshold()),
                String.valueOf(criticalThresholds.getHardErrorThreshold())));
        anomalies.add(createHtmlTableEntryString(true, "Majors", String.valueOf(amountMajors),
                String.valueOf(majorThresholds.getHardWarningThreshold()),
                String.valueOf(majorThresholds.getHardErrorThreshold())));
        anomalies.add(createHtmlTableEntryString(true, "Minors", String.valueOf(amountMinors),
                String.valueOf(minorThresholds.getHardWarningThreshold()),
                String.valueOf(minorThresholds.getHardErrorThreshold())));

        newHtmlString = newHtmlString.replace("$readability-anomalies-amount-table-body",
                addIndexToHtmlTableString(anomalies));

        // Quality Gate
        if (amountCriticals > criticalThresholds.getHardErrorThreshold()
                || amountMajors > majorThresholds.getHardErrorThreshold()
                || amountMinors > minorThresholds.getHardErrorThreshold()) {
            anomaliesQualityGate = FAILED;
        } else if (amountCriticals > criticalThresholds.getHardWarningThreshold()
                || amountMajors > majorThresholds.getHardWarningThreshold()
                || amountMinors > minorThresholds.getHardWarningThreshold()) {
            anomaliesQualityGate = WARNING;
        } else {
            anomaliesQualityGate = PASSED;
        }

        return newHtmlString;
    }

    @SuppressWarnings("unused")
    private static String exportDocumentText(String htmlString, JCas jCas, List<RatAnomalyModel> currentAnomalies) {
        String documentText = jCas.getDocumentText();
        String newHtmlString = htmlString;

        int offset = 0;
        for (RatAnomalyModel anomaly : currentAnomalies) {
            documentText = documentText.substring(0, anomaly.getBegin() + offset)
                    + "<mark data-toggle='tooltip' title='" + anomaly.getAnomalyName() + "' style='cursor: pointer;'>"
                    + anomaly.getCoveredText() + "</mark>"
                    + documentText.substring(anomaly.getEnd() + offset, documentText.length());

            offset += 69 + anomaly.getAnomalyName().length();
        }

        return newHtmlString.replace("$document-text", documentText);
    }

    private static String createStringForMostUsedWord(Map<String, Float> map, String posName) {
        StringBuilder sb = new StringBuilder();

        sb.append("<ul>");
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            String word = entry.getKey();
            Float percentage = entry.getValue();
            sb.append("<li> The " + posName + " <b>" + word + "</b> is used "
                    + String.format(PERCENTAGE_FORMAT, percentage * 100) + "% among other " + posName + "s</li>");
        }
        sb.append("</ul>");

        return sb.toString();
    }

    private static String createHtmlTableEntryStringWithIndex(int hashCode, String... values) {
        StringBuilder sb = new StringBuilder();

        sb.append("<tr data-hash-code='" + hashCode + "'>");
        for (String value : values) {
            sb.append(String.format(TD_HTML_TAG, value));
        }
        sb.append("</tr>");

        return sb.toString();
    }

    private static String createHtmlTableEntryString(boolean aligned, String... values) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            if (i >= 1 && aligned) {
                sb.append("<td style='text-align: center;'>" + values[i] + "</td>");
            } else {
                sb.append(String.format(TD_HTML_TAG, values[i]));
            }
        }

        return sb.toString();
    }

    private static String addIndexToHtmlTableString(List<String> statistics) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < statistics.size(); i++) {
            sb.append("<tr><td>" + (i + 1) + "</td>" + statistics.get(i) + "</tr>");
        }

        return sb.toString();
    }

    private static String getHtmlTableStringFromRatAnomalyModels(List<RatAnomalyModel> anomalies) {
        List<String> resultHtml = new ArrayList<String>();

        for (int i = 0; i < anomalies.size(); i++) {
            RatAnomalyModel anomaly = anomalies.get(i);

            if (anomaly.getViolations() != null) {
                resultHtml.add(createHtmlTableEntryStringWithIndex(anomaly.getHashCode(), String.valueOf(i + 1),
                        anomaly.getAnomalyName(), anomaly.getSeverity(),
                        CollectionUtils.printStringList(anomaly.getViolations()), anomaly.getSentence()));
            } else {
                resultHtml.add(createHtmlTableEntryStringWithIndex(anomaly.getHashCode(), String.valueOf(i + 1),
                        anomaly.getAnomalyName(), anomaly.getSeverity(), anomaly.getCoveredText(),
                        anomaly.getSentence()));
            }
        }

        StringBuilder sb = new StringBuilder();
        for (String string : resultHtml) {
            sb.append(string);
        }

        return sb.toString();
    }
}