package de.qaware.rat.api.interfaces;

import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.models.DocumentModel;

/**
 * The ImporterService interface is implemented by classes which provide
 * functionality to extract text from a document.
 * 
 * @author Matthias
 *
 */
public interface ImporterService extends ServiceProvider {
    /**
     * This function loads and abstracts a file (i.e., the original document) to
     * a {@link de.qaware.rat.api.models.DocumentModel}.
     * 
     * @param data
     *            the byte array representing the file's content.
     * @return a document ({@link de.qaware.rat.api.models.DocumentModel}).
     * @throws ImportException
     *             if an import error occurs.
     */
    DocumentModel getDocumentModel(byte[] data) throws ImportException;
}