package org.playlists.services;

import org.playlists.models.Track;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPreferenceService {
    // In a real implementation, this would query a database or external service
    private final Map<String, List<Track>> userLikes = new HashMap<>();
    private final Map<String, List<Track>> tracksByGenre = new HashMap<>();

    public List<Track> getLikedTracks(String userId) {
        return userLikes.getOrDefault(userId, Collections.emptyList());
    }

    public List<Track> getTracksByGenre(String genre) {
        return tracksByGenre.getOrDefault(genre.toLowerCase(), Collections.emptyList());
    }

    // For testing/demo purposes
    public void addUserLike(String userId, Track track) {
        userLikes.computeIfAbsent(userId, k -> new ArrayList<>()).add(track);
        tracksByGenre.computeIfAbsent(track.getGenre().toLowerCase(), k -> new ArrayList<>()).add(track);
    }
}