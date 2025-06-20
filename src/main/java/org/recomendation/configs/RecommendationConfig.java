package org.recomendation.configs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.recomendation.db.TrackGraph;
import org.recomendation.db.UserPreferenceRepository;
import org.recomendation.models.Track;
import org.recomendation.services.LikeService;
import org.recomendation.services.RecommendationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring-конфигурация, отвечающая за создание и настройку всех компонентов модуля рекомендаций.
 */
@Configuration
public class RecommendationConfig {

    /**
     * Создаёт и настраивает бин TrackGraph.
     * 1. Загружает треки из JSON-файла `spotify_tracks.json`.
     * 2. Добавляет все треки в граф.
     * 3. Строит рёбра (связи) между треками на основе общих исполнителей и жанров.
     * Это создаёт начальную структуру для системы рекомендаций.
     *
     * @return полностью настроенный TrackGraph.
     * @throws IOException если файл с треками не найден или не может быть прочитан.
     */
    @Bean
    public TrackGraph trackGraph() throws IOException {
        TrackGraph trackGraph = new TrackGraph();

        // 1. Загрузка треков из JSON
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = new ClassPathResource("spotify_1000_tracks_20250618_153243.json").getInputStream();
        List<Track> allTracks = objectMapper.readValue(inputStream, new TypeReference<List<Track>>() {});

        // 2. Добавление треков в граф
        for (Track track : allTracks) {
            trackGraph.addVertex(track);
        }

        // 3. Построение связей по исполнителям и жанрам
        buildEdges(trackGraph, allTracks);

        return trackGraph;
    }

    private void buildEdges(TrackGraph trackGraph, List<Track> allTracks) {
        // Оптимизированное создание рёбер O(N*M) вместо O(N^2), где N - кол-во треков, M - кол-во артистов/жанров
        Map<String, List<Track>> artistToTracks = new HashMap<>();
        Map<String, List<Track>> genreToTracks = new HashMap<>();

        for (Track track : allTracks) {
            if (track.getArtists() != null) {
                for (String artist : track.getArtists()) {
                    artistToTracks.computeIfAbsent(artist, k -> new ArrayList<>()).add(track);
                }
            }
            if (track.getGenres() != null) {
                for (String genre : track.getGenres()) {
                    genreToTracks.computeIfAbsent(genre, k -> new ArrayList<>()).add(track);
                }
            }
        }
        
        // Создание рёбер для треков одного исполнителя
        for (List<Track> tracks : artistToTracks.values()) {
            addEdgesBetweenTracks(trackGraph, tracks, 1.0); // Вес 1.0 для общего исполнителя
        }

        // Создание рёбер для треков одного жанра
        for (List<Track> tracks : genreToTracks.values()) {
            addEdgesBetweenTracks(trackGraph, tracks, 0.5); // Вес 0.5 для общего жанра
        }
    }
    
    private void addEdgesBetweenTracks(TrackGraph trackGraph, List<Track> tracks, double weight) {
        for (int i = 0; i < tracks.size(); i++) {
            for (int j = i + 1; j < tracks.size(); j++) {
                Track t1 = tracks.get(i);
                Track t2 = tracks.get(j);
                // Добавляем ребро, только если его ещё нет, чтобы не дублировать
                if (!trackGraph.getGraph().containsEdge(t1, t2)) {
                    trackGraph.addEdge(t1, t2);
                    trackGraph.setEdgeWeight(trackGraph.getEdge(t1, t2), weight);
                }
            }
        }
    }

    /**
     * Создаёт бин UserPreferenceRepository как синглтон.
     * Хранит предпочтения единственного пользователя.
     */
    @Bean
    public UserPreferenceRepository userPreferenceRepository() {
        return new UserPreferenceRepository();
    }

    /**
     * Создаёт бин LikeService. Spring автоматически внедряет
     * зависимости (trackGraph, userPreferenceRepository) из контекста.
     */
    @Bean
    public LikeService likeService(TrackGraph trackGraph, UserPreferenceRepository userPreferenceRepository) {
        return new LikeService(trackGraph, userPreferenceRepository);
    }

    /**
     * Создаёт бин RecommendationService. Spring автоматически внедряет
     * зависимости (trackGraph, userPreferenceRepository) из контекста.
     */
    @Bean
    public RecommendationService recommendationService(TrackGraph trackGraph, UserPreferenceRepository userPreferenceRepository) {
        return new RecommendationService(trackGraph, userPreferenceRepository);
    }
} 