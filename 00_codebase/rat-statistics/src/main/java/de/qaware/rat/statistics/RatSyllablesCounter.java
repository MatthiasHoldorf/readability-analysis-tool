package de.qaware.rat.statistics;

import de.tudarmstadt.ukp.dkpro.core.readability.measure.WordSyllableCounter;

/**
 * The {@code RatSyllablesCounter} provides functions to count syllables of a
 * word in multiple languages.
 * 
 * @author Matthias
 *
 */
public class RatSyllablesCounter {
    private WordSyllableCounter syllableCounter;

    /**
     * Create an object of the {@code RatSyllablesCounter} class.
     * 
     * @param languageCode
     *            the language to count the syllables for.
     */
    public RatSyllablesCounter(String languageCode) {
        syllableCounter = new WordSyllableCounter(languageCode);
    }

    /**
     * Count syllables for multiple words.
     * 
     * @param words
     *            the words to count the syllables for.
     * @return the number of syllables.
     */
    public int countSyllables(Iterable<String> words) {
        return syllableCounter.countSyllables(words);
    }

    /**
     * Count syllables for a single word.
     * 
     * @param word
     *            the word to count the syllables for.
     * @return the number of syllables.
     */
    public int countSyllables(String word) {
        return syllableCounter.countSyllables(word);
    }
}