package org.playlists.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.playlists.dto.PlaylistRule;
import org.playlists.models.Track;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistRuleEngineTest {
    private PlaylistRuleEngine engine;
    private UserPreferenceService preferenceService;

    @BeforeEach
    void setUp() {
        preferenceService = new UserPreferenceService();
        preferenceService.init();
        engine = new PlaylistRuleEngine(preferenceService);
    }

    @Test
    void processRules_withLikes_returnsTracks() {
        List<PlaylistRule> rules = List.of(
            new PlaylistRule("likes", null, 1.0)
        );
        List<Track> tracks = engine.processRules("test", rules);
        assertEquals(2, tracks.size());
    }

    @Test
    void processRules_withGenreFilter_returnsFiltered() {
        List<PlaylistRule> rules = List.of(
            new PlaylistRule("likes", "Rock", 1.0)
        );
        List<Track> tracks = engine.processRules("test", rules);
        assertEquals(1, tracks.size());
        assertEquals("Rock", tracks.get(0).getGenre());
    }
}