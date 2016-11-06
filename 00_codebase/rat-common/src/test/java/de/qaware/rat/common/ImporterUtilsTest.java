package de.qaware.rat.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

public class ImporterUtilsTest {
    @Test
    public void testReadFile() throws IOException {
        // Act
        byte[] data = ImporterUtils.readFile("src/test/resources/large-document.docx");

        // Assert
        assertNotNull(data);
    }

    @Test(expected = IOException.class)
    public void testReadFileIOException() throws IOException {
        // Act
        byte[] data = ImporterUtils.readFile("does-not-exist.docx");

        // Assert
        assertNotNull(data);
    }

    @Test
    public void testDetectSentences() {
        // Arrange
        String text = "Dies ist ein Beipsiel Text. Hier sollen alle Sätze erkannt werden! Ob dies gelingt? Es sollten in Summe 4 sein";

        // Act
        List<String> sentences = ImporterUtils.detectSentences(text, Locale.GERMANY);

        // Assert
        assertEquals(sentences.size(), 4);
    }

    @Test
    public void testGetFilePathsFromDirectory() throws IOException, URISyntaxException {
        // Arrange
        String path = ClassPathUtils.getPath("large-document.docx");
        String directoryPath = ImporterUtils.getDirectoryPathFromFilePath(path, "\\");
        
        // Act
        List<String> filePaths = ImporterUtils.getFilePathsFromDirectory(directoryPath);

        // Assert
        assertTrue(4 <= filePaths.size());
    }

    @Test
    public void testDetectFileTypeForDocx() throws IOException, URISyntaxException {
        // Arrange
        String path = ClassPathUtils.getPath("large-document.docx");

        // Act
        String fileType = ImporterUtils.detectFileType(path);

        // Assert
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", fileType);
    }

    @Test
    public void testDetectFileTypeForDoc() throws IOException, URISyntaxException {
        // Arrange
        String path = ClassPathUtils.getPath("testDetectFileTypeForDoc.doc");

        // Act
        String fileType = ImporterUtils.detectFileType(path);
        
        // Assert
        assertEquals("application/msword", fileType);
    }

    @Test
    public void testDetectFileExtensionForDoc() throws IOException, URISyntaxException {
        // Arrange
        String path = ClassPathUtils.getPath("testDetectFileTypeForDoc.doc");

        // Act
        String fileExtension = ImporterUtils.detectFileExtension(path);

        // Assert
        assertEquals("doc", fileExtension);
    }

    @Test
    public void testDetectFileExtensionForDocx() throws IOException, URISyntaxException {
        // Arrange
        String path = ClassPathUtils.getPath("large-document.docx");

        // Act
        String fileExtension = ImporterUtils.detectFileExtension(path);

        // Assert
        assertEquals("docx", fileExtension);
    }

    @Test
    public void testGetFileNameFromPath() throws URISyntaxException {
        // Arrange
        String path = ClassPathUtils.getPath("large-document.docx");

        // Act
        String fileName = ImporterUtils.getFileNameFromPath(path);

        // Assert
        assertEquals("large-document", fileName);
    }

    @Test
    public void testGetVersion() {
        // Act
        String version = ImporterUtils.getVersion();

        // Assert
        assertNotNull(version);
    }
}