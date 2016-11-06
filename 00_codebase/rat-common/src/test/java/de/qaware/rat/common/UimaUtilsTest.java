package de.qaware.rat.common;

import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.Test;

public class UimaUtilsTest {

    @Test
    public void testDetectLanguageForGerman() throws AnalysisEngineProcessException, ResourceInitializationException {
        // Arrange
        String text = "Dies ist ein deutscher Text.";

        // Act
        String language = UimaUtils.detectLanguage(text);
        
        // Assert
        assertEquals("de", language);
    }
    
    @Test
    public void testDetectLanguageForEnglish() throws AnalysisEngineProcessException, ResourceInitializationException {
        // Arrange
        String text = "This is an text in English.";

        // Act
        String language = UimaUtils.detectLanguage(text);
        
        // Assert
        assertEquals("en", language);
    }
    
    @Test
    public void testDetectLanguageForSpanish() throws AnalysisEngineProcessException, ResourceInitializationException {
        // Arrange
        String text = "Se trata de un texto en español.";

        // Act
        String language = UimaUtils.detectLanguage(text);
        
        // Assert
        assertEquals("es", language);
    }
    
    @Test
    public void testDetectLanguageForFrench() throws AnalysisEngineProcessException, ResourceInitializationException {
        // Arrange
        String text = "Ceci est un texte en français.";

        // Act
        String language = UimaUtils.detectLanguage(text);
        
        // Assert
        assertEquals("fr", language);
    }
}