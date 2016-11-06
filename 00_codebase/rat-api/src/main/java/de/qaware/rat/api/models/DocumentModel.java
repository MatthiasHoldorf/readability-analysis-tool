package de.qaware.rat.api.models;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

/**
 * The DocumentModel class represents an abstraction of a document to be
 * analysed.
 * 
 * <p>
 * The reference to the original document, the {@code WordprocessingMLPackage}
 * object, is stored in the {@link de.qaware.rat.api.models.DocumentModel}.
 * 
 * <p>
 * It further stores a references to the original document and proprietary
 * standards (e.g. OfficeOpenXML) to annotate the original document after the
 * analysis step.
 * 
 * @author Matthias
 *
 */
public class DocumentModel {
    private List<ParagraphModel> paragraphModels;
    private WordprocessingMLPackage wml;
    private JCas jCas;

    private List<Integer> appliedCommentsHashCodes;
    private List<RatAnomalyModel> previousAppliedComments;
    private List<RatAnomalyModel> falsePositives;
    private List<RatAnomalyModel> incorporatedProposals;

    /**
     * Create an abstraction of a document.
     * 
     * @param paragraphModels
     *            the paragraphs of a document as an abstraction.
     * @param wml
     *            the reference to the original document object.
     */
    public DocumentModel(List<ParagraphModel> paragraphModels, WordprocessingMLPackage wml) {
        this.paragraphModels = paragraphModels;
        this.wml = wml;
    }

    /**
     * Default / no arguments constructor for testing.
     */
    public DocumentModel() {
    }

    /**
     * This functions prints the text of the document.
     * 
     * <p>
     * The text of a document is represented by the text of all paragraphs of
     * the document.
     * 
     * @return the text of the document.
     */
    public String getText() {
        StringBuilder sb = new StringBuilder();

        for (ParagraphModel p : this.paragraphModels) {
            sb.append(p.getText());
        }

        return sb.toString();
    }
    
    /**
     * Get the index of the beginning of the document text.
     * 
     * @return the begin of the paragraph.
     */
    public int getBegin() {
        if (this.paragraphModels == null) {
            return 0;
        } else {
            return this.paragraphModels.get(0).getBegin();
        }
    }
    
    /**
     * Get the index of the end of the document text.
     * 
     * @return the end of the paragraph.
     */
    public int getEnd() {
        if (this.paragraphModels == null) {
            return 0;
        } else {
            return this.paragraphModels.get(this.paragraphModels.size() - 1).getEnd();
        }
    }

    public WordprocessingMLPackage getWml() {
        return wml;
    }

    public void setWml(WordprocessingMLPackage wml) {
        this.wml = wml;
    }

    /**
     * Returns jCas object that stores annotations from the linguistic and rule
     * analysis.
     * 
     * @return the jCas object.
     */
    public JCas getjCas() {
        return jCas;
    }

    /**
     * Sets jCas object stores annotations from the linguistic and rule
     * analysis.
     * 
     */
    public void setjCas(JCas jCas) {
        this.jCas = jCas;
    }

    public List<ParagraphModel> getParagraphModels() {
        return paragraphModels;
    }

    public void setParagraphs(List<ParagraphModel> paragraphModels) {
        this.paragraphModels = paragraphModels;
    }

    public List<Integer> getAppliedCommentsHashCodes() {
        return appliedCommentsHashCodes;
    }

    public void setAppliedCommentsHashCodes(List<Integer> appliedCommentsHashCodes) {
        this.appliedCommentsHashCodes = appliedCommentsHashCodes;
    }

    public List<RatAnomalyModel> getPreviousAppliedComments() {
        return previousAppliedComments;
    }

    public void setPreviousAppliedComments(List<RatAnomalyModel> previousAppliedComments) {
        this.previousAppliedComments = previousAppliedComments;
    }

    public List<RatAnomalyModel> getFalsePositives() {
        return falsePositives;
    }

    public void setFalsePositives(List<RatAnomalyModel> falsePositives) {
        this.falsePositives = falsePositives;
    }

    public List<RatAnomalyModel> getIncorporatedProposals() {
        return incorporatedProposals;
    }

    public void setIncorporatedProposals(List<RatAnomalyModel> incorporatedProposals) {
        this.incorporatedProposals = incorporatedProposals;
    }

    public void setParagraphModels(List<ParagraphModel> paragraphModels) {
        this.paragraphModels = paragraphModels;
    }

    @Override
    public String toString() {
        return "DocumentModel [getBegin()=" + getBegin() + ", getEnd()=" + getEnd() + ", getText()=" + getText() + "]";
    }
}