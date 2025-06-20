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
    private final Map<String, Track> trackMap = new HashMap<>();

    /**
     * Получить сам граф (для алгоритмов поиска и рекомендаций)
     */
    public SimpleWeightedGraph<Track, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    /**
     * Добавляет трек в граф и в карту для быстрого доступа.
     * @param track трек для добавления
     */
    public void addVertex(Track track) {
        graph.addVertex(track);
        trackMap.put(track.getId(), track);
    }

    /**
     * Найти трек по его id.
     * @param id ID трека
     * @return Track или null, если не найден
     */
    public Track getTrackById(String id) {
        return trackMap.get(id);
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

    public double getEdgeWeight(DefaultWeightedEdge edge) {
        return graph.getEdgeWeight(edge);
    }

    public void setEdgeWeight(DefaultWeightedEdge edge, double weight) {
        graph.setEdgeWeight(edge, weight);
    }

    public void addEdge(Track source, Track target) {
        graph.addEdge(source, target);
    }

    public DefaultWeightedEdge getEdge(Track source, Track target) {
        return graph.getEdge(source, target);
    }
} 