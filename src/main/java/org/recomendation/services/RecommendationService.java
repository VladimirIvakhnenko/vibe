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
     * @param limit максимальное количество рекомендаций
     * @return список рекомендованных треков
     */
    public List<Track> recommendTracks(int limit) {
        UserPreference user = userPreferenceRepository.get();

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

        // 5. Сортируем по весу и берём top-20
        List<Track> topTracks = recommendationScores.entrySet().stream()
                .sorted(Map.Entry.<Track, Double>comparingByValue().reversed())
                .limit(20)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Случайно выбираем limit треков из top-20
        Collections.shuffle(topTracks);
        List<Track> result = topTracks.stream().limit(limit).collect(Collectors.toList());

        // Случайный сдвиг: с вероятностью 20% меняем местами соседей
        Random random = new Random();
        for (int i = 0; i < result.size() - 1; i++) {
            if (random.nextDouble() < 0.2) {
                // Меняем местами i и i+1
                Track tmp = result.get(i);
                result.set(i, result.get(i + 1));
                result.set(i + 1, tmp);
                i++; // чтобы не сдвигать только что перемешанных
            }
        }
        return result;
    }
} 