package de.qaware.rat.codec.docx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import de.qaware.rat.api.exceptions.ImportException;
import de.qaware.rat.api.models.DocumentModel;
import de.qaware.rat.common.ImporterUtils;

public class Docx4jImporterTest {
	@Test
	public void testReadOfficeOpenXMLFile() throws Docx4JException, IOException, SAXParseException {
		// Arrange
		byte[] data = ImporterUtils.readFile("src/test/resources/test-documents/large-document.docx");

		// Act
		WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

		// Assert
		assertNotNull(wml);
	}

	@Test(expected = Docx4JException.class)
	public void testReadOfficeOpenXMLFileForDocx4jException() throws Docx4JException, SAXParseException {
		// Arrange
		byte[] data = new byte[0];

		// Act
		new Docx4jImporter().readDocxFile(data);
	}
	
	@Test(expected = NullPointerException.class)
	public void testReadOfficeOpenXMLFileForIllegalArgumentException() throws Docx4JException, SAXParseException {
		// Arrange
		byte[] data = null;

		// Act
		new Docx4jImporter().readDocxFile(data);
	}

    @Test
    public void testGetDocument() throws IOException, ImportException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/test-documents/small-document.docx");

        // Act
        DocumentModel documentModel = new Docx4jImporter().getDocumentModel(data);

        // Assert
        assertEquals(239, documentModel.getText().length());
    }

    @Test
    public void testGetCapability() {
        // Act
        String capability = new Docx4jImporter().getCapability();

        // Assert
        assertEquals("docx", capability);
    }
}