package org.recomendation.configs;

import org.recomendation.db.TrackGraph;
import org.recomendation.db.UserPreferenceRepository;
import org.recomendation.models.Track;
import org.recomendation.services.LikeService;
import org.recomendation.services.RecommendationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring-конфигурация для модуля рекомендаций.
 * Определяет бины (компоненты), управляемые Spring, и настраивает их зависимости.
 */
@Configuration
public class RecommendationConfig {

    /**
     * Создаёт бин TrackGraph как синглтон.
     * При старте приложения граф заполняется тестовыми данными (треками и связями),
     * чтобы можно было сразу тестировать рекомендации.
     */
    @Bean
    public TrackGraph trackGraph() {
        TrackGraph graph = new TrackGraph();

        // Добавляем тестовые данные для графа
        Track t1 = new Track("t1", "Bohemian Rhapsody", "Rock", "Queen");
        Track t2 = new Track("t2", "Don't Stop Me Now", "Rock", "Queen");
        Track t3 = new Track("t3", "Billie Jean", "Pop", "Michael Jackson");
        Track t4 = new Track("t4", "Thriller", "Pop", "Michael Jackson");
        Track t5 = new Track("t5", "Smells Like Teen Spirit", "Grunge", "Nirvana");

        graph.addTrack(t1);
        graph.addTrack(t2);
        graph.addTrack(t3);
        graph.addTrack(t4);
        graph.addTrack(t5);

        // Связи по жанру/исполнителю
        graph.connectTracks(t1, t2, 5.0);
        graph.connectTracks(t3, t4, 5.0);
        
        // Слабая связь между разными жанрами
        graph.connectTracks(t1, t5, 1.0);

        return graph;
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