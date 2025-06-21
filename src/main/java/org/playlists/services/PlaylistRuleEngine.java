package org.playlists.services;

import org.playlists.dto.PlaylistRule;
import org.playlists.models.Track;
import org.springframework.stereotype.Service;

import java.util.*;
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
                    // Filter by genre if specified
                    likedTracks = likedTracks.stream()
                            .filter(t -> rule.getGenre().equalsIgnoreCase(t.getGenre()))
                            .collect(Collectors.toList());
                }
                
                resultTracks.addAll(selectRandomTracks(likedTracks, rule.getWeight()));
            } else if (rule.getGenre() != null) {
                List<Track> genreTracks = userPreferenceService.getTracksByGenre(rule.getGenre());
                resultTracks.addAll(selectRandomTracks(genreTracks, rule.getWeight()));
            }
        }
        
        return resultTracks;
    }

    private List<Track> selectRandomTracks(List<Track> tracks, double weight) {
        if (tracks.isEmpty()) {
            return Collections.emptyList();
        }
        
        int targetSize = (int) (tracks.size() * weight);
        if (targetSize == 0) {
            targetSize = 1;
        }
        
        Collections.shuffle(tracks);
        return tracks.subList(0, Math.min(targetSize, tracks.size()));
    }
}