package de.qaware.rat.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * The {@code TextStatistic} class computes text statistic based on the analysis
 * data stored in jCas object.
 * 
 * @author Matthias
 *
 */
public final class TextStatistic {
    private static final List<String> FILTER_TOKEN = Arrays.asList(".", "!", "?", ",", ":", ";", "–", "(", ")", "\"",
            "'");
    private static final String READING_TIME_FORMAT = "%02d:%02d:%02d";

    private TextStatistic() {
    }

    /**
     * Calculate the reading time of a text.
     * 
     * <p>
     * The average reading time is estimated by 225 words per minute.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return a formatted string of the reading time hh:mm:ss.
     */
    public static String getReadingTime(JCas jCas) {
        int wordCount = getWordsInDocument(jCas).size();
        // This equals 225 words per minute
        long secondsToRead = (long) (wordCount / 0.00375);
        return parseTime(secondsToRead, READING_TIME_FORMAT);
    }

    /**
     * Calculate the speaking time of a text.
     * 
     * <p>
     * The average reading time is estimated by 125 words per minute.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return a formatted string of the speaking time hh:mm:ss.
     */
    public static String getSpeakingTime(JCas jCas) {
        int wordCount = getWordsInDocument(jCas).size();
        // This equals to 125 words per minute
        long secondsToSpeak = (long) (wordCount / 0.002083);
        return parseTime(secondsToSpeak, READING_TIME_FORMAT);
    }

    /**
     * Calculate the percentage of words with x or more characters in text.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param x
     *            the number of characters.
     * @return the percentage.
     */
    public static float percentageOfWordsWithXOrMoreCharacters(JCas jCas, int x) {
        Collection<Token> wordsInDocument = getWordsInDocument(jCas);
        int wordsCount = 0;

        Iterator<Token> iter = wordsInDocument.iterator();
        while (iter.hasNext()) {
            if (iter.next().getCoveredText().length() >= x) {
                wordsCount++;
            }
        }

        return (float) wordsCount / wordsInDocument.size();
    }

    /**
     * Calculate the percentage of words with x or more syllables in text.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param x
     *            the number of syllables.
     * @return the percentage.
     */
    public static float percentageOfWordsWithXOrMoreSyllables(JCas jCas, int x) {
        Collection<Token> wordsInDocument = getWordsInDocument(jCas);
        RatSyllablesCounter syllablesCounter = new RatSyllablesCounter("de");
        int wordsCount = 0;

        Iterator<Token> iter = wordsInDocument.iterator();
        while (iter.hasNext()) {
            if (syllablesCounter.countSyllables(iter.next().getCoveredText()) >= x) {
                wordsCount++;
            }
        }

        return (float) wordsCount / wordsInDocument.size();
    }

    /**
     * Calculate the percentage of words with x syllables in text.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param x
     *            the number of syllables.
     * @return the percentage.
     */
    public static float percentageOfWordsWithXSyllables(JCas jCas, int x) {
        Collection<Token> wordsInDocument = getWordsInDocument(jCas);
        RatSyllablesCounter syllablesCounter = new RatSyllablesCounter("de");
        int wordsCount = 0;

        Iterator<Token> iter = wordsInDocument.iterator();
        while (iter.hasNext()) {
            if (syllablesCounter.countSyllables(iter.next().getCoveredText()) == x) {
                wordsCount++;
            }
        }

        return (float) wordsCount / wordsInDocument.size();
    }

    /**
     * Calculates the number of sentences in the document.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the number of sentences in the document.
     */
    public static int numberOfSentencesInDocument(JCas jCas) {
        return JCasUtil.select(jCas, Sentence.class).size();
    }

    /**
     * Calculates the number of words in the document.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the number of words in the document.
     */
    public static int numberOfWordsInDocument(JCas jCas) {
        return getWordsInDocument(jCas).size();
    }

    /**
     * Calculates the number of syllables in the document.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the number of syllables in the document.
     */
    public static int numberOfSyllablesInDocument(JCas jCas) {
        Collection<Token> wordsInDocument = getWordsInDocument(jCas);
        RatSyllablesCounter syllablesCounter = new RatSyllablesCounter("de");
        int syllablesCount = 0;

        Iterator<Token> iter = wordsInDocument.iterator();
        while (iter.hasNext()) {
            syllablesCount += syllablesCounter.countSyllables(iter.next().getCoveredText());
        }

        return syllablesCount;
    }

    /**
     * Calculates the number of characters in the document.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the number of characters in the document.
     */
    public static int numberOfCharactersInDocument(JCas jCas) {
        Collection<Token> wordsInDocument = getWordsInDocument(jCas);
        int characterCount = 0;

        Iterator<Token> iter = wordsInDocument.iterator();
        while (iter.hasNext()) {
            characterCount += iter.next().getCoveredText().length();
        }

        return characterCount;
    }

    /**
     * Calculates the number of syllables in a sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the number of syllables in a sentence.
     */
    public static int numberOfSyllablesInSentence(JCas jCas, Sentence sentence) {
        Collection<Token> wordsInSentence = getTokensFromSentence(jCas, sentence);
        List<Token> wordsFromTokens = getWordsFromTokens(wordsInSentence);
        RatSyllablesCounter syllablesCounter = new RatSyllablesCounter("de");
        int syllablesCount = 0;

        for (Token word : wordsFromTokens) {
            syllablesCount += syllablesCounter.countSyllables(word.getCoveredText());
        }

        return syllablesCount;
    }

    /**
     * Calculates the number of characters in a sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the number of characters in a sentence.
     */
    public static int numberOfCharactersInSentence(JCas jCas, Sentence sentence) {
        Collection<Token> wordsInSentence = getTokensFromSentence(jCas, sentence);
        List<Token> wordsFromTokens = getWordsFromTokens(wordsInSentence);
        int characterCount = 0;

        for (Token word : wordsFromTokens) {
            characterCount += word.getCoveredText().length();
        }

        return characterCount;
    }

    /**
     * Calculate the average number of words per sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the average number.
     */
    public static double averageNumberOfWordsPerSentence(JCas jCas) {
        return (double) numberOfWordsInDocument(jCas) / numberOfSentencesInDocument(jCas);
    }

    /**
     * Calculate the average number of syllables per sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the average number.
     */
    public static double averageNumberOfSyllablesPerSentence(JCas jCas) {
        return (double) averageNumberOfSyllablesPerWord(jCas) * averageNumberOfWordsPerSentence(jCas);
    }

    /**
     * Calculate the average number of characters per sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the average number.
     */
    public static double averageNumberOfCharactersPerSentence(JCas jCas) {
        return (double) numberOfCharactersInDocument(jCas) / numberOfSentencesInDocument(jCas);
    }

    /**
     * Calculate the average number of syllables per word.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the average number.
     */
    public static double averageNumberOfSyllablesPerWord(JCas jCas) {
        return (double) numberOfSyllablesInDocument(jCas) / numberOfWordsInDocument(jCas);
    }

    /**
     * Calculate the average number of characters per word.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the average number.
     */
    public static double averageNumberOfCharactersPerWord(JCas jCas) {
        return (double) numberOfCharactersInDocument(jCas) / numberOfWordsInDocument(jCas);
    }

    /**
     * Get the longest sentence in the document.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the longest sentence as {@code String}.
     */
    public static String getLongestSentence(JCas jCas) {
        Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
        Iterator<Sentence> iter = sentences.iterator();
        String longestSentence = null;
        int lengthLongestSentence = 0;

        while (iter.hasNext()) {
            Sentence value = iter.next();
            if (lengthLongestSentence < value.getCoveredText().length()) {
                longestSentence = value.getCoveredText();
                lengthLongestSentence = value.getCoveredText().length();
            }
        }

        return longestSentence;
    }

    /**
     * Get the longest word in the document based on syllables count.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the longest word as {@code String}.
     */
    public static String getLongestWordBySyllables(JCas jCas) {
        Collection<Token> wordsInDocument = getWordsInDocument(jCas);
        RatSyllablesCounter syllablesCounter = new RatSyllablesCounter("de");
        String longestWordBySyllables = null;
        int lengthLongestWord = 0;

        Iterator<Token> iter = wordsInDocument.iterator();
        while (iter.hasNext()) {
            Token value = iter.next();
            if (lengthLongestWord < syllablesCounter.countSyllables(value.getCoveredText())) {
                longestWordBySyllables = value.getCoveredText();
                lengthLongestWord = value.getCoveredText().length();
            }
        }

        return longestWordBySyllables;
    }

    /**
     * Get the longest word in the document based on character count.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the longest word as {@code String}.
     */
    public static String getLongestWordByCharacters(JCas jCas) {
        Collection<Token> wordsInDocument = getWordsInDocument(jCas);
        String longestWordByCharacters = null;
        int lengthLongestWord = 0;

        Iterator<Token> iter = wordsInDocument.iterator();
        while (iter.hasNext()) {
            Token value = iter.next();
            if (lengthLongestWord < value.getCoveredText().length()) {
                longestWordByCharacters = value.getCoveredText();
                lengthLongestWord = value.getCoveredText().length();
            }
        }

        return longestWordByCharacters;
    }

    /**
     * Get all words in a document.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @return the words as {@code Tokens}.
     */
    public static List<Token> getWordsInDocument(JCas jCas) {
        Collection<Token> wordsInDocument = JCasUtil.select(jCas, Token.class);
        ArrayList<Token> words = new ArrayList<Token>();

        Iterator<Token> iter = wordsInDocument.iterator();

        while (iter.hasNext()) {
            Token value = iter.next();
            if (!FILTER_TOKEN.contains(value.getCoveredText())) {
                words.add(value);
            }
        }

        return words;
    }

    /**
     * Get all words from a sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param sentence
     *            the sentence containing the words.
     * @return the words as {@code Tokens}.
     */
    public static List<Token> getWordsInSentence(JCas jCas, Sentence sentence) {
        Collection<Token> wordsInSentence = getTokensFromSentence(jCas, sentence);
        List<Token> wordsFromTokens = getWordsFromTokens(wordsInSentence);

        return wordsFromTokens;
    }

    /**
     * Get the percentage of occurrences of a given keyword.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param keyword
     *            the word to look for in the text.
     * @return the percentage.
     */
    public static float getPercentageOfKeyword(JCas jCas, String keyword) {
        List<Token> wordsInDocument = new ArrayList<Token>(getWordsInDocument(jCas));
        return (float) countWordOccurencesInTokenList(wordsInDocument, keyword) / wordsInDocument.size();
    }

    /**
     * Get the percentage of occurrences of a given posType.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param posType
     *            the posType to look for in the text.(the posType ADJ has
     *            several posValues (ADJA, ADJD, ADV)).
     * @return the percentage.
     */
    public static float getPercentageOfUsedWordsByPOSType(JCas jCas, String posType) {
        List<Token> wordsInDocument = new ArrayList<Token>(getWordsInDocument(jCas));

        return (float) getTokensByPosType(wordsInDocument, posType).size() / wordsInDocument.size();
    }

    /**
     * Get the x most used words by posType from the document.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param posType
     *            the posType to look for in the text.(the posType ADJ has
     *            several posValues (ADJA, ADJD, ADV)).
     * @param x
     *            the number of words to return.
     * @return the x most used words by postType.
     */
    public static Map<String, Float> getXMostUsedWordsByPOSValueType(JCas jCas, String posType, int x) {
        List<Token> wordsInDocument = new ArrayList<Token>(getWordsInDocument(jCas));
        List<Token> wordsWithPosTypeInDocument = getTokensByPosType(wordsInDocument, posType);
        Map<String, Float> wordsByPosValue = new HashMap<String, Float>();

        for (Token word : wordsWithPosTypeInDocument) {
            float percentageOfWordOccurences = (float) countWordOccurencesInTokenList(wordsWithPosTypeInDocument,
                    word.getCoveredText()) / wordsWithPosTypeInDocument.size();
            wordsByPosValue.put(word.getCoveredText().toLowerCase(), percentageOfWordOccurences);
        }

        Map<String, Float> sortedMap = wordsByPosValue.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(x)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return sortedMap;
    }

    /**
     * Get all tokens by a given posType from a list of {@code Tokens}.
     * 
     * @param words
     *            the list of words to search for the posType.
     * @param posType
     *            the posType to look for in the text.(the posType ADJ has
     *            several posValues (ADJA, ADJD, ADV)).
     * @return the matched words by posType as {@code Tokens}.
     */
    public static List<Token> getTokensByPosType(List<Token> words, String posType) {
        List<Token> result = new ArrayList<Token>();

        for (Token word : words) {
            if (word.getPos().getType().getShortName().equals(posType)
                    || word.getPos().getPosValue().startsWith(posType)) {
                result.add(word);
            }
        }

        return result;
    }

    private static int countWordOccurencesInTokenList(List<Token> tokens, String word) {
        int result = 0;

        for (Token token : tokens) {
            if (token.getCoveredText().equalsIgnoreCase(word.toLowerCase())) {
                result++;
            }
        }

        return result;
    }

    private static List<Token> getWordsFromTokens(Collection<Token> tokens) {
        ArrayList<Token> words = new ArrayList<Token>();
        Iterator<Token> iter = tokens.iterator();

        while (iter.hasNext()) {
            Token value = iter.next();
            if (!FILTER_TOKEN.contains(value.getCoveredText())) {
                words.add(value);
            }
        }

        return words;
    }

    private static String parseTime(long milliseconds, String format) {
        return String.format(format, TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private static Collection<Token> getTokensFromSentence(JCas jCas, Sentence sentence) {
        int begin = sentence.getBegin();
        int end = sentence.getEnd();
        return JCasUtil.selectCovered(jCas, Token.class, begin, end);
    }

    /**
     * Returns matches of a wordList in a given sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param sentence
     *            the sentence to search in.
     * @param wordList
     *            a word list containing values to match.
     * @return the matched values as {@code Tokens}.
     */
    public static List<Token> getMatchesInSentence(JCas jCas, Sentence sentence, List<String> wordList) {
        List<Token> result = new ArrayList<Token>();

        for (Token token : getTokensFromSentence(jCas, sentence)) {
            if (wordList.contains(token.getCoveredText())) {
                result.add(token);
            }
        }

        return result;
    }

    /**
     * Returns matches of a posType in a given sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param sentence
     *            the sentence to search in.
     * @param posType
     *            the posType to match.
     * @return the matched values as {@code Tokens}.
     */
    public static List<Token> getMatchesInSentence(JCas jCas, Sentence sentence, String posType) {
        List<Token> result = new ArrayList<Token>();

        for (Token token : getTokensFromSentence(jCas, sentence)) {
            if (token.getPos().getType().getShortName().equals(posType)) {
                result.add(token);
            }
        }

        return result;
    }

    /**
     * Returns matches of a posType in a given sentence.
     * 
     * @param jCas
     *            a jCas object containing the results of the analysis.
     * @param sentence
     *            the sentence to search in.
     * @param posValues
     *            the posValues to match. (a posType, e.g. ADJ, has several
     *            posValues, e.g. ADJA, ADJD, ADV.)
     * @return the matched values as {@code Tokens}.
     */
    public static List<Token> getMatchesInSentenceByPosValue(JCas jCas, Sentence sentence, List<String> posValues) {
        List<Token> result = new ArrayList<Token>();

        for (Token token : getTokensFromSentence(jCas, sentence)) {
            if (posValues.contains(token.getPos().getPosValue())) {
                result.add(token);
            }
        }

        return result;
    }
}