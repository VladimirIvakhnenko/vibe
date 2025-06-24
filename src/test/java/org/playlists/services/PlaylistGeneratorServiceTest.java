package org.playlists.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.playlists.dto.GeneratePlaylistRequest;
import org.playlists.dto.GeneratePlaylistResponse;
import org.playlists.dto.PlaylistRule;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistGeneratorServiceTest {
    private PlaylistGeneratorService service;

    @BeforeEach
    void setUp() {
        UserPreferenceService userPreferenceService = new UserPreferenceService();
        userPreferenceService.init();
        
        service = new PlaylistGeneratorService(
            userPreferenceService,
            new PlaylistRuleEngine(userPreferenceService),
            new TrackGraphWalker()
        );
    }

    @Test
    void generatePlaylist_returnsValidResponse() {
        GeneratePlaylistRequest request = new GeneratePlaylistRequest();
        request.setUserId("test");
        request.setRules(List.of(
            new PlaylistRule("likes", null, 1.0)
        ));

        GeneratePlaylistResponse response = service.generatePlaylist(request);
        assertTrue(response.isSuccess());
        assertNotNull(response.getPlaylist());
        assertEquals(2, response.getPlaylist().getTracks().size());
    }
}