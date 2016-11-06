package de.qaware.rat.api.models;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The {@code RatAnomalyModel} class represents an abstraction of UIMA anomalies
 * for the type {@code de.qaware.rat.type.RatAnomaly}.
 * 
 * @author Matthias
 *
 */
public final class RatAnomalyModel {
    private String anomalyName;
    private String severity;
    private String category;
    private String explanation;
    private String sentence;
    private String coveredText;
    private List<String> violations;
    private int begin;
    private int end;
    private int hashCode;

    /**
     * Creates a {@code RatAnomalyModel} object.
     * 
     * @param anomalyName
     *            the name of the anomaly.
     * @param severity
     *            the severity of the broken rule.
     * @param category
     *            the category of the anomaly, e.g. ReadabilityAnomaly or
     *            GrammarAnomaly.
     * @param explanation
     *            the explanatory text of the anomaly.
     * @param sentence
     *            the sentence the anomaly occurs in.
     * @param coveredText
     *            the coveredText of the anomaly.
     * @param begin
     *            the offset of the beginning of the coveredText in the document
     *            text.
     * @param end
     *            the offset of the ending of the coveredText in the document
     *            text.
     */
    public RatAnomalyModel(String anomalyName, String severity, String category, String explanation, String sentence,
            String coveredText, int begin, int end) {
        super();
        this.anomalyName = anomalyName;
        this.severity = severity;
        this.category = category;
        this.explanation = explanation;
        this.sentence = sentence;
        this.coveredText = coveredText;
        this.begin = begin;
        this.end = end;
    }

    /**
     * Creates a {@code RatAnomalyModel} object.
     * 
     * @param anomalyName
     *            the name of the anomaly.
     * @param severity
     *            the severity of the broken rule.
     * @param category
     *            the category of the anomaly, e.g. ReadabilityAnomaly or
     *            GrammarAnomaly.
     * @param explanation
     *            the explanatory text of the anomaly.
     * @param sentence
     *            the sentence the anomaly occurs in. *
     * @param coveredText
     *            the coveredText of the anomaly.
     * @param violations
     *            the violations (e.g., multiple coveredTexts) of the anomaly.
     * @param begin
     *            the offset of the beginning of the coveredText in the document
     *            text.
     * @param end
     *            the offset of the ending of the coveredText in the document
     *            text.
     */
    public RatAnomalyModel(String anomalyName, String severity, String category, String explanation, String sentence,
            String coveredText, List<String> violations, int begin, int end) {
        super();
        this.anomalyName = anomalyName;
        this.severity = severity;
        this.category = category;
        this.explanation = explanation;
        this.sentence = sentence;
        this.coveredText = coveredText;
        this.violations = violations;
        this.begin = begin;
        this.end = end;
    }

    /**
     * Default constructor
     * 
     */
    public RatAnomalyModel() {
    }

    public String getAnomalyName() {
        return anomalyName;
    }

    public void setAnomalyName(String anomalyName) {
        this.anomalyName = anomalyName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getCoveredText() {
        return coveredText;
    }

    public void setCoveredText(String coveredText) {
        this.coveredText = coveredText;
    }

    public List<String> getViolations() {
        return violations;
    }

    public void setViolations(List<String> violations) {
        this.violations = violations;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    /**
     * Store the XML representation of a {@code RatAnomalyModle} in an
     * {@code Element}.
     * 
     * @param domDoc
     *            the {@code Document} containing the parsed xml.
     * @param nodeName
     *            the name of the newly created {@code Element}.
     * @return the created {@code Element}.
     */
    public Element getXmlRepresentation(Document domDoc, String nodeName) {
        Element anomaly = domDoc.createElement(nodeName);
        String attributeName = "value";

        Element anomalyNameElement = domDoc.createElement("anomalyName");
        anomalyNameElement.setAttribute(attributeName, this.getAnomalyName());
        Element severityElement = domDoc.createElement("severity");
        severityElement.setAttribute(attributeName, this.getSeverity());
        Element categoryElement = domDoc.createElement("category");
        categoryElement.setAttribute(attributeName, this.getCategory());
        Element explanationElement = domDoc.createElement("explanation");
        explanationElement.setAttribute(attributeName, this.getExplanation());
        Element sentenceElement = domDoc.createElement("sentence");
        sentenceElement.setAttribute(attributeName, this.getSentence());
        Element coveredTextElement = domDoc.createElement("coveredText");
        coveredTextElement.setAttribute(attributeName, this.getCoveredText());
        Element beginElement = domDoc.createElement("begin");
        beginElement.setAttribute(attributeName, String.valueOf(this.getBegin()));
        Element endElement = domDoc.createElement("end");
        endElement.setAttribute(attributeName, String.valueOf(this.getEnd()));
        Element hashCodeElement = domDoc.createElement("hashCode");
        hashCodeElement.setAttribute(attributeName, String.valueOf(this.getHashCode()));

        anomaly.appendChild(anomalyNameElement);
        anomaly.appendChild(severityElement);
        anomaly.appendChild(categoryElement);
        anomaly.appendChild(explanationElement);
        anomaly.appendChild(sentenceElement);
        anomaly.appendChild(coveredTextElement);
        anomaly.appendChild(beginElement);
        anomaly.appendChild(endElement);
        anomaly.appendChild(hashCodeElement);

        return anomaly;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((anomalyName == null) ? 0 : anomalyName.hashCode());
        result = prime * result + begin;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + ((coveredText == null) ? 0 : coveredText.hashCode());
        result = prime * result + end;
        result = prime * result + ((explanation == null) ? 0 : explanation.hashCode());
        result = prime * result + hashCode;
        result = prime * result + ((sentence == null) ? 0 : sentence.hashCode());
        result = prime * result + ((severity == null) ? 0 : severity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        RatAnomalyModel other = (RatAnomalyModel) obj;

        return hashCode == other.hashCode;
    }

    @Override
    public String toString() {
        return "RatAnomalyModel [anomalyName=" + anomalyName + ", severity=" + severity + ", category=" + category
                + ", explanation=" + explanation + ", sentence=" + sentence + ", coveredText=" + coveredText
                + ", begin=" + begin + ", end=" + end + "]";
    }
}
