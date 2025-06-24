package org.textsearch.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.textsearch.db.TextSearchDatabase;
import org.textsearch.models.TextSearchResult;
import org.textsearch.models.TrackMetadata;

class TextSearchServiceMockTest {
    private TextSearchDatabase db;
    private TextSearchService service;

    @BeforeEach
    void setUp() {
        db = mock(TextSearchDatabase.class);
        service = new TextSearchService(db);
    }

    @Test
    void testSearchTracksDelegatesToDb() {
        List<TextSearchResult> expected = Arrays.asList(
                mock(TextSearchResult.class),
                mock(TextSearchResult.class)
        );
        when(db.searchTracks("query", 5, true)).thenReturn(expected);
        List<TextSearchResult> actual = service.searchTracks("query", 5, true);
        assertEquals(expected, actual);
        verify(db, times(1)).searchTracks("query", 5, true);
    }

    @Test
    void testRegisterTrackDelegatesToDb() {
        TrackMetadata meta = mock(TrackMetadata.class);
        service.registerTrack(meta);
        verify(db, times(1)).addTrack(meta);
    }

    @Test
    void testRemoveTrackDelegatesToDb() {
        service.removeTrack("track1");
        verify(db, times(1)).removeTrack("track1");
    }

    @Test
    void testGetSuggestionsDelegatesToDb() {
        when(db.getSuggestions("pre", 3)).thenReturn(Collections.singletonList("prefix"));
        List<String> suggestions = service.getSuggestions("pre", 3);
        assertEquals(Collections.singletonList("prefix"), suggestions);
        verify(db, times(1)).getSuggestions("pre", 3);
    }

    @Test
    void testUpdateTrackDelegatesToDb() {
        TrackMetadata meta = mock(TrackMetadata.class);
        service.updateTrack(meta);
        verify(db, times(1)).updateTrack(meta);
    }

    @Test
    void testGetTrackDelegatesToDb() {
        TrackMetadata meta = mock(TrackMetadata.class);
        when(db.getTrack("track2")).thenReturn(meta);
        TrackMetadata result = service.getTrack("track2");
        assertEquals(meta, result);
        verify(db, times(1)).getTrack("track2");
    }

    @Test
    void testSearchTracksReturnsEmptyList() {
        when(db.searchTracks("empty", 5, false)).thenReturn(Collections.emptyList());
        List<TextSearchResult> results = service.searchTracks("empty", 5, false);
        assertEquals(0, results.size());
    }

    @Test
    void testRegisterNullTrack() {
        assertThrows(NullPointerException.class, () -> {
            service.registerTrack(null);
        });
    }

    @Test
    void testRemoveNullTrack() {
        service.removeTrack(null);
        verify(db, times(1)).removeTrack(null);
    }

    @Test
    void testGetSuggestionsEmpty() {
        when(db.getSuggestions("", 2)).thenReturn(Collections.emptyList());
        List<String> suggestions = service.getSuggestions("", 2);
        assertEquals(0, suggestions.size());
        verify(db, times(1)).getSuggestions("", 2);
    }

    @Test
    void testUpdateNullTrack() {
        service.updateTrack(null);
        verify(db, times(1)).updateTrack(null);
    }

    @Test
    void testGetTrackNull() {
        when(db.getTrack(null)).thenReturn(null);
        TrackMetadata result = service.getTrack(null);
        assertEquals(null, result);
        verify(db, times(1)).getTrack(null);
    }

    @Test
    void testMultipleDelegations() {
        TrackMetadata meta = mock(TrackMetadata.class);
        when(db.getTrack("multi")).thenReturn(meta);
        service.registerTrack(meta);
        service.updateTrack(meta);
        service.removeTrack("multi");
        service.getTrack("multi");
        verify(db, times(1)).addTrack(meta);
        verify(db, times(1)).updateTrack(meta);
        verify(db, times(1)).removeTrack("multi");
        verify(db, times(1)).getTrack("multi");
    }
} 