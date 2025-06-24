package org.playlists.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.playlists.models.Track;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserPreferenceServiceTest {
    private UserPreferenceService service;

    @BeforeEach
    void setUp() {
        service = new UserPreferenceService();
        service.init(); // Явно вызываем инициализацию
    }

    @Test
    void getLikedTracks_returnsTracksForExistingUser() {
        List<Track> tracks = service.getLikedTracks("test");
        assertEquals(2, tracks.size());
    }

    @Test
    void getTracksByGenre_returnsFilteredTracks() {
        List<Track> rockTracks = service.getTracksByGenre("Rock");
        assertEquals(1, rockTracks.size());
        assertEquals("Rock", rockTracks.get(0).getGenre());
    }
}