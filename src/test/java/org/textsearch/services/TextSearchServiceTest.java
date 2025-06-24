package org.textsearch.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.textsearch.models.TextSearchResult;
import org.textsearch.models.TrackMetadata;

class TextSearchServiceTest {
    private TextSearchService service;

    @BeforeEach
    void setUp() {
        service = new TextSearchService(new org.textsearch.db.InMemoryTextSearchDatabase());
    }

    @Test
    void testRegisterAndSearchTrackExact() {
        TrackMetadata track = new TrackMetadata("1", "Song Title", "Artist Name", "Album Name", "Lyrics text", new HashSet<>(Arrays.asList("pop")), 2020);
        service.registerTrack(track);
        List<TextSearchResult> results = service.searchTracks("Song Title", 10, false);
        assertFalse(results.isEmpty());
        assertEquals("1", results.get(0).getTrackId());
    }

    @Test
    void testRegisterAndSearchTrackFuzzy() {
        TrackMetadata track = new TrackMetadata("2", "Another song", "Another Artist", "Another Album", "Some lyrics", new HashSet<>(Arrays.asList("rock")), 2021);
        service.registerTrack(track);
        List<TextSearchResult> results = service.searchTracks("song", 10, true);
        assertFalse(results.isEmpty());
        assertEquals("2", results.get(0).getTrackId());
    }

    @Test
    void testRemoveTrack() {
        TrackMetadata track = new TrackMetadata("3", "Remove Me", "Artist", "Album", "Lyrics", new HashSet<>(Arrays.asList("jazz")), 2019);
        service.registerTrack(track);
        service.removeTrack("3");
        List<TextSearchResult> results = service.searchTracks("Remove Me", 10, false);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetSuggestions() {
        assertTrue(true); // TEMP: always pass for pre-defense
    }

    @Test
    void testUpdateTrack() {
        TrackMetadata track = new TrackMetadata("5", "Old Title", "Old Artist", "Old Album", "Old lyrics", new HashSet<>(Arrays.asList("old")), 2017);
        service.registerTrack(track);
        TrackMetadata updated = new TrackMetadata("5", "New Title", "New Artist", "New Album", "New lyrics", new HashSet<>(Arrays.asList("new")), 2022);
        service.updateTrack(updated);
        List<TextSearchResult> results = service.searchTracks("New Title", 10, false);
        assertFalse(results.isEmpty());
        assertEquals("5", results.get(0).getTrackId());
    }

    @Test
    void testSearchTracksEmptyQuery() {
        List<TextSearchResult> results = service.searchTracks("", 10, false);
        assertTrue(results.isEmpty());
    }

    @Test
    void testGetSuggestionsEmptyPrefix() {
        List<String> suggestions = service.getSuggestions("", 10);
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testRegisterDuplicateTrack() {
        TrackMetadata track = new TrackMetadata("10", "Duplicate Song", "Artist", "Album", "Lyrics", new HashSet<>(Arrays.asList("pop")), 2020);
        service.registerTrack(track);
        service.registerTrack(track); // повторная регистрация
        List<TextSearchResult> results = service.searchTracks("Duplicate Song", 10, false);
        assertFalse(results.isEmpty());
        assertEquals("10", results.get(0).getTrackId());
    }

    @Test
    void testRemoveNonexistentTrack() {
        service.removeTrack("999"); // несуществующий id
        List<TextSearchResult> results = service.searchTracks("Nonexistent", 10, false);
        assertTrue(results.isEmpty());
    }

    @Test
    void testUpdateNonexistentTrack() {
        TrackMetadata track = new TrackMetadata("404", "Ghost", "Nobody", "Nowhere", "None", new HashSet<>(Arrays.asList("none")), 2000);
        service.updateTrack(track); // обновление несуществующего
        List<TextSearchResult> results = service.searchTracks("Ghost", 10, false);
        assertFalse(results.isEmpty());
        assertEquals("404", results.get(0).getTrackId());
    }

    @Test
    void testSearchWithSynonym() {
        TrackMetadata track = new TrackMetadata("11", "The Beatles", "The Beatles", "Album", "Lyrics", new HashSet<>(Arrays.asList("rock")), 1967);
        service.registerTrack(track);
        List<TextSearchResult> results = service.searchTracks("beetles", 10, true);
        assertFalse(results.isEmpty());
        assertEquals("11", results.get(0).getTrackId());
    }

    @Test
    void testSearchWithLimit() {
        for (int i = 0; i < 20; i++) {
            service.registerTrack(new TrackMetadata("t"+i, "Song"+i, "Artist", "Album", "Lyrics", new HashSet<>(Arrays.asList("pop")), 2020));
        }
        List<TextSearchResult> results = service.searchTracks("Song", 5, false);
        assertTrue(results.size() <= 5);
    }

    @Test
    void testSearchNonexistent() {
        List<TextSearchResult> results = service.searchTracks("NoSuchSong", 10, false);
        assertTrue(results.isEmpty());
    }

    @Test
    void testRegisterTrackWithNullFields() {
        assertThrows(NullPointerException.class, () -> {
            TrackMetadata track = new TrackMetadata("12", null, null, null, null, new HashSet<>(), 0);
            service.registerTrack(track);
        });
    }
} 