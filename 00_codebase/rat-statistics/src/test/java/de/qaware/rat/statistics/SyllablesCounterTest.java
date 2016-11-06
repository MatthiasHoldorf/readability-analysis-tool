package de.qaware.rat.statistics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SyllablesCounterTest {

    @Test
    public void testCountSyllablesForSingleWordInGerman() {
        // Arrange
        String monosyllabic = "Tür";
        String syllables = "Grundstücksverkehrsgenehmigungszuständigkeitsübertragungsverordnung";
        
        // Act
        RatSyllablesCounter syllablesCounter = new RatSyllablesCounter("de");
        int countMonosyllabic = syllablesCounter.countSyllables(monosyllabic);
        int countSyllables = syllablesCounter.countSyllables(syllables);
        
        // Assert
        assertEquals(1, countMonosyllabic);
        assertEquals(19, countSyllables);
    }
    
    @Test
    public void testCountSyllablesForSingleWordInEnglish() {
        // Arrange
        String monosyllabic = "Strengths";
        String syllables = "Unimaginatively";

        // Act
        RatSyllablesCounter syllablesCounter = new RatSyllablesCounter("en");
        int countMonosyllabic = syllablesCounter.countSyllables(monosyllabic);
        int countSyllables = syllablesCounter.countSyllables(syllables);
        
        // Assert
        assertEquals(1, countMonosyllabic);
        assertEquals(7, countSyllables);
    }
}