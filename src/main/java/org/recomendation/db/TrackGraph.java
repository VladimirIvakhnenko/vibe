package org.recomendation.db;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.recomendation.models.Track;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * TrackGraph реализует in-memory графовую модель треков с помощью JGraphT.
 * Вершины графа — треки, рёбра — связи между ними с весами (strength).
 * Вес ребра отражает степень схожести или совместную популярность треков у пользователей.
 */
public class TrackGraph {
    /** Граф, где вершины — треки, а рёбра — связи с весами */
    private final SimpleWeightedGraph<Track, DefaultWeightedEdge> graph =
            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    /** Индекс для быстрого поиска трека по id */
    private final Map<String, Track> trackByIdIndex = new HashMap<>();

    /**
     * Получить сам граф (для алгоритмов поиска и рекомендаций)
     */
    public SimpleWeightedGraph<Track, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    /**
     * Добавить трек в граф и индекс.
     * @param track трек для добавления
     */
    public void addTrack(Track track) {
        graph.addVertex(track);
        trackByIdIndex.put(track.getId(), track);
    }

    /**
     * Найти трек по его id.
     * @param id идентификатор трека
     * @return Optional с треком, если найден
     */
    public Optional<Track> getTrackById(String id) {
        return Optional.ofNullable(trackByIdIndex.get(id));
    }

    /**
     * Создать или обновить связь между двумя треками с заданным весом.
     * @param t1 первый трек
     * @param t2 второй трек
     * @param weight вес связи
     */
    public void connectTracks(Track t1, Track t2, double weight) {
        graph.addVertex(t1);
        graph.addVertex(t2);
        var edge = graph.addEdge(t1, t2);
        if (edge != null) {
            graph.setEdgeWeight(edge, weight);
        }
    }

    /**
     * Изменить вес существующего ребра между двумя треками.
     * @param t1 первый трек
     * @param t2 второй трек
     * @param delta на сколько изменить вес (может быть отрицательным)
     */
    public void updateEdgeWeight(Track t1, Track t2, double delta) {
        var edge = graph.getEdge(t1, t2);
        if (edge != null) {
            double newWeight = graph.getEdgeWeight(edge) + delta;
            graph.setEdgeWeight(edge, newWeight);
        }
    }

    /**
     * Получить соседей трека (связанные треки).
     * @param track исходный трек
     * @return множество соседних треков
     */
    public Set<Track> getNeighbors(Track track) {
        return graph.containsVertex(track) ? graph.edgesOf(track).stream()
                .map(e -> graph.getEdgeSource(e).equals(track) ? graph.getEdgeTarget(e) : graph.getEdgeSource(e))
                .collect(java.util.stream.Collectors.toSet()) : java.util.Collections.emptySet();
    }
} 