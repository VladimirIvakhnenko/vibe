package org.playlists.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TrackTest {
    @Test
    void constructor_setsFieldsCorrectly() {
        Track track = new Track("1", "Song", "Artist", "Rock", 2020);
        
        assertEquals("1", track.getId());
        assertEquals("Song", track.getTitle());
        assertEquals("Rock", track.getGenre());
    }
}