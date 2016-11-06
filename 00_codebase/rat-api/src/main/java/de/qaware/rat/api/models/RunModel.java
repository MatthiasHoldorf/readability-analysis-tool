package de.qaware.rat.api.models;

import org.docx4j.wml.R;

/**
 * The RunModel class represents an abstraction of a run element.
 * 
 * <p>
 * A run element contains text elements. It further stores references to the
 * original run and proprietary standards (e.g. OfficeOpenXML) to annotate the
 * original document after the analysis step.
 * 
 * @author Matthias
 *
 */
public class RunModel {
    private R run;
    private ParagraphModel parent;
    private String text;
    private int begin;
    private int end;

    /**
     * Create an abstraction of a docx4j run element (<w:r>).
     * 
     * @param run
     *            a reference to the run object of the docx4j library.
     * @param p
     *            a reference to the parent element (the paragraph element (
     *            <w:p>) in the docx4j library) the run is contained in.
     * @param text
     *            the text of the run.
     * @param begin
     *            the index of the beginning of the run's text, relative to the
     *            text of the paragraph to which the run belongs.
     * @param end
     *            the index of the ending of the run's text, relative to the
     *            text of the paragraph to which the run belongs.
     */
    public RunModel(R run, ParagraphModel p, String text, int begin, int end) {
        this.run = run;
        this.parent = p;
        this.text = text;
        this.begin = begin;
        this.end = end;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public R getRun() {
        return run;
    }

    public void setRun(R run) {
        this.run = run;
    }

    public ParagraphModel getParent() {
        return parent;
    }

    public void setParent(ParagraphModel parent) {
        this.parent = parent;
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

    @Override
    public String toString() {
        return "RunModel [begin=" + begin + ", end=" + end + ", text=" + text + "]";
    }
}