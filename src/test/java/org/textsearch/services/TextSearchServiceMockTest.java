package org.textsearch.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
} 