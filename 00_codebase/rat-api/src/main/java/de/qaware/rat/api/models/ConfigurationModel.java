package de.qaware.rat.api.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The {@code ConfigurationModel} class represents an abstraction of the parsed
 * configuration.
 * 
 * @author Matthias
 *
 */
public final class ConfigurationModel {
    private List<String> keywords = new ArrayList<String>();
    private List<AnnotatorRuleModel> anomalyRuleModels;
    private Map<String, QualityGateConfigModel> qualityGateInformation;

    /**
     * Returns an {@code AnnotatorRuleModel} by name.
     * 
     * @param name
     *            the name of the {@code AnnotatorRuleModel}.
     * @return the {@code AnnotatorRuleModel} or null.
     */
    public AnnotatorRuleModel getAnomalyRuleModel(String name) {
        AnnotatorRuleModel result = null;

        if (anomalyRuleModels != null) {
            for (AnnotatorRuleModel rule : anomalyRuleModels) {
                if (rule.getName().equals(name)) {
                    result = rule;
                }
            }
        }

        return result;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<AnnotatorRuleModel> getAnomalyRuleModels() {
        return anomalyRuleModels;
    }

    public void setAnomalyRuleModels(List<AnnotatorRuleModel> anomalyRuleModels) {
        this.anomalyRuleModels = anomalyRuleModels;
    }

    public Map<String, QualityGateConfigModel> getQualityGateInformation() {
        return qualityGateInformation;
    }

    public void setQualityGateInformation(Map<String, QualityGateConfigModel> measurementModels) {
        this.qualityGateInformation = measurementModels;
    }

    @Override
    public String toString() {
        return "ConfigurationModel [keywords=" + keywords.size() + ", anomalyRuleModels=" + anomalyRuleModels.size()
                + ", qualityGateInformation=" + qualityGateInformation.size() + "]";
    }
}