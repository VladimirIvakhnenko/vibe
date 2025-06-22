package org.playlists.dto;

import org.junit.jupiter.api.Test;
import org.playlists.models.Playlist;
import java.util.List; 

import static org.junit.jupiter.api.Assertions.*;

class GeneratePlaylistResponseTest {
    @Test
    void constructor_setsFieldsCorrectly() {
        Playlist playlist = new Playlist("1", "Test", List.of());
        GeneratePlaylistResponse response = new GeneratePlaylistResponse(true, "OK", playlist);
        
        assertTrue(response.isSuccess());
        assertEquals("OK", response.getMessage());
        assertNotNull(response.getPlaylist());
    }
}