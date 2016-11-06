package de.qaware.rat.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * The {@code CollectionUtils} class provides utility functions for collections.
 * 
 * @author Matthias
 *
 */
public final class CollectionUtils {
    private CollectionUtils() {
    }

    /**
     * Find all duplicates in a collection.
     * 
     * @param list
     *            the list to find duplicates in.
     * @return the duplicates in the list.
     */
    public static <T> Set<T> findDuplicates(Collection<T> list) {
        Set<T> duplicates = new LinkedHashSet<T>();
        Set<T> uniques = new HashSet<T>();

        for (T t : list) {
            if (!uniques.add(t)) {
                duplicates.add(t);
            }
        }

        return duplicates;
    }

    /**
     * Determines if all entries in a list are equal.
     * 
     * @param list
     *            the list to check equal objects.
     * @return true, if all objects are equal; false otherwise.
     */
    public static <T> boolean allEqual(List<T> list) {
        boolean allEqual = true;

        for (int i = 0; i < list.size(); i++) {
            if (!list.get(0).equals(list.get(i))) {
                allEqual = false;
                break;
            }
        }

        return allEqual;
    }

    /**
     * Transforms a list of tokens to a list of Strings.
     * 
     * @param listToTransform
     *            the list to transform.
     * @return the transformed list.
     */
    public static List<String> transformToStringList(List<Token> listToTransform) {
        List<String> transformedList = new ArrayList<String>();

        for (Token token : listToTransform) {
            transformedList.add(token.getCoveredText());
        }

        return transformedList;
    }

    /**
     * Transform a list of Strings to a StringArray.
     * 
     * @param listToTransform
     *            the list to transform.
     * @return the transformed list.
     */
    public static List<String> transformToStringList(StringArray listToTransform) {
        List<String> transformedList = new ArrayList<String>();

        for (int i = 0; i < listToTransform.size(); i++) {
            transformedList.add(listToTransform.get(i));
        }

        return transformedList;
    }

    /**
     * Transform a list of Strings to a StringArray.
     * 
     * @param jCas
     *            the associated jCas object.
     * @param listToTransform
     *            the list to transform.
     * @return the transformed list.
     */
    public static StringArray transformToStringArray(JCas jCas, List<String> listToTransform) {
        StringArray stringArray = new StringArray(jCas, listToTransform.size());
        stringArray.copyFromArray((String[]) listToTransform.toArray(new String[listToTransform.size()]), 0, 0,
                listToTransform.size());

        return stringArray;
    }

    /**
     * Prints the elements of a string list without leading and ending brackets.
     * 
     * @param listToPrint
     *            the list to print.
     * @return a string as a list of elements without leading and ending
     *         brackets.
     */
    public static String printStringList(List<String> listToPrint) {
        return listToPrint.toString().replace("[", "").replace("]", "").trim();
    }
}