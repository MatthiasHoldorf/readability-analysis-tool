package de.qaware.rat.common;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class CollectionUtilsTest {

    @Test
    public void testFindDuplicates() {
        // Arrange
        Collection<String> twoDuplicates = new ArrayList<String>();
        twoDuplicates.add("a");
        twoDuplicates.add("a");
        twoDuplicates.add("b");
        twoDuplicates.add("b"); 

        Collection<String> oneDuplicate = new ArrayList<String>();
        oneDuplicate.add("a");
        oneDuplicate.add("a");
        oneDuplicate.add("c");
        
        Collection<String> zeroDuplicates = new ArrayList<String>();
        zeroDuplicates.add("a");
        zeroDuplicates.add("b");
        zeroDuplicates.add("c");

        // Act
        Collection<String> two = CollectionUtils.findDuplicates(twoDuplicates);
        Collection<String> one = CollectionUtils.findDuplicates(oneDuplicate);
        Collection<String> zero = CollectionUtils.findDuplicates(zeroDuplicates);

        // Assert
        assertEquals(2, two.size());
        assertEquals(1, one.size());
        assertEquals(0, zero.size());
    }

    @Test
    public void testAllEqual() {
        // Arrange
        List<String> stringListEqual = new ArrayList<String>();
        stringListEqual.add("a");
        stringListEqual.add("a");
        stringListEqual.add("a");

        List<String> stringListNotEqual = new ArrayList<String>();
        stringListNotEqual.add("a");
        stringListNotEqual.add("a");
        stringListNotEqual.add("c");

        // Act
        boolean equal = CollectionUtils.allEqual(stringListEqual);
        boolean notEqual = CollectionUtils.allEqual(stringListNotEqual);

        // Assert
        assertEquals(equal, true);
        assertEquals(notEqual, false);
    }
}