package de.qaware.rat.api.models;

import java.util.List;

import org.docx4j.wml.P;

/**
 * The ParagraphModel class represents an abstraction of a paragraph.
 * 
 * <p>
 * It further stores references to the original paragraph and proprietary
 * standards (e.g. OfficeOpenXML) to annotate the original document after the
 * analysis step.
 * 
 * @author Matthias
 *
 */
public class ParagraphModel {
    private P paragraph;
    private List<RunModel> runModels;

    /**
     * Create an abstraction of a docx4j paragraph element (<w:p>).
     * 
     * @param paragraph
     *            a reference to the paragraph object of the docx4j library.
     */
    public ParagraphModel(P paragraph) {
        this.paragraph = paragraph;
    }

    /**
     * This functions prints the text of the paragraph.
     * 
     * <p>
     * The text of a paragraph is represented by the text of all runs of the
     * paragraph.
     * 
     * @return the text of the paragraph.
     */
    public String getText() {
        StringBuilder sb = new StringBuilder();

        for (RunModel r : this.runModels) {
            sb.append(r.getText());
        }

        return sb.toString();
    }

    /**
     * Get the index of the beginning of the paragraph's text, relative to the
     * text of the document to which the run belongs.
     * 
     * @return the begin of the paragraph.
     */
    public int getBegin() {
        if (this.runModels == null) {
            return 0;
        } else {
            return this.runModels.get(0).getBegin();
        }
    }

    /**
     * Get the index of the ending of the paragraph's text, relative to the text
     * of the document to which the run belongs.
     * 
     * @return the end of the paragraph.
     */
    public int getEnd() {
        if (this.runModels == null) {
            return 0;
        } else {
            return this.runModels.get(this.runModels.size() - 1).getEnd();
        }
    }

    public P getParagraph() {
        return paragraph;
    }

    public void setParagraph(P paragraph) {
        this.paragraph = paragraph;
    }

    public List<RunModel> getRunModels() {
        return runModels;
    }

    public void setRunModels(List<RunModel> runModels) {
        this.runModels = runModels;
    }

    @Override
    public String toString() {
        return "ParagraphModel [getBegin()=" + getBegin() + ", getEnd()=" + getEnd() + ", getText()=" + getText() + "]";
    }
}