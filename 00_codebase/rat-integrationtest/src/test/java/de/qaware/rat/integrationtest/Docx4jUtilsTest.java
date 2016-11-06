package de.qaware.rat.integrationtest;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.junit.Test;
import org.xml.sax.SAXParseException;

import de.qaware.rat.codec.docx.Docx4jImporter;
import de.qaware.rat.codec.docx.utils.Docx4jUtils;
import de.qaware.rat.common.ImporterUtils;

public class Docx4jUtilsTest {

    @Test
    public void testGetAllElementsFromDocumentByXpath() throws Docx4JException, JAXBException, IOException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/docx4j-utils/find-runs-in-paragraphs.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

        // Act
        List<Object> objects = Docx4jUtils.getAllElementsFromDocumentByXpath(wml.getMainDocumentPart(), "//w:p");
        List<P> paragraphs = new ArrayList<P>();

        for (Object o : objects) {
            P p = (P) o;
            paragraphs.add(p);
        }

        // Assert
        assertEquals(paragraphs.size(), 12);
        assertEquals(3, paragraphs.get(0).getContent().size());
        assertEquals(1, paragraphs.get(1).getContent().size());
        assertEquals(1, paragraphs.get(2).getContent().size());
        assertEquals(30, paragraphs.get(3).getContent().size());
        assertEquals(1, paragraphs.get(4).getContent().size());
        assertEquals(1, paragraphs.get(5).getContent().size());
        assertEquals(7, paragraphs.get(6).getContent().size());
        assertEquals(1, paragraphs.get(7).getContent().size());
        assertEquals(0, paragraphs.get(8).getContent().size());
        assertEquals(0, paragraphs.get(9).getContent().size());
        assertEquals(0, paragraphs.get(10).getContent().size());
        assertEquals(0, paragraphs.get(11).getContent().size());
    }

    @Test
    public void testGetRunsOfParagraph() throws Docx4JException, JAXBException, IOException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/docx4j-utils/find-runs-in-paragraphs-comments.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

        // Act
        List<Object> objects = Docx4jUtils.getAllElementsFromDocumentByXpath(wml.getMainDocumentPart(), "//w:p");
        List<P> paragraphs = new ArrayList<P>();

        for (Object o : objects) {
            P p = (P) o;
            paragraphs.add(p);
        }

        List<R> runsOfParagraph00 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(0));
        List<R> runsOfParagraph01 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(1));
        List<R> runsOfParagraph02 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(2));
        List<R> runsOfParagraph03 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(3));
        List<R> runsOfParagraph04 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(4));
        List<R> runsOfParagraph05 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(5));
        List<R> runsOfParagraph06 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(6));
        List<R> runsOfParagraph07 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(7));
        List<R> runsOfParagraph08 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(8));
        List<R> runsOfParagraph09 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(9));
        List<R> runsOfParagraph10 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(10));
        List<R> runsOfParagraph11 = Docx4jUtils.getRunsOfParagraph(paragraphs.get(11));

        // Assert
        assertEquals(1, runsOfParagraph00.size());
        assertEquals(1, runsOfParagraph01.size());
        assertEquals(4, runsOfParagraph02.size());
        assertEquals(24, runsOfParagraph03.size());
        assertEquals(1, runsOfParagraph04.size());
        assertEquals(1, runsOfParagraph05.size());
        assertEquals(5, runsOfParagraph06.size());
        assertEquals(1, runsOfParagraph07.size());
        assertEquals(0, runsOfParagraph08.size());
        assertEquals(0, runsOfParagraph09.size());
        assertEquals(0, runsOfParagraph10.size());
        assertEquals(0, runsOfParagraph11.size());
    }

    @Test
    public void testFindRelevantParagraphs() throws Docx4JException, IOException, JAXBException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/docx4j-utils/find-paragraphs.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

        // Act
        List<P> relevantParagraphs = Docx4jUtils.findRelevantParagraphs(wml.getMainDocumentPart());

        // Assert
        assertEquals(7, relevantParagraphs.size());
    }

    @Test
    public void testGetTextFromParagraph() throws Docx4JException, JAXBException, IOException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/docx4j-utils/get-text-from-paragraph.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

        // Act
        List<Object> objects = Docx4jUtils.getAllElementsFromDocumentByXpath(wml.getMainDocumentPart(), "//w:p");
        List<P> paragraphs = new ArrayList<P>();

        for (Object o : objects) {
            P p = (P) o;
            paragraphs.add(p);
        }

        String text = Docx4jUtils.getTextFromParagraph(paragraphs.get(0));

        // Assert
        assertEquals("Der Text in einem Paragraphen.", text);
    }

    @Test
    public void testGetTextOfRun() throws Docx4JException, JAXBException, IOException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/docx4j-utils/get-text-from-run.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

        // Act
        List<Object> objects = Docx4jUtils.getAllElementsFromDocumentByXpath(wml.getMainDocumentPart(), "//w:r");
        List<R> runs = new ArrayList<R>();

        for (Object o : objects) {
            R r = (R) o;
            runs.add(r);
        }

        String text = Docx4jUtils.getTextOfRun(runs.get(0)).getValue();

        // Assert
        assertEquals("Der Text in einem Run.", text);
    }

    @Test
    public void testGetAllElementsFromObjectByClassName() throws Docx4JException, JAXBException, IOException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/docx4j-utils/find-runs-in-paragraphs.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);

        // Act
        List<Object> objects = Docx4jUtils.getAllElementsFromObjectByClassName(wml.getMainDocumentPart(), P.class);
        List<P> paragraphs = new ArrayList<P>();

        for (Object o : objects) {
            P p = (P) o;
            paragraphs.add(p);
        }

        // Assert
        assertEquals(paragraphs.size(), 12);
        assertEquals(3, paragraphs.get(0).getContent().size());
        assertEquals(1, paragraphs.get(1).getContent().size());
        assertEquals(1, paragraphs.get(2).getContent().size());
        assertEquals(30, paragraphs.get(3).getContent().size());
        assertEquals(1, paragraphs.get(4).getContent().size());
        assertEquals(1, paragraphs.get(5).getContent().size());
        assertEquals(7, paragraphs.get(6).getContent().size());
        assertEquals(1, paragraphs.get(7).getContent().size());
        assertEquals(0, paragraphs.get(8).getContent().size());
        assertEquals(0, paragraphs.get(9).getContent().size());
        assertEquals(0, paragraphs.get(10).getContent().size());
        assertEquals(0, paragraphs.get(11).getContent().size());
    }

    @Test
    public void testDetectSentencesFromParagraph() throws IOException, Docx4JException, JAXBException, SAXParseException {
        // Arrange
        byte[] data = ImporterUtils.readFile("src/test/resources/documents/docx4j-utils/find-sentences.docx");
        WordprocessingMLPackage wml = new Docx4jImporter().readDocxFile(data);
        List<P> relevantParagraphs = Docx4jUtils.findRelevantParagraphs(wml.getMainDocumentPart());

        // Act
        List<String> sentences = Docx4jUtils.detectSentencesFromParagraph(relevantParagraphs, Locale.GERMANY);

        // Assert
        assertEquals(7, sentences.size());
    }
}