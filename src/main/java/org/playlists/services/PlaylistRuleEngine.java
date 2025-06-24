package org.playlists.services;

import org.playlists.dto.PlaylistRule;
import org.playlists.models.Track;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistRuleEngine {
    private final UserPreferenceService userPreferenceService;

    public PlaylistRuleEngine(UserPreferenceService userPreferenceService) {
        this.userPreferenceService = userPreferenceService;
    }

    public List<Track> processRules(String userId, List<PlaylistRule> rules) {
        if (rules == null || rules.isEmpty()) {
            return Collections.emptyList();
        }

        List<Track> resultTracks = new ArrayList<>();
        
        for (PlaylistRule rule : rules) {
            if ("likes".equalsIgnoreCase(rule.getSource())) {
                List<Track> likedTracks = userPreferenceService.getLikedTracks(userId);
                
                if (rule.getGenre() != null) {
                    likedTracks = likedTracks.stream()
                            .filter(t -> rule.getGenre().equalsIgnoreCase(t.getGenre()))
                            .collect(Collectors.toList());
                }
                
                resultTracks.addAll(likedTracks);
            } else if (rule.getGenre() != null) {
                List<Track> genreTracks = userPreferenceService.getTracksByGenre(rule.getGenre());
                resultTracks.addAll(genreTracks);
            }
        }
        
        return resultTracks;
    }
}