package org.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Recommendations", description = "Графовые рекомендации треков с учетом лайков/дизлайков пользователя")
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    @Autowired
    private RecommendationGraphService graphService;

    @Operation(summary = "Графовые рекомендации (BFS)", description = "Рекомендации на основе графа связей между треками и лайков пользователя")
    @GetMapping
    public Map<String, List<Map<String, String>>> getRecommendations(@RequestParam("user_id") String userId,
                                                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {
        var recs = graphService.recommend(userId, limit);
        return Map.of("tracks", recs.stream().map(t -> Map.of(
                "id", t.id,
                "title", t.title,
                "genre", t.genre,
                "artist", t.artist
        )).collect(Collectors.toList()));
    }

    @Operation(summary = "Лайк трека", description = "Поставить лайк треку для пользователя и обновить веса связей в графе")
    @PostMapping("/like")
    public void like(@RequestParam String userId, @RequestParam String trackId) {
        graphService.likeTrack(userId, trackId);
    }

    @Operation(summary = "Дизлайк трека", description = "Поставить дизлайк треку для пользователя и обновить веса связей в графе")
    @PostMapping("/dislike")
    public void dislike(@RequestParam String userId, @RequestParam String trackId) {
        graphService.dislikeTrack(userId, trackId);
    }

    @Operation(summary = "Добавить трек в граф", description = "Добавить новый трек в граф рекомендаций")
    @PostMapping("/add-track")
    public void addTrack(@RequestParam String id, @RequestParam String title, @RequestParam String genre, @RequestParam String artist) {
        graphService.addTrack(id, title, genre, artist);
    }

    @Operation(summary = "Построить связи между треками", description = "Построить рёбра между треками по жанру и исполнителю")
    @PostMapping("/build-edges")
    public void buildEdges() {
        graphService.buildInitialEdges();
    }

    @Operation(summary = "Графовые рекомендации (random walk)", description = "Рекомендации с разнообразием на основе случайных блужданий по графу")
    @GetMapping("/random-walk")
    public Map<String, List<Map<String, String>>> getRandomWalkRecommendations(@RequestParam("user_id") String userId,
                                                                              @RequestParam(value = "limit", defaultValue = "10") int limit,
                                                                              @RequestParam(value = "depth", defaultValue = "2") int depth,
                                                                              @RequestParam(value = "walks", defaultValue = "20") int walks) {
        var recs = graphService.randomWalkRecommend(userId, limit, depth, walks);
        return Map.of("tracks", recs.stream().map(t -> Map.of(
                "id", t.id,
                "title", t.title,
                "genre", t.genre,
                "artist", t.artist
        )).collect(Collectors.toList()));
    }

    @Operation(summary = "Рекомендации с учетом компонент сильной связности (SCC)", description = "Если у пользователя мало лайков — рекомендации из компоненты сильной связности, иначе обычные.")
    @GetMapping("/scc")
    public Map<String, List<Map<String, String>>> getSccRecommendations(@RequestParam("user_id") String userId,
                                                                       @RequestParam(value = "limit", defaultValue = "10") int limit) {
        var recs = graphService.recommendWithSCC(userId, limit);
        return Map.of("tracks", recs.stream().map(t -> Map.of(
                "id", t.id,
                "title", t.title,
                "genre", t.genre,
                "artist", t.artist
        )).collect(Collectors.toList()));
    }

    @Operation(summary = "Загрузить треки из JSON", description = "Загружает треки из файла ресурсов в граф рекомендаций")
    @PostMapping("/load-json")
    public Map<String, String> loadTracksFromJson(@RequestParam(value = "file", defaultValue = "spotify_1000_tracks_20250618_153243.json") String file) {
        try {
            graphService.loadTracksFromJson(file);
            return Map.of("status", "ok", "message", "Треки успешно загружены из json");
        } catch (Exception e) {
            return Map.of("status", "error", "message", "Ошибка загрузки треков: " + e.getMessage());
        }
    }
} 