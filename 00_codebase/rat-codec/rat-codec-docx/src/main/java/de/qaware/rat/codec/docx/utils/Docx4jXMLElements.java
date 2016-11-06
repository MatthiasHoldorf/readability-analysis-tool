package de.qaware.rat.codec.docx.utils;

import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.docx4j.XmlUtils;
import org.docx4j.customXmlProperties.DatastoreItem;
import org.docx4j.jaxb.Context;
import org.docx4j.model.datastorage.CustomXmlDataStorage;
import org.docx4j.model.datastorage.CustomXmlDataStorageImpl;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.CustomXmlDataStoragePart;
import org.docx4j.openpackaging.parts.CustomXmlDataStoragePropertiesPart;
import org.docx4j.openpackaging.parts.CustomXmlPart;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.CommentsPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart.AddPartBehaviour;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.Color;
import org.docx4j.wml.CommentRangeEnd;
import org.docx4j.wml.CommentRangeStart;
import org.docx4j.wml.Comments;
import org.docx4j.wml.Comments.Comment;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.P.Hyperlink;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.qaware.rat.api.models.RatAnomalyModel;

/**
 * The Docx4jXMLElements class provides utility functions to create XML objects
 * for the {@code docx4j} library.
 * 
 * @author Matthias
 *
 */
public final class Docx4jXMLElements {
    private static final Logger LOGGER = LoggerFactory.getLogger(Docx4jXMLElements.class);

    private static final ObjectFactory FACTORY = Context.getWmlObjectFactory();
    private static final org.docx4j.relationships.ObjectFactory REL_FACTORY = new org.docx4j.relationships.ObjectFactory();

    private Docx4jXMLElements() {
    }

    /**
     * Create a comment. Usage in "/word/comments.xml".
     *
     * <pre>
     *      <w:comment w:id="0" w:author="Matthias" w:date="2016-05-06T10:45:00Z"
     *          w:initials="M">
     *          <w:p w:rsidR="00FF707E" w:rsidRDefault="00FF707E">
     *              <w:pPr>
     *                  <w:pStyle w:val="Kommentartext" />
     *              </w:pPr>
     *              <w:r>
     *                  <w:rPr>
     *                      <w:rStyle w:val="Kommentarzeichen" />
     *                  </w:rPr>
     *                  <w:annotationRef />
     *              </w:r>
     *              <w:r>
     *                  <w:t>The text of the comment.</w:t>
     *              </w:r>
     *          </w:p>
     *      </w:comment>
     * </pre>
     * 
     * @param commentId
     *            the commentId to create the comment with.
     * @param author
     *            the author to create the comment with.
     * @param date
     *            the date to create the comment with.
     * @param text
     *            the text of the comment.
     * @return the created comment element.
     */
    /**
     * Create a comment. Usage in "/word/comments.xml".
     *
     * <pre>
     *      <w:comment w:id="0" w:author="Matthias" w:date="2016-05-06T10:45:00Z"
     *          w:initials="M">
     *          <w:p w:rsidR="00FF707E" w:rsidRDefault="00FF707E">
     *              <w:pPr>
     *                  <w:pStyle w:val="Kommentartext" />
     *              </w:pPr>
     *              <w:r>
     *                  <w:rPr>
     *                      <w:rStyle w:val="Kommentarzeichen" />
     *                  </w:rPr>
     *                  <w:annotationRef />
     *              </w:r>
     *              <w:r>
     *                  <w:t>The text of the comment.</w:t>
     *              </w:r>
     *          </w:p>
     *      </w:comment>
     * </pre>
     * 
     * @param commentId
     *            the commentId to create the comment with.
     * @param author
     *            the author to create the comment with.
     * @param initials
     *            the initials for the comment.
     * @param date
     *            the date to create the comment with.
     * @param severity
     *            the severity of the broken rule.
     * @param annotationName
     *            the name of the anomaly.
     * @param explanation
     *            the explanatory text of the comment.
     * @param hyperlink
     *            the hyperlink that refers to the documentation.
     * @param hashCode
     *            the hashCode of the comment.
     * @return the created comment element.
     */
    public static Comment createComment(BigInteger commentId, String author, String initials, Date date,
            String severity, String annotationName, String explanation, Hyperlink hyperlink, int hashCode) {
        // Convert date
        GregorianCalendar cregorianClaender = new GregorianCalendar();
        cregorianClaender.setTime(date);
        XMLGregorianCalendar commentDate = null;
        try {
            commentDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cregorianClaender);
        } catch (DatatypeConfigurationException e) {
            throw new IllegalArgumentException(e);
        }

        // Create a <w:comment> tag
        Comment comment = FACTORY.createCommentsComment();
        comment.setId(commentId);
        comment.setAuthor(String.valueOf(hashCode));
        comment.setInitials(initials);
        comment.setDate(commentDate);

        // Create highlight for severity
        Color color = FACTORY.createColor();
        switch (severity) {
        case "Minor":
            color.setVal("#359f35");
            break;
        case "Major":
            color.setVal("#e3ab25");
            break;
        case "Critical":
            color.setVal("#d82e2e");
            break;
        default:
            color.setVal("#000000");
            break;
        }

        // Create a <w:p> tag for the first line of the comment
        P firstLine = FACTORY.createP();

        // Create a <w:r> tag for severity
        RPr format = new RPr();
        format.setColor(color);
        format.setB(new BooleanDefaultTrue());
        R severityR = createRun(severity, firstLine, format, false);

        // Create a <w:r> tag for annotationName
        R annotationNameR = createRun(" - " + annotationName);

        firstLine.getContent().add(createRun("["));
        firstLine.getContent().add(severityR);
        firstLine.getContent().add(createRun("]"));
        firstLine.getContent().add(annotationNameR);
        comment.getContent().add(firstLine);
        // new line
        comment.getContent().add(FACTORY.createP());

        // Create a <w:p> tag for the second line of the comment
        P secondLine = FACTORY.createP();

        // Create a <w:r> tag for explanation
        R explanationR = createRun(explanation);
        secondLine.getContent().add(explanationR);
        comment.getContent().add(secondLine);
        // new line
        comment.getContent().add(FACTORY.createP());

        // Create a <w:p> tag for the third line of the comment
        P thirdLine = FACTORY.createP();
        comment.getContent().add(thirdLine);

        // Add the hyperlink
        thirdLine.getContent().add(hyperlink);

        return comment;
    }

    /**
     * Create a comments part, hence creating the file "/word/comments.xml".
     * 
     * @return the comments part that can be added to an existing document
     * @throws InvalidFormatException
     *             if the comments part cannot be created.
     */
    public static CommentsPart createCommentsPart() throws InvalidFormatException {
        CommentsPart commentsPart = new CommentsPart();
        Comments comments = FACTORY.createComments();
        commentsPart.setJaxbElement(comments);

        return commentsPart;
    }

    /**
     * This functions returns the comments part ("/word/comments.xml") of a wml
     * object.
     * 
     * @param wml
     *            the document to get the comments part from.
     * @return the comments part, if it exists. Otherwise null will be returned.
     */
    public static CommentsPart getCommentsPart(WordprocessingMLPackage wml) {
        return wml.getMainDocumentPart().getCommentsPart();
    }

    /**
     * Create a comment reference. Usage in "/word/document.xml" following a
     * <w:r> containing the <w:t> to comment.
     * 
     * <pre>
     *     <w:commentRangeStart w:id="0" />
     *     <w:r>
     * 	       <w:t>The text that should be commented.</w:t>
     *     </w:r>
     *     <w:r>
     *	       <w:t>The text that should be commented.</w:t>
     *     </w:r>
     *     <w:commentRangeEnd w:id="0" />
     *     <w:r>
     * 	       <w:rPr>
     * 	 	       <w:rStyle w:val="Kommentarzeichen" />
     * 	       </w:rPr>
     * 	   <w:commentReference w:id="0" />
     *     </w:r>
     * </pre>
     * 
     * @param commentId
     *            the commentId the comment reference refers to.
     * @return the created comment reference run element.
     */
    public static R createCommentReference(BigInteger commentId) {
        // Create a <w:r> tag
        R run = FACTORY.createR();

        // Create a <w:commentReference> tag
        R.CommentReference commentRef = FACTORY.createRCommentReference();
        run.getContent().add(commentRef);
        commentRef.setId(commentId);

        return run;
    }

    /**
     * Create a comment range start. Usage in "/word/document.xml" before a
     * <w:r> containing the <w:t> to comment.
     * 
     * <pre>
     *     <w:commentRangeStart w:id="0" />
     *     <w:r>
     * 	       <w:t>The text that should be commented.</w:t>
     *     </w:r>
     *     <w:r>
     *	       <w:t>The text that should be commented.</w:t>
     *     </w:r>
     *     <w:commentRangeEnd w:id="0" />
     *     <w:r>
     * 	       <w:rPr>
     * 	 	       <w:rStyle w:val="Kommentarzeichen" />
     * 	       </w:rPr>
     * 	   <w:commentReference w:id="0" />
     *     </w:r>
     * </pre>
     * 
     * @param commentId
     *            the commentId the comment range start refers to.
     * @return the created comment range start element.
     */
    public static CommentRangeStart createCommentRangeStart(BigInteger commentId) {
        CommentRangeStart commentRangeStart = FACTORY.createCommentRangeStart();
        commentRangeStart.setId(commentId);

        return commentRangeStart;
    }

    /**
     * Create a comment range end. Usage in "/word/document.xml" after a
     * <w:r> containing the <w:t> to comment.
     * 
     * <pre>
     *     <w:commentRangeStart w:id="0" />
     *     <w:r>
     * 	       <w:t>The text that should be commented.</w:t>
     *     </w:r>
     *     <w:r>
     *	       <w:t>The text that should be commented.</w:t>
     *     </w:r>
     *     <w:commentRangeEnd w:id="0" />
     *     <w:r>
     * 	       <w:rPr>
     * 	 	       <w:rStyle w:val="Kommentarzeichen" />
     * 	       </w:rPr>
     * 	   <w:commentReference w:id="0" />
     *     </w:r>
     * </pre>
     * 
     * @param commentId
     *            the commentId the comment range end refers to.
     * @return the created comment range end element.
     */
    public static CommentRangeEnd createCommentRangeEnd(BigInteger commentId) {
        CommentRangeEnd commentRangeEnd = FACTORY.createCommentRangeEnd();
        commentRangeEnd.setId(commentId);

        return commentRangeEnd;
    }

    /**
     * This method creates a hyperlink element in the comments part.
     * 
     * <p>
     * In order to achieve this, a relationship has to be created in the
     * comments part.
     * 
     * @param wml
     *            the document to get the comments part from.
     * @param url
     *            the url of the hyperlink.
     * @param text
     *            the text of the hyperlink.
     * @return the hyperlink element.
     * @throws JAXBException
     *             if unmarshling the hyperlink element fails.
     * @throws InvalidFormatException
     *             if the comments part cannot be retrieved.
     */
    public static Hyperlink createHyperlinkInCommentsPart(WordprocessingMLPackage wml, String url, String text)
            throws InvalidFormatException, JAXBException {
        Relationship rel = REL_FACTORY.createRelationship();
        rel.setType(Namespaces.HYPERLINK);
        rel.setTarget(url);
        rel.setTargetMode("External");

        PartName commentsPartName = new PartName("/word/comments.xml");
        Part commentsPart = wml.getParts().get(commentsPartName);
        RelationshipsPart commentRelationshipPart = commentsPart.getRelationshipsPart();
        commentRelationshipPart.addRelationship(rel);

        String hyperlink = "<w:hyperlink r:id=\"" + rel.getId()
                + "\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" "
                + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" >" + "<w:r>"
                + "<w:rPr>" + "<w:rStyle w:val=\"Hyperlink\" />" + "</w:rPr>" + "<w:t>" + text + "</w:t>" + "</w:r>"
                + "</w:hyperlink>";

        wml.getMainDocumentPart().getPropertyResolver().activateStyle("Hyperlink");

        return (Hyperlink) XmlUtils.unmarshalString(hyperlink);
    }

    /**
     * Create a docx4j run element (<w:r>) with an embedded text element
     * containing the runText String.
     * 
     * @param runText
     *            the text of the embedded text element (<w:t>) of the run.
     * @param parent
     *            the parent paragraph of the run.
     * @param format
     * @param setPreserve
     *            set to true if the text of the run should not ignore
     *            whitespace in the runText.
     * @return the created run element (<w:r>).
     */
    public static R createRun(String runText, P parent, RPr format, Boolean setPreserve) {
        R run = FACTORY.createR();
        run.setRPr(format);

        Text text = FACTORY.createText();
        text.setValue(runText);
        run.getContent().add(text);

        if (setPreserve) {
            text.setSpace("preserve");
        }

        text.setParent(runText);
        run.setParent((Object) parent);

        return run;
    }

    /**
     * Create a docx4j run element (<w:r>) with an embedded text element
     * containing the runText String.
     * 
     * @param runText
     *            the text of the embedded text element (<w:t>) of the run.
     * @return the created run element (<w:r>).
     */
    public static R createRun(String runText) {
        R run = FACTORY.createR();

        Text text = FACTORY.createText();
        text.setValue(runText);
        run.getContent().add(text);

        text.setParent(runText);

        return run;
    }

    /**
     * This method creates a custom xml part in the wml (docx document) to store
     * data about currently, previously and false positives comments.
     * 
     * @param wml
     *            the document to create the custom xml in.
     * @param previousAppliedComments
     *            comments that were previously applied in the document.
     * @param falsePositives
     *            comments that are marked as false positive by the user.
     * @param incorporatedImprovementProposal
     *            comments that were incorporated by the user.
     * @throws Docx4JException
     *             if the custom xml part cannot be retrieved or the data cannot
     *             be set.
     */
    public static void createRatAnomalyCustomXmlPart(WordprocessingMLPackage wml,
            List<RatAnomalyModel> previousAppliedComments, List<RatAnomalyModel> falsePositives,
            List<RatAnomalyModel> incorporatedImprovementProposal) throws Docx4JException {
        CustomXmlPart ratCustomXmlPart = getRatAnomalyCustomXmlPart(wml);

        // Change existing CustomDataStorage Part
        if (ratCustomXmlPart != null) {
            CustomXmlDataStoragePart storagePart = (CustomXmlDataStoragePart) ratCustomXmlPart;
            CustomXmlDataStorage data = new CustomXmlDataStorageImpl();
            data.setDocument(
                    createRatAnomalyDocument(previousAppliedComments, falsePositives, incorporatedImprovementProposal));
            storagePart.setData(data);
            
            LOGGER.debug(ratCustomXmlPart.getXML());
        } else {
            // Create new CustomDataStorage Part
            CustomXmlDataStoragePart customXmlDataStoragePart = new CustomXmlDataStoragePart(
                    new PartName("/customXml/rat1.xml"));
            CustomXmlDataStorage data = new CustomXmlDataStorageImpl();
            data.setDocument(
                    createRatAnomalyDocument(previousAppliedComments, falsePositives, incorporatedImprovementProposal));
            customXmlDataStoragePart.setData(data);

            wml.getMainDocumentPart().addTargetPart(customXmlDataStoragePart,
                    AddPartBehaviour.OVERWRITE_IF_NAME_EXISTS);

            addProperties(customXmlDataStoragePart);

            LOGGER.debug(customXmlDataStoragePart.getXML());
        }
    }

    private static void addProperties(CustomXmlDataStoragePart customXmlDataStoragePart) throws InvalidFormatException {
        CustomXmlDataStoragePropertiesPart part = new CustomXmlDataStoragePropertiesPart(
                new PartName("/customXml/ratProps1.xml"));
        org.docx4j.customXmlProperties.ObjectFactory of = new org.docx4j.customXmlProperties.ObjectFactory();
        DatastoreItem dsi = of.createDatastoreItem();
        String newItemId = "rat-custom-xml";
        dsi.setItemID(newItemId);
        part.setJaxbElement(dsi);

        customXmlDataStoragePart.addTargetPart(part);
    }

    private static Document createRatAnomalyDocument(List<RatAnomalyModel> previousAppliedComments,
            List<RatAnomalyModel> falsePositives, List<RatAnomalyModel> incorporatedImprovementProposal) {
        Document domDoc = XmlUtils.neww3cDomDocument();
        Element ratAnomalies = domDoc.createElement("rat-anomalies");

        for (RatAnomalyModel anomaly : previousAppliedComments) {
            ratAnomalies.appendChild(anomaly.getXmlRepresentation(domDoc, "previous-applied"));
        }

        for (RatAnomalyModel anomaly : falsePositives) {
            ratAnomalies.appendChild(anomaly.getXmlRepresentation(domDoc, "false-positives"));
        }

        for (RatAnomalyModel anomaly : incorporatedImprovementProposal) {
            ratAnomalies.appendChild(anomaly.getXmlRepresentation(domDoc, "incorporated-proposals"));
        }

        domDoc.appendChild(ratAnomalies);

        return domDoc;
    }

    /**
     * Retrieve the custom xml part that stores rat anomalies.
     * 
     * @param wml
     *            the document to retrieve the custom xml part from.
     * @return the custom xml part or null if not found.
     * @throws Docx4JException
     *             if the custom xml part cannot be accessed.
     */
    public static CustomXmlPart getRatAnomalyCustomXmlPart(WordprocessingMLPackage wml) throws Docx4JException {
        HashMap<String, CustomXmlPart> customParts = wml.getCustomXmlDataStorageParts();
        CustomXmlPart ratCustomXmlPart = null;

        for (Entry<String, CustomXmlPart> entry : customParts.entrySet()) {
            CustomXmlPart value = entry.getValue();

            if (value.getXML().contains("<rat-anomalies>")) {
                ratCustomXmlPart = value;
            }
        }

        return ratCustomXmlPart;
    }
}