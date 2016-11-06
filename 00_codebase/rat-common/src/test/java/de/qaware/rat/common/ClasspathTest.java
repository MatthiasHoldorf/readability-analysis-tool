package de.qaware.rat.common;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ClasspathTest {

    @Test
    public void testLoadAsByte() {
        // Act
        byte[] data = ClassPathUtils.loadAsByte("small-document.docx");

        // Assert
        assertNotNull(data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadAsByteIllegalArgumentException() {
        // Act
        ClassPathUtils.loadAsByte("invalid-location-parameter");
    }
}