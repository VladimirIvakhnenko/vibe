package org.recomendation.services;

import org.recomendation.db.TrackGraph;
import org.recomendation.db.UserPreferenceRepository;
import org.recomendation.models.Track;
import org.recomendation.models.UserPreference;

/**
 * LikeService отвечает за обработку лайков и дизлайков пользователей.
 * При лайке/дизлайке обновляет веса связей между треками в графе.
 * Также хранит и обновляет предпочтения пользователя (лайки/дизлайки).
 */
public class LikeService {
    /** Граф треков, где обновляются веса рёбер */
    private final TrackGraph trackGraph;
    /** Репозиторий для хранения предпочтений пользователей */
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
     * Обработать лайк трека пользователем.
     * Увеличивает вес связей между этим треком и всеми другими лайкнутыми треками пользователя.
     * @param userId идентификатор пользователя
     * @param trackId идентификатор трека
     */
    public void likeTrack(String userId, String trackId) {
        UserPreference user = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> userPreferenceRepository.save(new UserPreference(userId)));
        Track likedTrack = trackGraph.getTrackById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("Track not found"));

        // Для каждого другого лайкнутого трека увеличиваем вес связи
        user.getLikedTrackIds().stream()
                .filter(otherTrackId -> !otherTrackId.equals(trackId))
                .forEach(otherTrackId -> {
                    trackGraph.getTrackById(otherTrackId).ifPresent(otherTrack -> {
                        trackGraph.updateEdgeWeight(likedTrack, otherTrack, 1.0);
                    });
                });

        user.getLikedTrackIds().add(trackId);
        userPreferenceRepository.save(user);
    }

    /**
     * Обработать дизлайк трека пользователем.
     * Уменьшает вес связей между этим треком и всеми лайкнутыми треками пользователя.
     * @param userId идентификатор пользователя
     * @param trackId идентификатор трека
     */
    public void dislikeTrack(String userId, String trackId) {
        UserPreference user = userPreferenceRepository.findByUserId(userId)
                .orElseGet(() -> userPreferenceRepository.save(new UserPreference(userId)));
        Track dislikedTrack = trackGraph.getTrackById(trackId)
                .orElseThrow(() -> new IllegalArgumentException("Track not found"));
        
        // Для каждого лайкнутого трека уменьшаем вес связи
        user.getLikedTrackIds().stream()
                .filter(otherTrackId -> !otherTrackId.equals(trackId))
                .forEach(otherTrackId -> {
                    trackGraph.getTrackById(otherTrackId).ifPresent(otherTrack -> {
                        trackGraph.updateEdgeWeight(dislikedTrack, otherTrack, -1.0);
                    });
                });

        user.getDislikedTrackIds().add(trackId);
        user.getLikedTrackIds().remove(trackId); // Если трек был лайкнут, убираем лайк
        userPreferenceRepository.save(user);
    }
} 