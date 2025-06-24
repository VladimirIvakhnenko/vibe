package org.textsearch.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SynonymManagerTest {
    @Test
    void testNormalizeTextWithSynonyms() {
        SynonymManager sm = new SynonymManager();
        java.util.Set<String> beatles = new java.util.HashSet<>(java.util.Arrays.asList(sm.normalizeText("The Beatles")));
        for (String expected : new String[]{"beatles", "beattles", "beetles"}) {
            assertTrue(beatles.contains(expected), "Missing: " + expected);
        }
        java.util.Set<String> metallica = new java.util.HashSet<>(java.util.Arrays.asList(sm.normalizeText("metalica")));
        for (String expected : new String[]{"metallica", "metalica"}) {
            assertTrue(metallica.contains(expected), "Missing: " + expected);
        }
        java.util.Set<String> peppers = new java.util.HashSet<>(java.util.Arrays.asList(sm.normalizeText("rhcp")));
        for (String expected : new String[]{"red", "hot", "chili", "peppers", "rhcp", "chilli"}) {
            assertTrue(peppers.contains(expected), "Missing: " + expected);
        }
        java.util.Set<String> rap = new java.util.HashSet<>(java.util.Arrays.asList(sm.normalizeText("hip-hop")));
        for (String expected : new String[]{"hip", "hop", "hip-hop", "rap"}) {
            assertTrue(rap.contains(expected), "Missing: " + expected);
        }
        java.util.Set<String> rock = new java.util.HashSet<>(java.util.Arrays.asList(sm.normalizeText("rock music")));
        for (String expected : new String[]{"rock", "music"}) {
            assertTrue(rock.contains(expected), "Missing: " + expected);
        }
        java.util.Set<String> beyonce = new java.util.HashSet<>(java.util.Arrays.asList(sm.normalizeText("beyoncé")));
        for (String expected : new String[]{"beyonce", "beyoncé"}) {
            assertTrue(beyonce.contains(expected), "Missing: " + expected);
        }
        java.util.Set<String> acdc = new java.util.HashSet<>(java.util.Arrays.asList(sm.normalizeText("a c d c")));
        for (String expected : new String[]{"acdc", "ac/dc", "a", "c", "d", "c"}) {
            assertTrue(acdc.contains(expected), "Missing: " + expected);
        }
    }

    @Test
    void testNormalizeTextNoSynonym() {
        SynonymManager sm = new SynonymManager();
        String[] result = sm.normalizeText("unknownband");
        assertEquals(1, result.length);
        assertEquals("unknownband", result[0]);
    }

    @Test
    void testNormalizeTextCaseInsensitive() {
        SynonymManager sm = new SynonymManager();
        String[] result = sm.normalizeText("THE BEATLES");
        boolean found = false;
        for (String s : result) if (s.equals("beatles")) found = true;
        assertTrue(found);
    }

    @Test
    void testNormalizeTextWithSpaces() {
        SynonymManager sm = new SynonymManager();
        String[] result = sm.normalizeText("red hot chili peppers");
        boolean found = false;
        for (String s : result) if (s.equals("rhcp")) found = true;
        assertTrue(found);
    }

    @Test
    void testNormalizeTextWithHyphen() {
        SynonymManager sm = new SynonymManager();
        String[] result = sm.normalizeText("hip hop");
        boolean found = false;
        for (String s : result) if (s.equals("hip-hop")) found = true;
        assertTrue(found);
    }

    @Test
    void testNormalizeTextEmptyString() {
        SynonymManager sm = new SynonymManager();
        String[] result = sm.normalizeText("");
        assertEquals(0, result.length);
    }

    @Test
    void testNormalizeTextUnknownWord() {
        SynonymManager sm = new SynonymManager();
        String[] result = sm.normalizeText("abracadabra");
        assertTrue(result.length == 1 && result[0].equals("abracadabra"));
    }

//    @Test
//    void testSynonyms() {
//        SynonymManager sm = new SynonymManager();
//        assertEquals("beatles", sm.getCanonicalForm("the beatles"));
//        assertEquals("rock", sm.getCanonicalForm("rock music"));
//    }
} 