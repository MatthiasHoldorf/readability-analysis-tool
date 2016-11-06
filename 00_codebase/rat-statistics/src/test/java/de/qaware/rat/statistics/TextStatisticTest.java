package de.qaware.rat.statistics;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.qaware.rat.common.ClassPathUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;

public class TextStatisticTest {
    private static AnalysisEngine analysisEngine;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        AnalysisEngineDescription seg = createEngineDescription(OpenNlpSegmenter.class);
        AnalysisEngineDescription pos = createEngineDescription(OpenNlpPosTagger.class);
        AnalysisEngineDescription analysisEngineDescription = createEngineDescription(seg, pos);
        analysisEngine = UIMAFramework.produceAnalysisEngine(analysisEngineDescription);
    }

    @Test
    public void testPercentageOfWordsWithXOrMoreCharacters()
            throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        float wordCount = TextStatistic.percentageOfWordsWithXOrMoreCharacters(jCas, 6);

        // Assert
        assertEquals(0.47, wordCount, 0.05);
    }

    @Test
    public void testPercentageOfWordsWithXSyllables()
            throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        float percentage = TextStatistic.percentageOfWordsWithXSyllables(jCas, 1);

        // Assert
        assertEquals(0.46, percentage, 0.05);
    }

    @Test
    public void testPercentageOfWordsWithXOrMoreSyllables()
            throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        float wordCount = TextStatistic.percentageOfWordsWithXOrMoreSyllables(jCas, 3);

        // Assert
        assertEquals(0.25, wordCount, 0.05);
    }

    @Test
    public void testGetSpeakingTime() throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        String speakingTime = TextStatistic.getSpeakingTime(jCas);

        // Assert
        assertEquals("00:01:12", speakingTime);
    }

    @Test
    public void testGetReadingTime() throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        String readingTime = TextStatistic.getReadingTime(jCas);

        // Assert
        assertEquals("00:00:40", readingTime);
    }

    @Test
    public void testAverageNumberOfSyllablesPerWord()
            throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        double averageNumberOfSyllablesPerWord = TextStatistic.averageNumberOfSyllablesPerWord(jCas);

        // Assert
        assertEquals(2, averageNumberOfSyllablesPerWord, 0.05);
    }

    @Test
    public void testGetWordsInDocument() throws AnalysisEngineProcessException, ResourceInitializationException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        Collection<Token> wordsInDocument = TextStatistic.getWordsInDocument(jCas);

        // Assert
        assertEquals(151, wordsInDocument.size());
    }

    @Test
    public void testNumberOfSentencesInDocument()
            throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        double count = TextStatistic.numberOfSentencesInDocument(jCas);

        // Assert
        assertEquals(11, count, 0);
    }
    
    @Test
    public void testNumberOfSyllablesInDocument()
            throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        int count = TextStatistic.numberOfSyllablesInDocument(jCas);

        // Assert
        assertEquals(307, count);
    }
    
    @Test
    public void testNumberOfCharactersInDocument()
            throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        int count = TextStatistic.numberOfCharactersInDocument(jCas);

        // Assert
        assertEquals(963, count);
    }

    @Test
    public void testAverageNumberOfWordsPerSentence()
            throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        double count = TextStatistic.averageNumberOfWordsPerSentence(jCas);

        // Assert
        assertEquals(13.72, count, 0.5);
    }

    @Test
    public void testAverageNumberOfSyllablesPerSentence()
            throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        double count = TextStatistic.averageNumberOfSyllablesPerSentence(jCas);

        // Assert
        assertEquals(27.9, count, 0.5);
    }

    @Test
    public void testAverageNumberOfCharactersPerSentence()
            throws ResourceInitializationException, IOException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        double average = TextStatistic.averageNumberOfCharactersPerSentence(jCas);

        // Assert
        assertEquals(87.5, average, 0.5);
    }

    @Test
    public void testGetWordsInSentence() throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
        Sentence sentence = sentences.iterator().next();

        // Act
        int count = TextStatistic.getWordsInSentence(jCas, sentence).size();

        // Assert
        assertEquals(12, count);
    }
    
    @Test
    public void testLongestSentence() throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        String result = TextStatistic.getLongestSentence(jCas);
        
        // Assert
        assertEquals(136, result.length());
    }
    
    @Test
    public void testLongestWordBySyllables() throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        String result = TextStatistic.getLongestWordBySyllables(jCas);

        // Assert
        assertEquals(20, result.length());
    }
    
    @Test
    public void testLongestWordByCharacters() throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        String result = TextStatistic.getLongestWordByCharacters(jCas);

        // Assert
        assertEquals(20, result.length());
    }
    
    @Test
    public void testGetXMostUsedWordsByPOSValueType() throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        Map<String, Float> result = TextStatistic.getXMostUsedWordsByPOSValueType(jCas, "NN", 3);

        // Assert
        assertEquals(3, result.size());
    }
    

    @Test
    public void testGetPercentageOfUsedWordsByPOSType() throws ResourceInitializationException, AnalysisEngineProcessException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);

        // Act
        Float result = TextStatistic.getPercentageOfUsedWordsByPOSType(jCas, "NN");

        // Assert
        assertEquals(0.31, result, 0.5);
    }
}