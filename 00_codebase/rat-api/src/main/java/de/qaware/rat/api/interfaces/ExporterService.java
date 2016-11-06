package de.qaware.rat.api.interfaces;

import java.util.Collection;
import java.util.List;

import de.qaware.rat.api.exceptions.ExportException;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.api.models.RatAnomalyModel;
import de.qaware.rat.type.RatAnomaly;

/**
 * The ExporterService interface is implemented by classes which provide
 * functionality to apply annotations, add hash codes of annotations and save
 * the an original document.
 * 
 * @author Matthias
 *
 */
public interface ExporterService extends ServiceProvider {
    /**
     * Apply annotations from the jCas object to the original document.
     * 
     * @param documentModel
     *            the document model containing a reference to the original
     *            document to annotate.
     * @param annotations
     *            the annotations to apply.
     * @throws ExportException
     *             if an export error occurs.
     * @return the annotations that were applied.
     */
    List<RatAnomaly> applyAnnotations(DocumentModel documentModel, Collection<RatAnomaly> annotations)
            throws ExportException;

    /**
     * Add rat anomalies of previous applied comments, false positive and
     * incorporated anomalies to a custom xml part in the word document.
     * 
     * @param documentModel
     *            the document model containing a reference to the original
     *            document to add the comments hash codes to.
     * @param previousAppliedComments
     *            the rat anomalies of previous applied comments.
     * @param falsePositives
     *            the rat anomalies of actual false positive annotations.
     * @param incorporatedImprovementProposal
     *            the rat anomalies that were incorporated by a user.
     * @throws ExportException
     *             if an export error occurs.
     */
    void addRatAnomaliesToCustomXmlPart(DocumentModel documentModel, List<RatAnomalyModel> previousAppliedComments,
            List<RatAnomalyModel> falsePositives, List<RatAnomalyModel> incorporatedImprovementProposal)
            throws ExportException;

    /**
     * Export the annotated documentModel with the prefix "rat", e.g.
     * "fileName-rat.fileType" at the same location as the original document.
     * 
     * <p>
     * If the file already has the extension "-rat", the export overwrites the
     * file.
     * 
     * <p>
     * This procedure is chosen, in order to keep a safe copy of the original
     * document.
     * 
     * @param documentModel
     *            the document model to export.
     * @param filePath
     *            the file path of the original document.
     * @param fileName
     *            the file name of the original document.
     * @param fileExtension
     *            the file extension of the original document.
     * @throws ExportException
     *             if an export error occurs.
     */
    void exportDocument(DocumentModel documentModel, String filePath, String fileName, String fileExtension)
            throws ExportException;
}