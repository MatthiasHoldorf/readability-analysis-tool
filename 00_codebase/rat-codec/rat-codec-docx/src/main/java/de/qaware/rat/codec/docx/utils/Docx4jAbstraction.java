package de.qaware.rat.codec.docx.utils;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.CustomXmlPart;
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Comments.Comment;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.api.models.ParagraphModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.api.models.RunModel;

/**
 * The Docx4jAbstraction class abstracts .docx file to a
 * {@link de.qaware.rat.api.models.DocumentModel}.
 * 
 * @author Matthias
 *
 */
public final class Docx4jAbstraction {
    private static final Logger LOGGER = LoggerFactory.getLogger(Docx4jAbstraction.class);

    private Docx4jAbstraction() {
    }

    /**
     * This methods takes a {@code WordprocessingMLPackage} and abstracts it to
     * a generic {@link de.qaware.rat.api.models.DocumentModel}.
     * 
     * <p>
     * The reference to the original document, the
     * {@code WordprocessingMLPackage} object, is stored in the
     * {@link de.qaware.rat.api.models.DocumentModel}.
     * 
     * @param wml
     *            the reference object from the {@code Docx4J} library
     *            representing a .docx file.
     * @return the abstracted {@code DocumentModel}.
     * @throws JAXBException
     *             if the relevantParagraphs cannot be retrieved.
     * @throws Docx4JException
     *             if a docx4j error occurs.
     */
    public static DocumentModel abstractWordDocument(WordprocessingMLPackage wml)
            throws Docx4JException, JAXBException {
        DocumentModel documentModel = null;
        MainDocumentPart documentPart = wml.getMainDocumentPart();

        List<P> relevantParagraphs = Docx4jUtils.findRelevantParagraphs(documentPart);
        if (relevantParagraphs.size() > 0) {
            List<ParagraphModel> paragraphModels = createParagraphModels(relevantParagraphs);
            documentModel = new DocumentModel(paragraphModels, wml);

            LOGGER.debug(documentModel.toString());
        } else {
            LOGGER.warn("The document does not contain any relevant paragraph to the analysis.");
            return null;
        }

        // Set data about previous applied comments in order to detect redundant
        // anomalies, false positives and incorporated anomalies
        documentModel.setAppliedCommentsHashCodes(getCommentHashCodes(documentModel));
        documentModel.setPreviousAppliedComments(getRatAnomaliesFromCustomXmlPart(documentModel, "previous-applied"));
        documentModel.setFalsePositives(getRatAnomaliesFromCustomXmlPart(documentModel, "false-positives"));
        documentModel
                .setIncorporatedProposals(getRatAnomaliesFromCustomXmlPart(documentModel, "incorporated-proposals"));

        return documentModel;
    }

    /**
     * This function creates an abstraction of run elements.
     * 
     * <p>
     * The function is used to update the runs of a changed paragraph during the
     * exporting phase of annotation. Therefore, the function is public.
     * 
     * @param lengthOfPrecedingParagraph
     *            the length of the preceding paragraph determines the index to
     *            begin with.
     * @param paragraphModel
     *            the paragraph abstraction to generate the run abstraction
     *            from.
     * @return a list of abstracted docx4j run elements (<w:r>).
     */
    public static List<RunModel> createRunModels(int lengthOfPrecedingParagraph, ParagraphModel paragraphModel) {
        List<RunModel> runModels = new ArrayList<RunModel>();

        // Retrieve docx4j runs from paragraphModel
        List<R> runs = Docx4jUtils.getRunsOfParagraph(paragraphModel.getParagraph());

        int begin = 0;
        int end = lengthOfPrecedingParagraph;

        for (R r : runs) {
            Text t = Docx4jUtils.getTextOfRun(r);

            // A run can be a commentReference, which does not have text. This
            // results in a null reference at this point.
            if (t != null) {
                String text = t.getValue();

                begin = end;
                end = begin + text.length();

                RunModel runModel = new RunModel(r, paragraphModel, text, begin, end);

                runModels.add(runModel);
            }
        }

        // Add a space (if not already there) to the last text element of the
        // runModel in the list.
        // Otherwise the paragraphs are not distinguished by a space, which
        // hinders the linguistic analysis.

        RunModel lastRunModel = runModels.get(runModels.size() - 1);
        String lastRunModelText = lastRunModel.getText();

        try {
            if (!Character.isWhitespace(lastRunModelText.charAt(lastRunModelText.length() - 1))) {
                LOGGER.debug("RunModelText to append with a space: " + lastRunModelText);
                lastRunModel.setText(lastRunModel.getText() + " ");
                lastRunModel.setEnd(lastRunModel.getEnd() + 1);
            }
        } catch (StringIndexOutOfBoundsException e) {
            LOGGER.warn("Adding a space to the last run model failed.");
        }

        // Update the runModels list
        runModels.remove(runModels.size() - 1);
        runModels.add(lastRunModel);

        LOGGER.debug(runModels.toString());
        return runModels;
    }

    /**
     * This function creates an abstraction of paragraph elements.
     * 
     * @param paragraphs
     *            the docx4j paragraph object to create an abstraction from.
     * @return a paragraphModels list as abstraction from the given paragraphs.
     */
    private static List<ParagraphModel> createParagraphModels(List<P> paragraphs) {
        List<ParagraphModel> paragraphModels = new ArrayList<ParagraphModel>();
        int lengthOfPrecedingParagraph = 0;

        for (P p : paragraphs) {
            // Create a paragraphModel from the docx4j paragraph object
            ParagraphModel paragraphModel = new ParagraphModel(p);

            // Create runModels
            List<RunModel> runModels = createRunModels(lengthOfPrecedingParagraph, paragraphModel);
            paragraphModel.setRunModels(runModels);
            lengthOfPrecedingParagraph = paragraphModel.getEnd();

            // Add the paragraphModel the the list of paragraphModels
            LOGGER.debug(paragraphModel.toString());
            paragraphModels.add(paragraphModel);
        }

        return paragraphModels;
    }

    /**
     * This function retrieves hash codes from comments in a document.
     * 
     * @param documentModel
     *            the documentModel yielding the original reference to a
     *            document.
     * @return a list of hash codes of the currently applied comments in the
     *         document.
     * @throws Docx4JException
     *             if an docx4j error occurs.
     */
    private static List<Integer> getCommentHashCodes(DocumentModel documentModel) throws Docx4JException {
        List<Integer> hashCodes = new ArrayList<Integer>();
        WordprocessingMLPackage wml = documentModel.getWml();
        CommentsPart commentsPart;
        List<Comment> comments;

        try {
            commentsPart = Docx4jUtils.getCommentsPart(wml);
            comments = commentsPart.getContents().getComment();
        } catch (Docx4JException e) {
            throw new Docx4JException("Cannot get hash codes from comments. Accessing the comments part failed.", e);
        }

        for (Comment comment : comments) {
            if (comment.getInitials() != null) {
                if (comment.getInitials().equals("RAT")) {
                    try {
                        int hashCode = Integer.parseInt(comment.getAuthor());
                        hashCodes.add(hashCode);
                    } catch (NumberFormatException e) {
                        LOGGER.warn("A comment with initials RAT does not have a hash code attribute.");
                    }
                }
            }
        }

        return hashCodes;
    }

    /**
     * 
     * @param documentModel
     * @param anomalyType
     *            types of anomalies stored in the document:
     *            previous-applied,false positives or incorporated-proposals.
     * @return
     * @throws Docx4JException
     */
    private static List<RatAnomalyModel> getRatAnomaliesFromCustomXmlPart(DocumentModel documentModel,
            String anomalyType) throws Docx4JException {
        List<RatAnomalyModel> anomalies = new ArrayList<RatAnomalyModel>();
        String xml = null;

        try {
            CustomXmlPart customXmlPart = Docx4jXMLElements.getRatAnomalyCustomXmlPart(documentModel.getWml());

            if (customXmlPart != null) {
                xml = Docx4jXMLElements.getRatAnomalyCustomXmlPart(documentModel.getWml()).getXML();
                Document parsedXml = Jsoup.parse(xml);
                Elements anomalyElements = parsedXml.select(anomalyType);

                for (Element anomalyElement : anomalyElements) {
                    anomalies.add(getRatAnomalyModelFromXml(anomalyElement));
                }
            } else {
                LOGGER.debug("The document has not been analysed by RAT before.");
                LOGGER.debug("The custom xml part does not exist yet; cannot get " + anomalyType + " from document.");
            }
        } catch (Docx4JException e) {
            throw new Docx4JException("Getting rat anomalies of " + anomalyType + " failed.", e);
        }

        return anomalies;
    }

    private static RatAnomalyModel getRatAnomalyModelFromXml(Element anomalyElement) {
        RatAnomalyModel anomaly = new RatAnomalyModel();
        String attributeName = "value";

        anomaly.setAnomalyName(anomalyElement.select("anomalyName").attr(attributeName));
        anomaly.setSeverity(anomalyElement.select("severity").attr(attributeName));
        anomaly.setCategory(anomalyElement.select("category").attr(attributeName));
        anomaly.setExplanation(anomalyElement.select("explanation").attr(attributeName));
        anomaly.setSentence(anomalyElement.select("sentence").attr(attributeName));
        anomaly.setCoveredText(anomalyElement.select("coveredText").attr(attributeName));
        anomaly.setBegin(Integer.valueOf(anomalyElement.select("begin").attr(attributeName)));
        anomaly.setEnd(Integer.valueOf(anomalyElement.select("end").attr(attributeName)));
        anomaly.setHashCode(Integer.valueOf(anomalyElement.select("hashCode").attr(attributeName)));

        return anomaly;
    }
}