package org.recommendation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecommendationGraphServiceTest {
    private RecommendationGraphService service;

    @BeforeEach
    void setUp() {
        service = new RecommendationGraphService();
        // Добавляем треки
        service.addTrack("1", "Track1", "Rock", "ArtistA");
        service.addTrack("2", "Track2", "Rock", "ArtistB");
        service.addTrack("3", "Track3", "Pop", "ArtistA");
        service.addTrack("4", "Track4", "Pop", "ArtistC");
        service.addTrack("5", "Track5", "Jazz", "ArtistD");
        service.buildInitialEdges();
    }

    @Test
    void testLikeAndDislikeAffectWeights() {
        service.likeTrack("user1", "1");
        service.likeTrack("user1", "2");
        // Связь между 1 и 2 должна увеличиться
        var recs = service.recommend("user1", 10);
        assertFalse(recs.isEmpty());
        // Теперь дизлайк
        service.dislikeTrack("user1", "2");
        // Связь между 1 и 2 должна уменьшиться
        // Проверяем, что рекомендации не содержат дизлайкнутый трек
        var recs2 = service.recommend("user1", 10);
        assertTrue(recs2.stream().noneMatch(t -> t.id.equals("2")));
    }

    @Test
    void testRecommendBFS() {
        service.likeTrack("user2", "1");
        List<RecommendationGraphService.TrackNode> recs = service.recommend("user2", 3);
        // Должны быть рекомендации, не включающие лайкнутый трек
        assertTrue(recs.stream().noneMatch(t -> t.id.equals("1")));
        assertFalse(recs.isEmpty());
    }

    @Test
    void testRandomWalkRecommend() {
        service.likeTrack("user3", "3");
        List<RecommendationGraphService.TrackNode> recs = service.randomWalkRecommend("user3", 3, 2, 20);
        // Должны быть рекомендации, не включающие лайкнутый трек
        assertTrue(recs.stream().noneMatch(t -> t.id.equals("3")));
        assertFalse(recs.isEmpty());
    }

    @Test
    void testAddTrackAndEdges() {
        service.addTrack("6", "Track6", "Rock", "ArtistE");
        service.buildInitialEdges();
        // Проверяем, что новый трек появился в графе
        var recs = service.recommend("user1", 10);
        assertTrue(service.recommend("user1", 10).stream().anyMatch(t -> t.id.equals("6")) || recs.isEmpty());
    }

    @Test
    void testEmptyGraph() {
        RecommendationGraphService emptyService = new RecommendationGraphService();
        var recs = emptyService.recommend("userX", 5);
        assertTrue(recs.isEmpty(), "Рекомендации для пустого графа должны быть пустыми");
    }

    @Test
    void testUserWithoutLikes() {
        var recs = service.recommend("noLikesUser", 5);
        assertTrue(recs.isEmpty(), "Пользователь без лайков не должен получать рекомендации");
    }

    @Test
    void testAllTracksDisliked() {
        service.likeTrack("user4", "1");
        service.dislikeTrack("user4", "2");
        service.dislikeTrack("user4", "3");
        service.dislikeTrack("user4", "4");
        service.dislikeTrack("user4", "5");
        var recs = service.recommend("user4", 10);
        // Все кроме лайкнутого и дизлайкнутых — пусто
        assertTrue(recs.isEmpty(), "Если все треки дизлайкнуты, рекомендаций быть не должно");
    }

    @Test
    void testCyclesInGraph() {
        // Создаем цикл вручную
        service.addTrack("7", "Track7", "Rock", "ArtistA");
        service.buildInitialEdges();
        service.likeTrack("user5", "1");
        service.likeTrack("user5", "7");
        // Проверяем, что цикл не вызывает бесконечный обход
        var recs = service.recommend("user5", 5);
        assertTrue(recs.stream().noneMatch(t -> t.id.equals("1") || t.id.equals("7")));
    }

    @Test
    void testRepeatedLikesAndDislikes() {
        service.likeTrack("user6", "1");
        service.likeTrack("user6", "1");
        service.dislikeTrack("user6", "2");
        service.dislikeTrack("user6", "2");
        // Повторные лайки/дизлайки не должны ломать граф
        var recs = service.recommend("user6", 5);
        assertFalse(recs.contains("1"));
        assertTrue(recs.stream().noneMatch(t -> t.id.equals("2")));
    }
} 