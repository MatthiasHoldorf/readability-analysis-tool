package de.qaware.rat.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LevenshteinDistanceTest {
    @Test
    public void testComputeLevenshteinDistance() {
        // Arrange
        String text = "Der Coach ist vor allem bei der erstmaligen Durchführung eines Projektes dem Extreme Programming Modell wichtig: Er ist mit allen Aspekten und Abläufen von Extreme Programming vertraut und leitet die Teilnehmer so an, dass sie ihre entsprechende Rolle einnehmen und nach den genannten Extrem Programming Werten, Prinzipien und Techniken handeln.";
        String almostEqual = "Der Coach ist vor allem bei der erstmaligen Durchführung eines Projektes nach dem Extreme Programming Modell wichtig: Er ist mit allen Aspekten und Abläufen von Extreme Programming vertraut und leitet die Teilnehmer so an, dass sie ihre entsprechende Rolle einnehmen und nach den genannten Extrem Programming Werten, Prinzipien und Techniken handeln.";

        // Act
        int value = LevenshteinDistance.computeLevenshteinDistance(text, almostEqual);

        // Assert
        assertEquals(5, value);
    }
}