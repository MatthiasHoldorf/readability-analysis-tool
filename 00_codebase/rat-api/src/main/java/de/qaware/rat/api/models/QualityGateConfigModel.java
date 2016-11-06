package de.qaware.rat.api.models;

/**
 * The {@code QualityGateConfigModel} class represents an abstraction for the
 * configuration of the quality gate.
 * 
 * 
 * @author Matthias
 *
 */
public class QualityGateConfigModel {
    private String name;
    private double easyWarningThreshold;
    private double hardWarningThreshold;
    private double easyErrorThreshold;
    private double hardErrorThreshold;

    /**
     * Create a {@code QualityGateConfigModel} object.
     * 
     * @param name
     *            the name of the quality gate measurement.
     * @param easyWarningThreshold
     *            the warning threshold applied for too easy.
     * @param hardWarningThreshold
     *            the warning threshold applied for too hard.
     * @param easyErrorThreshold
     *            the error threshold applied for too easy.
     * @param hardErrorThreshold
     *            the error threshold applied for too hard.
     */
    public QualityGateConfigModel(String name, double easyWarningThreshold, double hardWarningThreshold,
            double easyErrorThreshold, double hardErrorThreshold) {
        this.name = name;
        this.easyWarningThreshold = easyWarningThreshold;
        this.hardWarningThreshold = hardWarningThreshold;
        this.easyErrorThreshold = easyErrorThreshold;
        this.hardErrorThreshold = hardErrorThreshold;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getEasyWarningThreshold() {
        return easyWarningThreshold;
    }

    public void setEasyWarningThreshold(double easyWarningThreshold) {
        this.easyWarningThreshold = easyWarningThreshold;
    }

    public double getHardWarningThreshold() {
        return hardWarningThreshold;
    }

    public void setHardWarningThreshold(double hardWarningThreshold) {
        this.hardWarningThreshold = hardWarningThreshold;
    }

    public double getHardErrorThreshold() {
        return hardErrorThreshold;
    }

    public void setHardErrorThreshold(double hardErrorThreshold) {
        this.hardErrorThreshold = hardErrorThreshold;
    }

    public double getEasyErrorThreshold() {
        return easyErrorThreshold;
    }

    public void setEasyErrorThreshold(double easyErrorThreshold) {
        this.easyErrorThreshold = easyErrorThreshold;
    }
}