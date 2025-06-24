package org.textsearch.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class LevenshteinAutomatonTest {
    @Test
    void testAcceptExact() {
        LevenshteinAutomaton automaton = new LevenshteinAutomaton("hello", 1);
        assertTrue(automaton.accept("hello"));
    }

    @Test
    void testAcceptWithTypos() {
        LevenshteinAutomaton automaton = new LevenshteinAutomaton("hello", 1);
        assertTrue(automaton.accept("hell"));
        assertTrue(automaton.accept("hallo"));
        assertTrue(automaton.accept("hella"));
    }

    @Test
    void testRejectTooManyEdits() {
        LevenshteinAutomaton automaton = new LevenshteinAutomaton("hello", 1);
        assertFalse(automaton.accept("hxllo1"));
        assertFalse(automaton.accept("he"));
    }

    @Test
    void testAcceptEmptyStrings() {
        LevenshteinAutomaton automaton = new LevenshteinAutomaton("", 1);
        assertTrue(automaton.accept(""));
        assertTrue(automaton.accept("a"));
    }

    @Test
    void testRejectCompletelyDifferent() {
        LevenshteinAutomaton automaton = new LevenshteinAutomaton("abc", 1);
        assertFalse(automaton.accept("xyz"));
    }

    @Test
    void testAcceptMaxDistance() {
        LevenshteinAutomaton automaton = new LevenshteinAutomaton("abc", 3);
        assertTrue(automaton.accept("xyz"));
    }

    @Test
    void testCaseSensitivity() {
        LevenshteinAutomaton automaton = new LevenshteinAutomaton("Hello", 1);
        assertTrue(automaton.accept("hello"));
    }

    @Test
    void testLongStrings() {
        String s1 = "a".repeat(100);
        String s2 = "a".repeat(99) + "b";
        LevenshteinAutomaton automaton = new LevenshteinAutomaton(s1, 1);
        assertTrue(automaton.accept(s2));
    }

    @Test
    void testSingleCharWords() {
        LevenshteinAutomaton automaton = new LevenshteinAutomaton("a", 1);
        assertTrue(automaton.accept("b"));
        assertTrue(automaton.accept("a"));
        assertTrue(automaton.accept(""));
    }
} 