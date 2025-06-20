package org.recomendation.controller;

import org.recomendation.dto.LikeRequest;
import org.recomendation.dto.RecommendationResponse;
import org.recomendation.models.Track;
import org.recomendation.services.LikeService;
import org.recomendation.services.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для работы с системой музыкальных рекомендаций.
 * Предоставляет API для получения рекомендаций, лайков, дизлайков и прослушиваний.
 * Вся бизнес-логика делегируется соответствующим сервисам.
 */
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    /** Сервис генерации рекомендаций */
    private final RecommendationService recommendationService;
    /** Сервис обработки лайков/дизлайков */
    private final LikeService likeService;

    /**
     * Конструктор контроллера с внедрением зависимостей.
     * @param recommendationService сервис рекомендаций
     * @param likeService сервис лайков/дизлайков
     */
    public RecommendationController(RecommendationService recommendationService, LikeService likeService) {
        this.recommendationService = recommendationService;
        this.likeService = likeService;
    }

    /**
     * Получить список рекомендованных треков.
     * @param limit максимальное количество рекомендаций (по умолчанию 10)
     * @return список треков в формате RecommendationResponse
     */
    @GetMapping
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Track> recommendedTracks = recommendationService.recommendTracks(limit);
        return ResponseEntity.ok(new RecommendationResponse(recommendedTracks));
    }

    /**
     * Обрабатывает лайк трека. Увеличивает вес рёбер (+1.0).
     * @param request Запрос, содержащий ID трека.
     * @return HTTP 200 OK в случае успеха.
     */
    @PostMapping("/like")
    public ResponseEntity<Void> likeTrack(@RequestBody LikeRequest request) {
        likeService.likeTrack(request.getTrackId());
        return ResponseEntity.ok().build();
    }

    /**
     * Обрабатывает дизлайк трека. Уменьшает вес рёбер (-1.0).
     * @param request Запрос, содержащий ID трека.
     * @return HTTP 200 OK в случае успеха.
     */
    @PostMapping("/dislike")
    public ResponseEntity<Void> dislikeTrack(@RequestBody LikeRequest request) {
        likeService.dislikeTrack(request.getTrackId());
        return ResponseEntity.ok().build();
    }

    /**
     * Регистрирует полное прослушивание трека. Увеличивает вес рёбер (+0.5).
     * @param request Запрос, содержащий ID трека.
     * @return HTTP 200 OK в случае успеха.
     */
    @PostMapping("/listen")
    public ResponseEntity<Void> listenTrack(@RequestBody LikeRequest request) {
        likeService.listenTrack(request.getTrackId());
        return ResponseEntity.ok().build();
    }
} 