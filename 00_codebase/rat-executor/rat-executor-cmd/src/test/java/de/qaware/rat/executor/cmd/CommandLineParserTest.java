package de.qaware.rat.executor.cmd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.qaware.rat.api.exceptions.ParserException;
import de.qaware.rat.api.models.ArgumentsModel;

public class CommandLineParserTest {
    @Test
    public void testGetFilePaths() throws ParserException {
        // Arrange
        String[] args = new String[] { "src/test/resources/documents/existing-file/sample.docx" };

        // Act
        ArgumentsModel arguments = CommandLineParser.parse(args);

        // Assert
        assertEquals(arguments.getFilePaths().size(), 1);
    }

    @Test
    public void testGetMultipleFilePaths() throws ParserException {
        // Arrange
        String[] args = new String[] { "src/test/resources/documents/existing-file/sample.docx",
                "src/test/resources/documents/multiple-documents/large-document.docx",
                "src/test/resources/documents/multiple-documents/45-page-9500-words-assignment.docx" };

        // Act
        ArgumentsModel arguments = CommandLineParser.parse(args);

        // Assert
        assertEquals(arguments.getFilePaths().size(), 3);
    }

    @Test
    public void testGetConfigurationPath() throws ParserException {
        // Arrange
        String[] cArgs = new String[] { "-c src/test/resources/documents/existing-file/rat-config.xml" };
        String[] configurationPathArgs = new String[] {
                "-configurationPath src/test/resources/documents/existing-file/rat-config.xml" };

        // Act
        ArgumentsModel cArguments = CommandLineParser.parse(cArgs);
        ArgumentsModel configurationPathArguments = CommandLineParser.parse(configurationPathArgs);

        // Assert
        assertEquals(cArguments.getConfigurationPath(), "src/test/resources/documents/existing-file/rat-config.xml");
        assertEquals(configurationPathArguments.getConfigurationPath(),
                "src/test/resources/documents/existing-file/rat-config.xml");
    }

    @Test
    public void testGetoutputDirectory() throws ParserException {
        // Arrange
        String[] args = new String[] { "-c src/test/resources/documents/existing-file/rat-config.xml",
                "-o path/to/file" };

        // Act
        ArgumentsModel arguments = CommandLineParser.parse(args);

        // Assert
        assertEquals(arguments.getOutputDirectory(), "path/to/file");
    }

    @Test
    public void testParse() throws ParserException {
        // Arrange
        String[] cArgs = new String[] { "-c src/test/resources/documents/existing-file/rat-config.xml",
                "src/test/resources/documents/existing-file/sample.docx" };
        String[] configurationPathArgs = new String[] {
                "-configurationPath src/test/resources/documents/existing-file/rat-config.xml",
                "src/test/resources/documents/existing-file/sample.docx",
                "src/test/resources/documents/multiple-documents/large-document.docx",
                "src/test/resources/documents/multiple-documents/45-page-9500-words-assignment.docx" };

        // Act
        ArgumentsModel cArguments = CommandLineParser.parse(cArgs);
        ArgumentsModel configurationPathArguments = CommandLineParser.parse(configurationPathArgs);

        // Assert
        assertEquals(cArguments.getConfigurationPath(), "src/test/resources/documents/existing-file/rat-config.xml");
        assertEquals(cArguments.getFilePaths().size(), 1);
        assertEquals(configurationPathArguments.getConfigurationPath(),
                "src/test/resources/documents/existing-file/rat-config.xml");
        assertEquals(configurationPathArguments.getFilePaths().size(), 3);
    }

    @Test
    public void testParseForDisplayingTheHelpMenu() throws ParserException {
        // Arrange
        String[] args1 = new String[] { "-c path/to/config/file", "-h" };
        String[] args2 = new String[] { "-f docx", "--help" };
        String[] args3 = new String[] {};

        // Act
        ArgumentsModel arguments1 = CommandLineParser.parse(args1);
        ArgumentsModel arguments2 = CommandLineParser.parse(args2);
        ArgumentsModel arguments3 = CommandLineParser.parse(args3);

        // Assert
        assertEquals(null, arguments1);
        assertEquals(null, arguments2);
        assertEquals(null, arguments3);
    }
}