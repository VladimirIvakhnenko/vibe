package org.playlists.services;

import org.playlists.models.Track;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class UserPreferenceService {
    private final Map<String, List<Track>> userLikes = new HashMap<>();
    private final Map<String, List<Track>> tracksByGenre = new HashMap<>();

    @PostConstruct
    public void init() {
        // Инициализация тестовых данных при старте приложения
        Track track1 = new Track("1", "Bohemian Rhapsody", "Queen", "Rock", 1975);
        Track track2 = new Track("2", "Yesterday", "The Beatles", "Pop", 1965);
        
        addUserLike("test", track1);
        addUserLike("test", track2);
    }

    public List<Track> getLikedTracks(String userId) {
        return userLikes.getOrDefault(userId, Collections.emptyList());
    }

    public List<Track> getTracksByGenre(String genre) {
        if (genre == null) return Collections.emptyList();
        return tracksByGenre.getOrDefault(genre.toLowerCase(), Collections.emptyList());
    }

    public void addUserLike(String userId, Track track) {
        userLikes.computeIfAbsent(userId, k -> new ArrayList<>()).add(track);
        tracksByGenre.computeIfAbsent(track.getGenre().toLowerCase(), k -> new ArrayList<>()).add(track);
    }
}