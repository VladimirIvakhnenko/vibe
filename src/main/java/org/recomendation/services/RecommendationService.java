package org.recomendation.services;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.recomendation.db.TrackGraph;
import org.recomendation.db.UserPreferenceRepository;
import org.recomendation.models.Track;
import org.recomendation.models.UserPreference;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RecommendationService реализует алгоритм генерации музыкальных рекомендаций для пользователя.
 * Использует графовую модель треков и предпочтения пользователя (лайки/дизлайки).
 */
public class RecommendationService {
    /** Граф треков для поиска связей */
    private final TrackGraph trackGraph;
    /** Репозиторий предпочтений пользователей */
    private final UserPreferenceRepository userPreferenceRepository;

    /**
     * Конструктор RecommendationService с внедрением зависимостей.
     * @param trackGraph граф треков
     * @param userPreferenceRepository репозиторий предпочтений
     */
    public RecommendationService(TrackGraph trackGraph, UserPreferenceRepository userPreferenceRepository) {
        this.trackGraph = trackGraph;
        this.userPreferenceRepository = userPreferenceRepository;
    }

    /**
     * Сгенерировать список рекомендованных треков для пользователя.
     * Алгоритм:
     * 1. Берёт до 10 лайкнутых треков пользователя.
     * 2. Для каждого запускает BFS по графу (глубина 2).
     * 3. Суммирует веса связей для найденных треков.
     * 4. Исключает уже лайкнутые и дизлайкнутые треки.
     * 5. Сортирует по весу и возвращает top-N.
     *
     * @param userId идентификатор пользователя
     * @param limit максимальное количество рекомендаций
     * @return список рекомендованных треков
     */
    public List<Track> recommendTracks(String userId, int limit) {
        UserPreference user = userPreferenceRepository.findByUserId(userId)
                .orElse(new UserPreference(userId));

        Set<String> likedTrackIds = user.getLikedTrackIds();
        Set<String> dislikedTrackIds = user.getDislikedTrackIds();

        if (likedTrackIds.isEmpty()) {
            return Collections.emptyList();
        }

        Graph<Track, DefaultWeightedEdge> graph = trackGraph.getGraph();
        Map<Track, Double> recommendationScores = new HashMap<>();

        // 1. Берём топ-10 лайкнутых треков
        List<Track> topLikedTracks = likedTrackIds.stream()
                .map(trackGraph::getTrackById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .limit(10)
                .collect(Collectors.toList());

        for (Track startNode : topLikedTracks) {
            // 2. BFS по графу (глубина 2)
            BreadthFirstIterator<Track, DefaultWeightedEdge> bfs = new BreadthFirstIterator<>(graph, startNode);
            bfs.setCrossComponentTraversal(false);

            while (bfs.hasNext()) {
                Track currentTrack = bfs.next();
                if (startNode.equals(currentTrack)) continue;

                int depth = bfs.getDepth(currentTrack);
                if (depth > 2) break;

                // 4. Исключаем уже оценённые треки
                if (likedTrackIds.contains(currentTrack.getId()) || dislikedTrackIds.contains(currentTrack.getId())) {
                    continue;
                }

                // 3. Суммируем веса связей
                DefaultWeightedEdge edge = graph.getEdge(startNode, currentTrack);
                if (edge != null) {
                    double weight = graph.getEdgeWeight(edge);
                    recommendationScores.merge(currentTrack, weight, Double::sum);
                }
            }
        }

        // 5. Сортируем по весу и возвращаем top-N
        return recommendationScores.entrySet().stream()
                .sorted(Map.Entry.<Track, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
} 