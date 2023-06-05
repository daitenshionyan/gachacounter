package com.hanyans.gachacounter.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class VersionTest {
    private final Version v1 = new Version(0, 0, 0);
    private final Version v2 = new Version(0, 0, 1);
    private final Version v3 = new Version(0, 1, 0);
    private final Version v4 = new Version(1, 0, 0);
    private final Version v5 = new Version(1, 1, 1);
    private final Version v6 = new Version(1, 2, 1);


    @Test
    public void parseTest_valid() throws Throwable {
        assertEquals(Version.parse("v1.2.3"), new Version(1, 2, 3));
        assertEquals(Version.parse("v1.2"), new Version(1, 2, 0));
        assertEquals(Version.parse("v1"), new Version(1, 0, 0));
        assertEquals(Version.parse("V1"), new Version(1, 0, 0));
        assertEquals(Version.parse("1"), new Version(1, 0, 0));
        assertEquals(Version.parse("1.2"), new Version(1, 2, 0));
        assertEquals(Version.parse("1.2.3"), new Version(1, 2, 3));
        assertEquals(Version.parse("v1.2.3ea"), new Version(1, 2, 3));
    }


    @Test
    public void parseTest_invalid() throws Throwable {
        assertThrows(NullPointerException.class, () -> Version.parse(null));
        assertThrows(IllegalArgumentException.class, () -> Version.parse(""));
        assertThrows(IllegalArgumentException.class, () -> Version.parse("v"));
        assertThrows(IllegalArgumentException.class, () -> Version.parse("v.."));
        assertThrows(IllegalArgumentException.class, () -> Version.parse("vabc"));
        assertThrows(IllegalArgumentException.class, () -> Version.parse("v.2.3"));
        assertThrows(IllegalArgumentException.class, () -> Version.parse("v..3"));
        assertThrows(IllegalArgumentException.class, () -> Version.parse("v-1.2.3"));
    }


    @Test
    public void isBeforeTest() throws Throwable {
        assertTrue(v1.isBefore(v2));
        assertTrue(v2.isBefore(v3));
        assertTrue(v3.isBefore(v4));
        assertTrue(v4.isBefore(v5));
        assertTrue(v5.isBefore(v6));

        // equals
        assertFalse(v6.isBefore(v6));

        // reverse
        assertFalse(v6.isBefore(v1));
    }


    @Test
    public void isAfterTest() throws Throwable {
        assertTrue(v6.isAfter(v5));
        assertTrue(v5.isAfter(v4));
        assertTrue(v4.isAfter(v3));
        assertTrue(v3.isAfter(v2));
        assertTrue(v2.isAfter(v1));

        // equals
        assertFalse(v1.isAfter(v1));

        // reverse
        assertFalse(v1.isAfter(v6));
    }
}
