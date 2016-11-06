package de.qaware.rat.statistics;

import org.apache.uima.jcas.JCas;

/**
 * The {@code ReadabilityFormula} class provides functions to calcuate
 * readability formulas.
 * 
 * @author Matthias
 *
 */
public final class ReadabilityFormula {
    private ReadabilityFormula() {
    }

    /**
     * Calculate the flesch reading ease formula.
     * 
     * @param jCas
     *            the jCas containing the analysed text.
     * @return the score of the flesch reading ease formula.
     */
    public static double calculateFleschReadingEase(JCas jCas) {
        return 206.835 - (1.015 * TextStatistic.averageNumberOfWordsPerSentence(jCas))
                - (84.6 * TextStatistic.averageNumberOfSyllablesPerWord(jCas));
    }

    /**
     * Calculate the flesch reading ease formula by amstad.
     * 
     * <p>
     * This formula is the german adaption of the flesch reading ease (which was
     * developed for the english language).
     * 
     * @param jCas
     *            the jCas containing the analysed text.
     * @return the score of the flesch reading ease formula by amstad.
     */
    public static double calculateFleschReadingEaseAmstad(JCas jCas) {
        double asl = TextStatistic.averageNumberOfWordsPerSentence(jCas);
        double asw = TextStatistic.averageNumberOfSyllablesPerWord(jCas);

        return 180 - asl - (58.5 * asw);
    }

    /**
     * Calculate the 1. wiener sachtext formula.
     * 
     * @param jCas
     *            the jCas containing the analysed text.
     * @return the score of the 1. wiener sachtext formula.
     */
    public static double calculateWienerSachtextformel(JCas jCas) {
        float ms = TextStatistic.percentageOfWordsWithXOrMoreSyllables(jCas, 3) * 100;
        double sl = TextStatistic.averageNumberOfWordsPerSentence(jCas);
        float iw = TextStatistic.percentageOfWordsWithXOrMoreCharacters(jCas, 6) * 100;
        float es = TextStatistic.percentageOfWordsWithXSyllables(jCas, 1) * 100;

        return 0.1935 * ms + 0.1672 * sl + 0.1297 * iw - 0.0327 * es - 0.875;
    }
}