package org.playlists.services;

import org.playlists.models.Track;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserPreferenceService {
    private final Map<String, List<Track>> userLikes = new HashMap<>();
    private final Map<String, List<Track>> tracksByGenre = new HashMap<>();

    @PostConstruct
    public void init() {
        // Инициализация тестовых данных
        Track track1 = new Track("1", "Bohemian Rhapsody", "Queen", "Rock", 1975);
        Track track2 = new Track("2", "Yesterday", "The Beatles", "Pop", 1965);
        
        // Добавляем треки для пользователя test
        userLikes.put("test", Arrays.asList(track1, track2));
        
        // Добавляем треки по жанрам
        tracksByGenre.put("rock", Collections.singletonList(track1));
        tracksByGenre.put("pop", Collections.singletonList(track2));
    }

    public List<Track> getLikedTracks(String userId) {
        return new ArrayList<>(userLikes.getOrDefault(userId, Collections.emptyList()));
    }

    public List<Track> getTracksByGenre(String genre) {
        if (genre == null) return Collections.emptyList();
        return new ArrayList<>(tracksByGenre.getOrDefault(genre.toLowerCase(), Collections.emptyList()));
    }
}