package de.qaware.rat.statistics;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.*;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.qaware.rat.common.ClassPathUtils;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpSegmenter;

public class ReadabilityFormulaTest {
    private static AnalysisEngine analysisEngine;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        AnalysisEngineDescription seg = createEngineDescription(OpenNlpSegmenter.class);
        AnalysisEngineDescription pos = createEngineDescription(OpenNlpPosTagger.class);
        AnalysisEngineDescription analysisEngineDescription = createEngineDescription(seg, pos);
        analysisEngine = UIMAFramework.produceAnalysisEngine(analysisEngineDescription);
    }
    
    @Test
    public void testCalculateFleschReadingEase() throws AnalysisEngineProcessException, ResourceInitializationException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);
        
        // Act
        double fleschReadingEaseScore = ReadabilityFormula.calculateFleschReadingEase(jCas);
        
        // Assert
        assertEquals(21, fleschReadingEaseScore, 0.5);
    }
    
    @Test
    public void testCalculateFleschReadingEaseGer() throws AnalysisEngineProcessException, ResourceInitializationException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);
        
        // Act
        double fleschReadingEaseScore = ReadabilityFormula.calculateFleschReadingEaseAmstad(jCas);
        
        // Assert
        assertEquals(47, fleschReadingEaseScore, 0.5);
    }
    
    @Test
    public void testCalculateWienerSachtextformel() throws AnalysisEngineProcessException, ResourceInitializationException {
        // Arrange
        String text = ClassPathUtils.loadAsString("averageNumberOfCharactersPerSentence.txt");

        JCas jCas = analysisEngine.newJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText(text);
        analysisEngine.process(jCas);
        
        // Act
        double wienerSachtextformelScore = ReadabilityFormula.calculateWienerSachtextformel(jCas);
        
        // Assert
        assertEquals(11, wienerSachtextformelScore, 0.5);
    }
}