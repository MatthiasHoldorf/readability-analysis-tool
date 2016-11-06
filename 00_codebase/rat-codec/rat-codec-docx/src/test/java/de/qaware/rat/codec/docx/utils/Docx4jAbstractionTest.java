package de.qaware.rat.codec.docx.utils;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.codec.docx.Docx4jImporter;
import de.qaware.rat.common.ImporterUtils;

public class Docx4jAbstractionTest {

    @Test
    public void testAbstractWordDocumentForSmallDocument() throws Docx4JException, JAXBException, IOException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/test-documents/small-document.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

        // Act
        DocumentModel documentModel = Docx4jAbstraction.abstractWordDocument(wml);

        // Assert
        assertEquals(239, documentModel.getText().length());
    }

    @Test
    public void testAbstractWordDocumentLargeDocument() throws Docx4JException, JAXBException, IOException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/test-documents/large-document.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

        // Act
        DocumentModel documentModel = Docx4jAbstraction.abstractWordDocument(wml);

        // Assert
        assertEquals(1164, documentModel.getText().length());
    }

    @Test
    public void testAbstractWordDocumentFiftyPageDocument() throws Docx4JException, JAXBException, IOException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/test-documents/45-page-9500-words-assignment.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

        // Act
        DocumentModel documentModel = Docx4jAbstraction.abstractWordDocument(wml);

        // Assert
        assertEquals(75610, documentModel.getText().length());
    }
}