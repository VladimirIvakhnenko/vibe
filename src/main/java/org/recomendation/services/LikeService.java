package org.recomendation.services;

import org.recomendation.db.TrackGraph;
import org.recomendation.db.UserPreferenceRepository;
import org.recomendation.models.Track;
import org.recomendation.models.UserPreference;
import org.springframework.stereotype.Service;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * LikeService отвечает за обработку лайков и дизлайков пользователя.
 * При лайке/дизлайке обновляет веса связей между треками в графе.
 * Также хранит и обновляет предпочтения пользователя (лайки/дизлайки).
 */
@Service
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
        // Проверяем, что трек существует
        if (trackGraph.getTrackById(trackId) == null) {
            throw new IllegalArgumentException("Track not found with id: " + trackId);
        }
        userPreferenceRepository.get().getLikedTrackIds().add(trackId);
        userPreferenceRepository.get().getDislikedTrackIds().remove(trackId);
        updateWeight(trackId, 1.0);
    }

    /**
     * Обработать дизлайк трека.
     * Уменьшает вес связей между этим треком и всеми лайкнутыми треками.
     * @param trackId идентификатор трека
     */
    public void dislikeTrack(String trackId) {
        // Проверяем, что трек существует
        if (trackGraph.getTrackById(trackId) == null) {
            throw new IllegalArgumentException("Track not found with id: " + trackId);
        }
        userPreferenceRepository.get().getDislikedTrackIds().add(trackId);
        userPreferenceRepository.get().getLikedTrackIds().remove(trackId);
        updateWeight(trackId, -1.0);
    }

    /**
     * Зарегистрировать полное прослушивание трека.
     * Увеличивает вес связей между этим треком и всеми лайкнутыми треками на 0.5.
     * @param trackId идентификатор трека
     */
    public void listenTrack(String trackId) {
        // Проверяем, что трек существует
        if (trackGraph.getTrackById(trackId) == null) {
            throw new IllegalArgumentException("Track not found with id: " + trackId);
        }
        updateWeight(trackId, 0.5);
    }

    /**
     * Вспомогательный метод для обновления веса рёбер для данного трека.
     * Находит всех соседей трека в графе и изменяет вес ребра, связывающего их.
     * @param trackId ID трека, для которого обновляются веса
     * @param weightChange значение, на которое нужно изменить вес (положительное или отрицательное)
     */
    private void updateWeight(String trackId, double weightChange) {
        Track track = trackGraph.getTrackById(trackId);
        if (track == null) {
            return;
        }

        // Оптимизированный способ: получаем список только соседних вершин
        for (Track neighbor : Graphs.neighborListOf(trackGraph.getGraph(), track)) {
            DefaultWeightedEdge edge = trackGraph.getGraph().getEdge(track, neighbor);
            if (edge != null) {
                double currentWeight = trackGraph.getGraph().getEdgeWeight(edge);
                trackGraph.getGraph().setEdgeWeight(edge, currentWeight + weightChange);
            }
        }
    }
} 