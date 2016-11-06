package de.qaware.rat.codec.docx;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.interfaces.ImporterService;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.codec.docx.utils.Docx4jAbstraction;

/**
 * The Docx4jImporter class reads files of the OfficeOpenXML format.
 * 
 * <p>
 * The class reads a file from an byte array and finds relevant paragraphs as
 * well as relevant sentences within that paragraphs.
 * 
 * <p>
 * The class relies on the {@code docx4j} library for its implementation.
 *
 * @author Matthias Holdorf
 *
 */
public final class Docx4jImporter implements ImporterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Docx4jImporter.class);
    
    @Override
    public String getCapability() {
        return "docx";
    }

    /**
     * This function loads the original document and returns an object
     * containing a reference and an abstraction of that document.
     * 
     * @param dat
     *            the byte array representing the .docx file.
     * @return a documentModel yielding a reference to the original document and
     *         an abstraction of the content.
     * @throws ImportException
     *             if an import error occurs.
     */
    @Override
    public DocumentModel getDocumentModel(byte[] data) throws ImportException {
        WordprocessingMLPackage wml = null;
        DocumentModel documentModel = null;

        try {
            wml = readDocxFile(data);
        } catch (Docx4JException | SAXParseException e) {
            LOGGER.info("Loading the document failed.");
            return null;
        }

        try {
            documentModel = Docx4jAbstraction.abstractWordDocument(wml);
        } catch (JAXBException | Docx4JException e) {
            LOGGER.info("Abstracting the document failed.");
            return null;
        }

        return documentModel;
    }

    /**
     * Read a docx file by a byte array.
     * 
     * @param data
     *            the byte array to read the docx file from.
     * @return a {@code WordprocessingMLPackage} object, which represents a docx
     *         file in the {@code docx4j} libraray,
     * @throws Docx4JException
     *             if the docx file could not be read from the byte array.
     * @throws SAXParseException
     *             if the docx file could not be read from the byte array.
     */
    public WordprocessingMLPackage readDocxFile(byte[] data) throws Docx4JException, SAXParseException {
        InputStream file = new ByteArrayInputStream(data);
        return Docx4J.load(file);
    }
}