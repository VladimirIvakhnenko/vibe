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
} 