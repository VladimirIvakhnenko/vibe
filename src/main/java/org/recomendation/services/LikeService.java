package org.recomendation.services;

import org.recomendation.db.TrackGraph;
import org.recomendation.db.UserPreferenceRepository;
import org.recomendation.models.Track;
import org.recomendation.models.UserPreference;

/**
 * LikeService отвечает за обработку лайков и дизлайков пользователя.
 * При лайке/дизлайке обновляет веса связей между треками в графе.
 * Также хранит и обновляет предпочтения пользователя (лайки/дизлайки).
 */
public class LikeService {
    /** Граф треков, где обновляются веса рёбер */
    private final TrackGraph trackGraph;
    /** Репозиторий для хранения предпочтений пользователя */
    private final UserPreferenceRepository userPreferenceRepository;

    /**
     * Конструктор LikeService с внедрением зависимостей.
     * @param trackGraph граф треков
     * @param userPreferenceRepository репозиторий предпочтений
     */
    public LikeService(TrackGraph trackGraph, UserPreferenceRepository userPreferenceRepository) {
        this.trackGraph = trackGraph;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    /**
     * Обработать лайк трека.
     * Увеличивает вес связей между этим треком и всеми другими лайкнутыми треками.
     * @param trackId идентификатор трека
     */
    public void likeTrack(String trackId) {
        UserPreference user = userPreferenceRepository.get();
        Track likedTrack = trackGraph.getTrackById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("Track not found"));

        user.getLikedTrackIds().stream()
                .filter(otherTrackId -> !otherTrackId.equals(trackId))
                .forEach(otherTrackId -> {
                    trackGraph.getTrackById(otherTrackId).ifPresent(otherTrack -> {
                        trackGraph.updateEdgeWeight(likedTrack, otherTrack, 1.0);
                    });
                });

        user.getLikedTrackIds().add(trackId);
    }

    /**
     * Обработать дизлайк трека.
     * Уменьшает вес связей между этим треком и всеми лайкнутыми треками.
     * @param trackId идентификатор трека
     */
    public void dislikeTrack(String trackId) {
        UserPreference user = userPreferenceRepository.get();
        Track dislikedTrack = trackGraph.getTrackById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("Track not found"));
        
        user.getLikedTrackIds().stream()
                .filter(otherTrackId -> !otherTrackId.equals(trackId))
                .forEach(otherTrackId -> {
                    trackGraph.getTrackById(otherTrackId).ifPresent(otherTrack -> {
                        trackGraph.updateEdgeWeight(dislikedTrack, otherTrack, -1.0);
                    });
                });

        user.getDislikedTrackIds().add(trackId);
        user.getLikedTrackIds().remove(trackId);
    }

    /**
     * Зарегистрировать полное прослушивание трека.
     * Увеличивает вес связей между этим треком и всеми лайкнутыми треками на 0.5.
     * @param trackId идентификатор трека
     */
    public void listenTrack(String trackId) {
        UserPreference user = userPreferenceRepository.get();
        Track listenedTrack = trackGraph.getTrackById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("Track not found"));

        user.getLikedTrackIds().stream()
                .filter(otherTrackId -> !otherTrackId.equals(trackId))
                .forEach(otherTrackId -> {
                    trackGraph.getTrackById(otherTrackId).ifPresent(otherTrack -> {
                        trackGraph.updateEdgeWeight(listenedTrack, otherTrack, 0.5);
                    });
                });
    }
} 