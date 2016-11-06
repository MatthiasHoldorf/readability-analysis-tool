package de.qaware.rat.api.models;

/**
 * The {@class AnnotatorRuleModel} class represents an abstraction for the
 * configuration of the {@code Java Annotator Classes}.
 * 
 * @author Matthias
 *
 */
public class AnnotatorRuleModel {
    private String name;
    private String severity;
    private int threshold;
    private boolean isEnabled;

    /**
     * Creates an {@code AnnotatorRuleModel} object.
     * 
     * @param name
     *            the name of the {@code Java Annotator Class}.
     * @param severity
     *            the severity of the rule that is broken.
     * @param threshold
     *            the threshold value at which point the rule is applied.
     * @param isEnabled
     *            whether or not the rule is enabled.
     */
    public AnnotatorRuleModel(String name, String severity, int threshold, boolean isEnabled) {
        this.name = name;
        this.severity = severity;
        this.threshold = threshold;
        this.isEnabled = isEnabled;
    }

    /**
     * Creates a {@code AnnotatorRuleModel} object.
     * 
     * @param name
     *            the name of the {@code Java Annotator Class}.
     * @param severity
     *            the severity of the rule that is broken.
     */
    public AnnotatorRuleModel(String name, String severity) {
        this.name = name;
        this.severity = severity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public String toString() {
        return "AnomalyRuleModel [name=" + name + ", severity=" + severity + ", threshold=" + threshold + ", isEnabled="
                + isEnabled + "]";
    }
}