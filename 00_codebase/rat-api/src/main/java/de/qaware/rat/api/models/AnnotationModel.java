package de.qaware.rat.api.models;

/**
 * The AnnotationModel class represents an abstraction of an annotation from the
 * linguistic and rule processing.
 * 
 * @author Matthias
 *
 */
public class AnnotationModel {
    private int begin;
    private int end;
    private String coveredText;
    private String description;

    /**
     * Creates an abstraction of an annotation.
     * 
     * @param begin
     *            the index of the beginning of the annotation.
     * @param end
     *            the index of the end of the annotation.
     * @param coveredText
     *            the text the annotation covers.
     * @param description
     *            the description, i.e. the text of the comment, of the
     *            annotation.
     */
    public AnnotationModel(int begin, int end, String coveredText, String description) {
        this.begin = begin;
        this.end = end;
        this.coveredText = coveredText;
        this.description = description;
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

    public String getCoveredText() {
        return coveredText;
    }

    public void setCoveredText(String coveredText) {
        this.coveredText = coveredText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AnnotationModel [begin=" + begin + ", end=" + end + ", coveredText=" + coveredText + ", description="
                + description + "]";
    }
}