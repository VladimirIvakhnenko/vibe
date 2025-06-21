package org.playlists.services;

import org.playlists.dto.GeneratePlaylistRequest;
import org.playlists.dto.GeneratePlaylistResponse;
import org.playlists.models.Playlist;
import org.playlists.models.Track;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlaylistGeneratorService {
    private final UserPreferenceService userPreferenceService;
    private final PlaylistRuleEngine ruleEngine;
    private final TrackGraphWalker trackGraphWalker;

    public PlaylistGeneratorService(UserPreferenceService userPreferenceService,
                                  PlaylistRuleEngine ruleEngine,
                                  TrackGraphWalker trackGraphWalker) {
        this.userPreferenceService = userPreferenceService;
        this.ruleEngine = ruleEngine;
        this.trackGraphWalker = trackGraphWalker;
    }

    @Cacheable(value = "userTopLikes", key = "#request.userId")
    public GeneratePlaylistResponse generatePlaylist(GeneratePlaylistRequest request) {
        try {
            List<Track> seedTracks = ruleEngine.processRules(
                    request.getUserId(), 
                    request.getRules()
            );

            if (seedTracks.isEmpty()) {
                return new GeneratePlaylistResponse(
                        false, 
                        "No seed tracks found based on rules", 
                        null
                );
            }

            List<Track> playlistTracks = trackGraphWalker.generatePlaylist(
                    seedTracks, 
                    20 // target playlist size
            );

            Playlist playlist = new Playlist(
                    UUID.randomUUID().toString(),
                    "Generated Playlist",
                    playlistTracks
            );

            return new GeneratePlaylistResponse(
                    true, 
                    "Playlist generated successfully", 
                    playlist
            );
        } catch (Exception e) {
            return new GeneratePlaylistResponse(
                    false, 
                    "Error generating playlist: " + e.getMessage(), 
                    null
            );
        }
    }
}