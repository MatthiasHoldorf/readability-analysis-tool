package de.qaware.rat.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.type.RatAnomaly;

/**
 * The {@code AnomalyValidator} class detects redundant, false positives and
 * incorporated anomalies based on applied anomalies, previous applied anomalies
 * and detected anomalies.
 * 
 * @author Matthias
 *
 */
public final class AnomalyValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnomalyValidator.class);

    private static final List<String> FILTER_ANOMALIES_BY = Arrays.asList("REF", "www", "/", "\\", "http", "ARABIC");

    private List<RatAnomaly> detectedAnomaliesToApply = null;
    private List<RatAnomalyModel> falsePositiveAnomalies = null;
    private List<RatAnomalyModel> incorporatedAnomalies = null;
    private List<RatAnomalyModel> redundantAnomalies = null;
    private List<RatAnomalyModel> newPreviousAppliedAnomalies = null;

    public List<RatAnomaly> getDetectedAnomaliesToApply() {
        return detectedAnomaliesToApply;
    }

    public void setDetectedAnomaliesToApply(List<RatAnomaly> detectedAnomaliesToApply) {
        this.detectedAnomaliesToApply = detectedAnomaliesToApply;
    }

    public List<RatAnomalyModel> getFalsePositiveAnomalies() {
        return falsePositiveAnomalies;
    }

    public void setFalsePositiveAnomalies(List<RatAnomalyModel> falsePositiveAnomalies) {
        this.falsePositiveAnomalies = falsePositiveAnomalies;
    }

    public List<RatAnomalyModel> getIncorporatedAnomalies() {
        return incorporatedAnomalies;
    }

    public void setIncorporatedAnomalies(List<RatAnomalyModel> incorporatedAnomalies) {
        this.incorporatedAnomalies = incorporatedAnomalies;
    }

    public List<RatAnomalyModel> getRedundantAnomalies() {
        return redundantAnomalies;
    }

    public void setRedundantAnomalies(List<RatAnomalyModel> redundantAnomalies) {
        this.redundantAnomalies = redundantAnomalies;
    }

    public List<RatAnomalyModel> getNewPreviousAppliedAnomalies() {
        return newPreviousAppliedAnomalies;
    }

    public void setNewPreviousAppliedAnomalies(List<RatAnomalyModel> newPreviousAppliedAnomalies) {
        this.newPreviousAppliedAnomalies = newPreviousAppliedAnomalies;
    }

    /**
     * Filter anomalies that apply to wrong parsed text passages.
     * 
     * @param detectedAnomalies
     *            the detected anomalies to filter.
     */
    public void filterAnomalies(List<RatAnomaly> detectedAnomalies) {
        // Defensive copy of detected anomalies
        detectedAnomaliesToApply = new ArrayList<RatAnomaly>(detectedAnomalies);

        for (Iterator<RatAnomaly> iterator = detectedAnomaliesToApply.iterator(); iterator.hasNext();) {
            RatAnomaly detectedAnomaly = iterator.next();

            if (FILTER_ANOMALIES_BY.contains(detectedAnomaly.getSentence())) {
                LOGGER.debug(String.format("Remove anomaly: %s", detectedAnomaly.getSentence()));
                iterator.remove();
            }

        }
    }

    /**
     * Detect redundant and false positive anomalies.
     * 
     * @param documentModel
     *            the document model to retrieve information from previous
     *            analysis.
     * @param detectedAnomalies
     *            the newly detected anomalies that are going to be applied.
     */
    public void validateAnomalies(DocumentModel documentModel, List<RatAnomaly> detectedAnomalies) {
        // Defensive copy of detected anomalies
        detectedAnomaliesToApply = new ArrayList<RatAnomaly>(detectedAnomalies);
        LOGGER.debug("Detected anomalies: " + detectedAnomaliesToApply.size());

        // Currently annotated anomalies
        List<Integer> appliedCommentHashCodes = new ArrayList<Integer>(documentModel.getAppliedCommentsHashCodes());
        LOGGER.debug("Currently annotated anomalies: " + appliedCommentHashCodes.size());

        // Anomalies that were applied in the previous analysis
        List<RatAnomalyModel> previousAppliedAnomalies = new ArrayList<RatAnomalyModel>(
                documentModel.getPreviousAppliedComments());
        LOGGER.debug("Previous applied anomalies: " + previousAppliedAnomalies.size());

        // Redundant anomalies
        redundantAnomalies = new ArrayList<RatAnomalyModel>();

        // Determine redundant anomalies via hash code comparison
        for (int previousHashCode : appliedCommentHashCodes) {
            for (Iterator<RatAnomaly> iterator = detectedAnomaliesToApply.iterator(); iterator.hasNext();) {
                RatAnomaly detectedAnomaly = iterator.next();
                if (previousHashCode == detectedAnomaly.getHashCode()) {
                    iterator.remove();
                    redundantAnomalies.add(UimaUtils.getRatAnomalyModelFromRatAnomaly(detectedAnomaly));
                }
            }
        }

        // Remove redundant anomalies from detected anomalies to apply
        LOGGER.debug("Redundant anomalies: " + redundantAnomalies.size());
        LOGGER.debug("Detected anomalies after removal of redundant anomalies: " + detectedAnomaliesToApply.size());

        // False positive anomalies
        falsePositiveAnomalies = new ArrayList<RatAnomalyModel>(documentModel.getFalsePositives());

        // Determine new false positive anomalies and remove them from detected
        // anomalies to apply
        for (RatAnomalyModel previousAppliedAnomaly : previousAppliedAnomalies) {
            for (Iterator<RatAnomaly> iterator = detectedAnomaliesToApply.iterator(); iterator.hasNext();) {
                RatAnomaly detectedAnomaly = iterator.next();
                if (previousAppliedAnomaly.getHashCode() == detectedAnomaly.getHashCode()) {
                    iterator.remove();
                    falsePositiveAnomalies.add(UimaUtils.getRatAnomalyModelFromRatAnomaly(detectedAnomaly));
                }
            }
        }

        // Remove redundant anomalies via LevenshteinDistance
        for (RatAnomalyModel previousAppliedAnomaly : previousAppliedAnomalies) {
            for (Iterator<RatAnomaly> iterator = detectedAnomaliesToApply.iterator(); iterator.hasNext();) {
                RatAnomaly detectedAnomaly = iterator.next();
                // The anomaly name is equal
                if (previousAppliedAnomaly.getAnomalyName().equals(detectedAnomaly.getAnomalyName())) {
                    // The sentence is almost equal
                    if (30 > LevenshteinDistance.computeLevenshteinDistance(previousAppliedAnomaly.getSentence(),
                            detectedAnomaly.getSentence())) {
                        LOGGER.debug("Redundant: " + detectedAnomaly.getSentence());
                        iterator.remove();
                        redundantAnomalies.add(UimaUtils.getRatAnomalyModelFromRatAnomaly(detectedAnomaly));
                    }
                }
            }
        }

        LOGGER.debug("False positive anomalies: " + falsePositiveAnomalies.size());
        LOGGER.debug("Detected anomalies after removal of false positive anomalies: " + detectedAnomaliesToApply.size());
    }

    /**
     * Determine incorporated anomalies and the new previous applied anomalies
     * to store them in a document.
     * 
     * <p>
     * This is necessary for an upcoming analysis.
     * 
     * @param documentModel
     *            the document model to retrieve information from previous
     *            analysis.
     * @param appliedAnomalies
     *            the anomalies that were applied to the document.
     */
    public void prepareForNextAnalysis(DocumentModel documentModel, List<RatAnomaly> appliedAnomalies) {
        LOGGER.debug("Applied anomalies size: " + appliedAnomalies.size());
        LOGGER.debug("Previous applied anomalies size: " + documentModel.getPreviousAppliedComments().size());

        // Incorporated anomalies
        incorporatedAnomalies = new ArrayList<RatAnomalyModel>(documentModel.getPreviousAppliedComments());
        incorporatedAnomalies.removeAll(redundantAnomalies);
        incorporatedAnomalies.removeAll(falsePositiveAnomalies);

        for (RatAnomalyModel redundantAnomaly : redundantAnomalies) {
            for (Iterator<RatAnomalyModel> iterator = incorporatedAnomalies.iterator(); iterator.hasNext();) {
                RatAnomalyModel incorporatedAnomaly = iterator.next();
                // The anomaly name is equal
                if (redundantAnomaly.getAnomalyName().equals(incorporatedAnomaly.getAnomalyName())) {
                    // The sentence is almost equal
                    if (30 > LevenshteinDistance.computeLevenshteinDistance(redundantAnomaly.getSentence(),
                            incorporatedAnomaly.getSentence())) {
                        LOGGER.debug("Redundant: " + incorporatedAnomaly.getSentence());
                        iterator.remove();
                    }
                }
            }
        }

        LOGGER.debug("Incorporated anomalies: " + incorporatedAnomalies.size());
        incorporatedAnomalies.addAll(documentModel.getIncorporatedProposals());
        LOGGER.debug("Incorporated anomalies in total: " + incorporatedAnomalies.size());

        // New previous applied comments
        newPreviousAppliedAnomalies = new ArrayList<RatAnomalyModel>();
        newPreviousAppliedAnomalies.addAll(redundantAnomalies);
        newPreviousAppliedAnomalies.addAll(UimaUtils.getRatAnomalyModelsFromAnomalies(appliedAnomalies));

        LOGGER.debug("New previous applied anomalies: " + newPreviousAppliedAnomalies.size());
    }
}