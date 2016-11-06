package de.qaware.rat.codec.docx.utils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.docx4j.TextUtils;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Comments.Comment;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.exceptions.ExportException;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.common.ImporterUtils;

/**
 * The Docx4jUtils class provides utility functions for the {@code docx4j}
 * library, which is used in the {@link de.qaware.rat.codec.docx.Docx4jExporter}
 * and {@link de.qaware.rat.codec.docx.Docx4jImporter} classes.
 * 
 * @author Matthias
 *
 */
public final class Docx4jUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Docx4jUtils.class);

    private Docx4jUtils() {
    }

    /**
     * Traverses a MainDocumentPart and returns a list of matched objects by the
     * xpathExpression.
     * 
     * @param documentPart
     *            the document part to traverse.
     * @param xpathExpression
     *            the xpath expression to match.
     * @return a list of matched objects
     * @throws XPathBinderAssociationIsPartialException
     *             if a docx4j error occurs.
     * @throws JAXBException
     *             if a docx4j error occurs.
     */
    public static List<Object> getAllElementsFromDocumentByXpath(MainDocumentPart documentPart, String xpathExpression)
            throws XPathBinderAssociationIsPartialException, JAXBException {
        List<Object> results;

        try {
            results = documentPart.getJAXBNodesViaXPath(xpathExpression, false);
        } catch (NumberFormatException e) {
            throw new JAXBException(e);
        }

        return results;
    }

    /**
     * This function retrieves all run elements <w:r> from a paragraph element
     * <w:p>.
     * 
     * @param paragraph
     *            the paragraph to retrieve runs from.
     * @return a list of runs contained in the paragraph.
     */
    public static List<R> getRunsOfParagraph(P paragraph) {
        List<Object> objects = getAllElementsFromObjectByClassName(paragraph, R.class);
        List<R> runs = new ArrayList<R>();

        for (Object o : objects) {
            runs.add((R) o);
        }

        return runs;
    }

    /**
     * Find relevant paragraphs within an OfficeOpenXML file.
     * 
     * <p>
     * A relevant paragraph contains a punctuation mark or is at least 70
     * characters long (including whitespace).
     * 
     * @param documentPart
     *            the document part to traverse.
     * @return a list of paragraphs containing the relevant text passages.
     * @throws JAXBException
     *             if a docx4j error occurs.
     * @throws XPathBinderAssociationIsPartialException
     *             if a docx4j error occurs.
     */
    public static List<P> findRelevantParagraphs(MainDocumentPart documentPart)
            throws XPathBinderAssociationIsPartialException, JAXBException {
        LOGGER.info("START PARAGRAPH EXTRACTION");
        List<Object> paragraphsInDocument = getAllElementsFromDocumentByXpath(documentPart, "//w:p");
        List<P> relevantParagraphs = new ArrayList<P>();

        for (Object obj : paragraphsInDocument) {
            P p = (P) obj;

            // Find sentences ending with punctuation mark
            if (getTextFromParagraph(p).contains(".") || getTextFromParagraph(p).contains("!")
                    || getTextFromParagraph(p).contains("?")) {
                if (doesParagraphContainText(p)) {
                    LOGGER.debug("New paragraph: " + p);
                    relevantParagraphs.add(p);
                }
            }
        }

        LOGGER.info(relevantParagraphs.size() + " Paragraphs found.");
        LOGGER.info("END PARAGRAPH EXTRACTION");
        return relevantParagraphs;
    }

    /**
     * This functions gets the text from a paragraph.
     * 
     * @param paragraph
     *            the paragraph to get the text from.
     * @return the text of a paragraph
     */
    public static String getTextFromParagraph(P paragraph) {
        StringWriter str = new StringWriter();

        try {
            TextUtils.extractText((Object) paragraph, str);
        } catch (Exception e) {
            LOGGER.error("Extracting text from paragraph failed", e);
        }

        return str.toString();
    }

    /**
     * Returns the text of a given run <w:r>.
     * 
     * @param run
     *            the run to get the text from.
     * @return the text element <w:t> of the run or null, if the run <w:r> does
     *         not contain a text element.
     */
    public static Text getTextOfRun(R run) {
        List<Object> o = getAllElementsFromObjectByClassName(run, Text.class);
        if (o.size() != 0) {
            return (Text) o.get(0);
        } else {
            return null;
        }
    }

    /**
     * Traverses an object and returns classes found by the toSearch parameter.
     * 
     * @param obj
     *            the object to search.
     * @param toSearch
     *            the class to search for.
     * @return the found objects of the class (toSearch) in the obj parameter.
     */
    public static List<Object> getAllElementsFromObjectByClassName(Object obj, Class<?> toSearch) {
        List<Object> result = new ArrayList<Object>();
        Object object = obj;

        if (object instanceof JAXBElement) {
            object = ((JAXBElement<?>) object).getValue();
        }

        if (object != null && object.getClass().equals(toSearch)) {
            result.add(object);
        } else if (object instanceof ContentAccessor) {
            List<?> children = ((ContentAccessor) object).getContent();
            for (Object child : children) {
                result.addAll(getAllElementsFromObjectByClassName(child, toSearch));
            }
        }

        return result;
    }

    /**
     * This functions returns the comments part ("/word/comments.xml") of a wml
     * object.
     * 
     * <p>
     * If not comments parts exists, a new one is created and added to the wml.
     * 
     * @param wml
     *            the document to get the comments part from.
     * @return the comments part, if it exists. Otherwise null will be returned.
     * @throws InvalidFormatException
     *             if a new comments part cannot be created.
     */
    public static CommentsPart getCommentsPart(WordprocessingMLPackage wml) throws InvalidFormatException {
        CommentsPart commentsPart = wml.getMainDocumentPart().getCommentsPart();

        if (commentsPart == null) {
            commentsPart = Docx4jXMLElements.createCommentsPart();
            wml.getMainDocumentPart().addTargetPart(commentsPart);
        }

        return commentsPart;
    }

    /**
     * Detect sentences from a list of paragraphs.
     * 
     * @param paragraphs
     *            A list of paragraphs containing sentences.
     * @param locale
     *            A <code>Locale</code> object represents a specific
     *            geographical, political, or cultural region.
     * @return sentences a list of sentences contained in the list of
     *         paragraphs.
     */
    public static List<String> detectSentencesFromParagraph(List<P> paragraphs, Locale locale) {
        LOGGER.info("START SENTENCE EXTRACTION");
        List<String> sentences = new ArrayList<String>();
        for (P paragraph : paragraphs) {
            sentences.addAll(detectSentencesFromParagraph(paragraph, locale));
        }
        LOGGER.debug(sentences.size() + " Sentences detected.");
        LOGGER.info("END SENTENCE EXTRACTION");
        return sentences;
    }

    /**
     * Detect sentences form a paragraph.
     * 
     * @param paragraph
     *            A paragraph containing sentences.
     * @param locale
     *            A <code>Locale</code> object represents a specific
     *            geographical, political, or cultural region.
     * @return sentences a list of sentences contained in the paragraph.
     */
    public static List<String> detectSentencesFromParagraph(P paragraph, Locale locale) {
        List<String> sentences = new ArrayList<String>();
        sentences.addAll(ImporterUtils.detectSentences(getTextFromParagraph(paragraph), locale));

        for (String sentence : sentences) {
            LOGGER.debug("New sentence: " + sentence);
        }

        return sentences;
    }

    /**
     * This function returns the highest comment id in the given document (
     * {@code wml}).
     * 
     * @param wml
     *            the document to retrieve the comments from.
     * @return the highest comment id or 0 if no comment exists.
     * @throws ExportException
     *             if the content of the comments part could not be retrieved.
     */
    public static int getMaxCommentIdValue(WordprocessingMLPackage wml) throws ExportException {
        try {
            CommentsPart commentsPart = Docx4jUtils.getCommentsPart(wml);
            List<Comment> existingComments = commentsPart.getContents().getComment();
            int maxCurrentCommentId = 0;

            for (Comment comment : existingComments) {
                if (comment.getId().intValue() > maxCurrentCommentId) {
                    maxCurrentCommentId = (comment.getId().intValue());
                }
            }

            return maxCurrentCommentId;

        } catch (Docx4JException e) {
            throw new ExportException("The content of the comments part could not be retrieved", e);
        }
    }

    /**
     * Remove comments from the commentsPart and the documentPart of a word
     * document.
     * 
     * @param wml
     *            the word document to remove the comments from.
     * @param anomalies
     *            the anomaly to remove (by hash code as identifier).
     * @throws ExportException
     *             if the deletion of the comments fails.
     */
    public static void deleteCommentsByHashCode(WordprocessingMLPackage wml, List<RatAnomalyModel> anomalies)
            throws ExportException {
        try {
            CommentsPart commentsPart = Docx4jUtils.getCommentsPart(wml);
            List<Comment> existingComments = commentsPart.getContents().getComment();

            for (RatAnomalyModel anomaly : anomalies) {
                for (Iterator<Comment> iterator = existingComments.iterator(); iterator.hasNext();) {
                    Comment comment = iterator.next();
                    if (comment.getAuthor().equals(String.valueOf(anomaly.getHashCode()))) {
                        LOGGER.info(String.format("Remove comment with id: %s from word document.", comment.getId()));
                        // Remove from comments part
                        iterator.remove();
                        // Remove comments elements from document part
                        CommentFinder.deleteCommentElements(wml.getMainDocumentPart().getContents().getBody(),
                                comment.getId());
                    }
                }
            }

        } catch (Docx4JException e) {
            throw new ExportException(
                    "The content of the comments part could not be retrieved. The deletion of comments failed.", e);
        }
    }

    private static Boolean doesParagraphContainText(P p) {
        List<R> runs = getRunsOfParagraph(p);

        for (R r : runs) {
            if (getTextOfRun(r) != null) {
                return true;
            }
        }

        return false;
    }
}