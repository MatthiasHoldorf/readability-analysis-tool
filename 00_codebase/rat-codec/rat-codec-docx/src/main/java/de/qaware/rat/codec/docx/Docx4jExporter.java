package de.qaware.rat.codec.docx;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;
import org.docx4j.wml.CommentRangeEnd;
import org.docx4j.wml.CommentRangeStart;
import org.docx4j.wml.Comments.Comment;
import org.docx4j.wml.P;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.qaware.rat.api.exceptions.ExportException;
import de.qaware.rat.api.interfaces.ExporterService;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.api.models.ParagraphModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.api.models.RunModel;
import de.qaware.rat.codec.docx.utils.Docx4jAbstraction;
import de.qaware.rat.codec.docx.utils.Docx4jUtils;
import de.qaware.rat.codec.docx.utils.Docx4jXMLElements;
import de.qaware.rat.type.RatAnomaly;

/**
 * The Docx4jExporter class applies annotations in the form of comments to .docx
 * files.
 * 
 * <p>
 * A .docx file is standarized by the OpenOfficeXML standard
 * (http://www.ecma-international.org/publications/standards/Ecma-376.htm),
 * which determines the rules of the xml formatting of the numerous xml files
 * which represent a .docx file.
 * 
 * <p>
 * A .docx file consists of numerous xml files, which can be investigated by
 * unzipping a .docx file. This class manipulates two of these files, the
 * "/word/document.xml", containing the paragraphs <w:p>, runs <w:r> and texts
 * <w:r> elements and the "/word/comments.xml", an array of comments that are
 * embedded by references in the "/word/document.xml".
 * 
 * <p>
 * A "/word/document.xml" contains multiple paragraph elements, which contain
 * (amongst other things, e.g. <w:rPr> for style) runs elements, which contain
 * up to one text element. The text elements depict the text we see in the .docx
 * file.
 * 
 * <p>
 * A comment is applied on the level of a paragraph's content around the run
 * element that contains the text to annotate.
 * 
 * <p>
 * In particular, a comment is applied by adding the following elements to a
 * paragraph's content: 1) a commentRangeStart element (<b>before</b> the run to
 * annotate), 2) a commentRangeEnd element (after the run to annotate) and a
 * commentReference (after the commentRangeEnd element).
 * 
 * <p>
 * The commentReference in the ("/word/comments.xml") links to the actual
 * comment within ("/word/comments.xml"). A commentReference also counts as run
 * element <w:r>. See {@link de.qaware.rat.codec.docx.utils.Docx4jXMLElements}
 * and {@link de.qaware.rat.codec.docx.utils.Docx4jUtilsTest}.
 * 
 * <p>
 * Applying a comment requires updating a paragraph's abstraction in the
 * {@link de.qaware.rat.api.models.DocumentModel}, especially when there are two
 * annotations within one paragraph.
 * 
 * <p>
 * A comment is applied on the level of a paragraph's content, around run
 * elements. These run elements contain the actual text in the form of text
 * elements <w:t>. However, a text element can contain a single word as well as
 * an entire sentence. Therefore, it might be necessary to create a new run
 * element that only contains the text we like to annotate. There are 4 possible
 * cases are taken into account:
 * 
 * <pre>
 * 1.	<w:t>The <b>weather</b> is nice”</w:t>	e.g., in the middle
 * 2.	<w:t>The <b>weather</b>”</w:t>		    e.g., at the end
 * 3.	<w:t><b>weather</b> is nice”</w:t>		e.g., at the beginning
 * 4.	<w:t><b>weather</b></w:t>			    e.g., already a standalone <w:r>-element
 * 5.   <w:r>									e.g., the word is formatted in multiple ways
 * 			<w:t>
 * 				<b>wea</b>
 * 			</w:t>
 * 		<w:r>
 * 		<w:r>
 * 			<w:t>
 * 				<i>ther</i>
 * 			</w:t>
 * 		</w:r>
 * </pre>
 * 
 * <p>
 * There is a 5. case which is not supported. That is annotating multiple words.
 * For that, multiple runs needs to be detected and the splitting as above needs
 * to be done over these runs.
 * 
 * <p>
 * The {@code docx4j} library is used to apply the comments.
 * 
 * @author Matthias
 *
 */
public final class Docx4jExporter implements ExporterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Docx4jExporter.class);
    private static final String ANNOTATION_TEXT_DOES_NOT_MATCH = "The annotationPart variable \"%s\" does not match the annotation text \"%s\" to cover:";

    @Override
    public String getCapability() {
        return "docx";
    }

    /**
     * This function applies annotations to the original document (the
     * {@code WordprocessingMLPackage} object), which is represented by the
     * abstraction: {@code documentModel}.
     * 
     * @param documentModel
     *            the abstraction of the original document.
     * @param annotations
     *            the annotations retrieved from the pipeline to annotate as
     *            comments in the original document.
     * @throws Docx4JException
     *             if a Docx4J error occurs.
     * @throws JAXBException
     *             if a XML error occurs.
     */
    @Override
    public List<RatAnomaly> applyAnnotations(DocumentModel documentModel, Collection<RatAnomaly> annotations)
            throws ExportException {
        List<RatAnomaly> appliedAnnotations = new ArrayList<RatAnomaly>();

        // Retrieve the highest comment id as starting point
        int currentCommentId = Docx4jUtils.getMaxCommentIdValue(documentModel.getWml());

        for (RatAnomaly annotation : annotations) {
            RunModel runToApplyAnnotation = findRunToAnnotate(documentModel, annotation);

            if (runToApplyAnnotation == null) {
                LOGGER.debug(String.format("Could not find run for the annotation text: \"%s\"",
                        annotation.getCoveredText()));
            } else {

                ParagraphModel paragraph = runToApplyAnnotation.getParent();
                int runIndexInParagraph = paragraph.getParagraph().getContent().indexOf(runToApplyAnnotation.getRun());

                // The run does not have a parent paragraph; the annotations
                // will not be applied
                if (runIndexInParagraph == -1) {
                    LOGGER.debug("Annotation could not be applied to " + "\""
                            + Docx4jUtils.getTextOfRun(runToApplyAnnotation.getRun()).getValue() + "\"");
                } else {

                    // Get the commentsPart and existing comments
                    CommentsPart commentsPart;
                    List<Comment> existingComments;
                    try {
                        commentsPart = Docx4jUtils.getCommentsPart(documentModel.getWml());
                        existingComments = commentsPart.getContents().getComment();
                    } catch (Docx4JException e) {
                        throw new ExportException("The content of the comments part could not be retrieved", e);
                    }

                    // Increment the commentId
                    LOGGER.debug(String.format("The size of the comments part is %s.", existingComments.size()));
                    LOGGER.debug(String.format("The current comment id is %s.", currentCommentId));
                    currentCommentId++;

                    // Create the Hyperlink to the documentation
                    Hyperlink linkToDocumentation;
                    try {
                        linkToDocumentation = Docx4jXMLElements.createHyperlinkInCommentsPart(documentModel.getWml(),
                                "https://github.com/MatthiasHoldorf/rat-readability-analysis-tool-documentation#"
                                        + annotation.getAnomalyName(),
                                "Sehen Sie Beispiele zu dieser Regel in der Dokumentation.");
                    } catch (InvalidFormatException | JAXBException e) {
                        throw new ExportException("The hyperlink for the comment could not be created.", e);
                    }

                    // Create comment
                    BigInteger commentId = BigInteger.valueOf(currentCommentId);
                    Comment comment = Docx4jXMLElements.createComment(commentId, "Readability-Analysis-Tool", "RAT",
                            new Date(), annotation.getSeverity(), annotation.getAnomalyName(),
                            annotation.getExplanation(), linkToDocumentation, annotation.getHashCode());

                    // Add the comment to the "/word/comments.xml" part
                    existingComments.add(comment);

                    try {
                        applyAnnotationToRun(runToApplyAnnotation, annotation, commentId, documentModel.getText(),
                                runIndexInParagraph);
                    } catch (Docx4JException e) {
                        throw new ExportException("The annotation could not be applied to the run as a comment.", e);
                    }

                    appliedAnnotations.add(annotation);
                }

                // Update the paragraph that contains the run that got
                // annotated. This code unobtrusively changes the documentModel
                // parameter.
                ParagraphModel paragraphModel = runToApplyAnnotation.getParent();
                List<RunModel> runModels = Docx4jAbstraction.createRunModels(paragraphModel.getBegin(), paragraphModel);
                paragraphModel.setRunModels(runModels);
            }
        }

        return appliedAnnotations;
    }

    @Override
    public void addRatAnomaliesToCustomXmlPart(DocumentModel documentModel,
            List<RatAnomalyModel> previousAppliedComments, List<RatAnomalyModel> falsePositives,
            List<RatAnomalyModel> incorporatedImprovementProposal) throws ExportException {
        try {
            Docx4jXMLElements.createRatAnomalyCustomXmlPart(documentModel.getWml(), previousAppliedComments,
                    falsePositives, incorporatedImprovementProposal);
        } catch (Docx4JException e) {
            throw new ExportException("Adding rat anomalies failed.", e);
        }
    }

    /**
     * This function exports the annotated abstraction of the original document
     * ({@code WordprocessingMLPackage}), which is represented by the
     * abstraction: {@code documentModel}.
     * 
     * @param documentModel
     *            the abstraction of the original document to export.
     * @throws ExportException
     *             if a docx4j error occurs.
     */
    @Override
    public void exportDocument(DocumentModel documentModel, String directoryPath, String fileName, String fileType)
            throws ExportException {
        WordprocessingMLPackage wml = documentModel.getWml();
        String newFileName = fileName;

        if (!newFileName.endsWith("-rat")) {
            newFileName = newFileName + "-rat";
        }

        try {
            wml.save(new File(directoryPath + newFileName + "." + fileType));
        } catch (Docx4JException e) {
            throw new ExportException("The document could not be exported.", e);
        }

        LOGGER.info("Export document to: " + directoryPath + newFileName + "." + fileType);
        LOGGER.info("DOCUMENT EXPORT DONE");
    }

    private void applyAnnotationToRun(RunModel runToApplyAnnotation, RatAnomaly annotation, BigInteger commentId,
            String documentText, int runIndexInParagraph) throws Docx4JException {
        // Get indices
        int beginRun = runToApplyAnnotation.getBegin();
        int endRun = runToApplyAnnotation.getEnd();
        int beginAnnotation = annotation.getBegin();
        int endAnnotation = annotation.getEnd();

        // Create the xml elements for the comment annotation
        CommentRangeStart commentRangeStart = Docx4jXMLElements.createCommentRangeStart(commentId);
        CommentRangeEnd commentRangeEnd = Docx4jXMLElements.createCommentRangeEnd(commentId);
        R commentReference = Docx4jXMLElements.createCommentReference(commentId);

        // Apply the annotation to the run
        RPr format = runToApplyAnnotation.getRun().getRPr();
        P paragraph = runToApplyAnnotation.getParent().getParagraph();

        if (beginAnnotation > beginRun && endAnnotation < endRun) {
            // The text stands in the middle of the run
            LOGGER.debug("Case: 1");

            String frontPart = documentText.substring(beginRun, beginAnnotation);
            String annotationPart = documentText.substring(beginAnnotation, endAnnotation);
            String backPart = documentText.substring(endAnnotation, endRun);

            if (!annotationPart.equals(annotation.getCoveredText())) {
                LOGGER.error(
                        String.format(ANNOTATION_TEXT_DOES_NOT_MATCH, annotationPart, annotation.getCoveredText()));
                return;
            }

            R frontRun = Docx4jXMLElements.createRun(frontPart, paragraph, format, true);
            R annotationRun = Docx4jXMLElements.createRun(annotationPart, paragraph, format, false);
            R backRun = Docx4jXMLElements.createRun(backPart, paragraph, format, true);

            // Apply comment
            paragraph.getContent().remove(runIndexInParagraph);
            paragraph.getContent().add(runIndexInParagraph, frontRun);
            paragraph.getContent().add(runIndexInParagraph + 1, commentRangeStart);
            paragraph.getContent().add(runIndexInParagraph + 2, annotationRun);
            paragraph.getContent().add(runIndexInParagraph + 3, commentRangeEnd);
            paragraph.getContent().add(runIndexInParagraph + 4, commentReference);
            paragraph.getContent().add(runIndexInParagraph + 5, backRun);

        } else if (beginAnnotation > beginRun && endAnnotation == endRun) {
            // the text stands at the end of the run element
            LOGGER.debug("Case: 2");

            String frontPart = documentText.substring(beginRun, beginAnnotation);
            String annotationPart = documentText.substring(beginAnnotation, endAnnotation);

            if (!annotationPart.equals(annotation.getCoveredText())) {
                LOGGER.error(ANNOTATION_TEXT_DOES_NOT_MATCH
                        + Docx4jUtils.getTextOfRun(runToApplyAnnotation.getRun()).toString());
                return;
            }

            R frontRun = Docx4jXMLElements.createRun(frontPart, paragraph, format, true);
            R annotationRun = Docx4jXMLElements.createRun(annotationPart, paragraph, format, false);

            // Apply comment
            paragraph.getContent().remove(runIndexInParagraph);
            paragraph.getContent().add(runIndexInParagraph, frontRun);
            paragraph.getContent().add(runIndexInParagraph + 1, commentRangeStart);
            paragraph.getContent().add(runIndexInParagraph + 2, annotationRun);
            paragraph.getContent().add(runIndexInParagraph + 3, commentRangeEnd);
            paragraph.getContent().add(runIndexInParagraph + 4, commentReference);

        } else if (beginAnnotation == beginRun && endAnnotation < endRun) {
            // The text stands at the beginning of the run element
            LOGGER.debug("Case: 3");

            String annotationPart = documentText.substring(beginAnnotation, endAnnotation);
            String backPart = documentText.substring(endAnnotation, endRun);

            if (!annotationPart.equals(annotation.getCoveredText())) {
                LOGGER.error(ANNOTATION_TEXT_DOES_NOT_MATCH
                        + Docx4jUtils.getTextOfRun(runToApplyAnnotation.getRun()).toString());
                return;
            }

            R annotationRun = Docx4jXMLElements.createRun(annotationPart, paragraph, format, false);
            R backRun = Docx4jXMLElements.createRun(backPart, paragraph, format, true);

            // Apply comment
            paragraph.getContent().remove(runIndexInParagraph);
            paragraph.getContent().add(runIndexInParagraph, commentRangeStart);
            paragraph.getContent().add(runIndexInParagraph + 1, annotationRun);
            paragraph.getContent().add(runIndexInParagraph + 2, commentRangeEnd);
            paragraph.getContent().add(runIndexInParagraph + 3, commentReference);
            paragraph.getContent().add(runIndexInParagraph + 4, backRun);

        } else {
            // The word to annotate is in a single <w:r>-element
            LOGGER.debug("Case: 4");

            String annotationPart = documentText.substring(beginAnnotation, endAnnotation);
            R annotationRun = Docx4jXMLElements.createRun(annotationPart, paragraph, format, false);

            if (!annotationPart.equals(annotation.getCoveredText())) {
                LOGGER.error(ANNOTATION_TEXT_DOES_NOT_MATCH
                        + Docx4jUtils.getTextOfRun(runToApplyAnnotation.getRun()).toString());
                return;
            }

            // Apply comment
            paragraph.getContent().remove(runIndexInParagraph);
            paragraph.getContent().add(runIndexInParagraph, commentRangeStart);
            paragraph.getContent().add(runIndexInParagraph + 1, annotationRun);
            paragraph.getContent().add(runIndexInParagraph + 2, commentRangeEnd);
            paragraph.getContent().add(runIndexInParagraph + 3, commentReference);
        }
    }

    private RunModel findRunToAnnotate(DocumentModel document, RatAnomaly annotation) {
        List<ParagraphModel> paragraphModels = document.getParagraphModels();

        // Search for the index in the abstraction of the original document
        for (ParagraphModel p : paragraphModels) {

            // Find the paragraph
            if (annotation.getBegin() >= p.getBegin() && annotation.getEnd() <= p.getEnd()) {
                List<RunModel> runModel = p.getRunModels();

                // Find the run
                for (RunModel r : runModel) {
                    if (annotation.getBegin() >= r.getBegin() && annotation.getEnd() <= r.getEnd()) {
                        return r;
                    }
                }
            }
        }

        return null;
    }
}