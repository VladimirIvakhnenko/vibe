package org.textsearch.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.textsearch.dto.TextSearchResponse;
import org.textsearch.models.TrackMetadata;
import org.textsearch.services.ITextSearchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST контроллер для текстового поиска треков
 */
@RestController
@RequestMapping("/api/textsearch")
@Tag(name = "Text Search", description = "API для текстового поиска по трекам")
public class TextSearchController {

    private final ITextSearchService textSearchService;

    public TextSearchController(ITextSearchService textSearchService) {
        this.textSearchService = textSearchService;
    }

    /**
     * Поиск треков по текстовому запросу
     */
    @Operation(summary = "Поиск треков", description = "Выполняет текстовый поиск по трекам с поддержкой нечеткого поиска и синонимов")
    @GetMapping("/search")
    public ResponseEntity<TextSearchResponse> searchTracks(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "false") boolean fuzzy) {

        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(TextSearchResponse.error("Search query cannot be empty"));
            }

            List<org.textsearch.models.TextSearchResult> results = 
                textSearchService.searchTracks(query, limit, fuzzy);

            return ResponseEntity.ok(TextSearchResponse.fromResults(results));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TextSearchResponse.error("Error during search: " + e.getMessage()));
        }
    }

    /**
     * Регистрация метаданных трека для поиска
     */
    @Operation(summary = "Регистрация трека", description = "Регистрирует трек для текстового поиска")
    @PostMapping("/register")
    public ResponseEntity<TextSearchResponse> registerTrack(
            @RequestParam("trackId") String trackId,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist,
            @RequestParam(value = "album", required = false) String album,
            @RequestParam(value = "lyrics", required = false) String lyrics,
            @RequestParam(value = "genres", required = false) Set<String> genres,
            @RequestParam(value = "year", defaultValue = "0") int year) {

        try {
            if (trackId == null || title == null || artist == null) {
                return ResponseEntity.badRequest()
                        .body(TextSearchResponse.error("trackId, title, and artist are required"));
            }

            TrackMetadata metadata = new TrackMetadata(
                    trackId, title, artist, album, lyrics, genres, year);

            textSearchService.registerTrack(metadata);

            return ResponseEntity.ok(new TextSearchResponse(
                    true,
                    "Track registered successfully for text search",
                    null,
                    null
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TextSearchResponse.error("Error registering track: " + e.getMessage()));
        }
    }

    /**
     * Получение предложений для автодополнения
     */
    @Operation(summary = "Подсказки", description = "Возвращает подсказки по префиксу для автодополнения")
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(
            @RequestParam("prefix") String prefix,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            if (prefix == null || prefix.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(List.of());
            }

            List<String> suggestions = textSearchService.getSuggestions(prefix, limit);
            return ResponseEntity.ok(suggestions);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

    /**
     * Удаление трека из поискового индекса
     */
    @Operation(summary = "Удаление трека", description = "Удаляет трек из поискового индекса по trackId")
    @DeleteMapping("/tracks/{trackId}")
    public ResponseEntity<TextSearchResponse> removeTrack(@PathVariable String trackId) {

        try {
            if (trackId == null || trackId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(TextSearchResponse.error("Track ID cannot be empty"));
            }

            textSearchService.removeTrack(trackId);

            return ResponseEntity.ok(new TextSearchResponse(
                    true,
                    "Track removed from search index",
                    null,
                    null
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TextSearchResponse.error("Error removing track: " + e.getMessage()));
        }
    }

    /**
     * Обновление метаданных трека
     */
    @Operation(summary = "Обновление трека", description = "Обновляет метаданные трека по trackId")
    @PutMapping("/tracks/{trackId}")
    public ResponseEntity<TextSearchResponse> updateTrack(
            @PathVariable String trackId,
            @RequestParam("title") String title,
            @RequestParam("artist") String artist,
            @RequestParam(value = "album", required = false) String album,
            @RequestParam(value = "lyrics", required = false) String lyrics,
            @RequestParam(value = "genres", required = false) Set<String> genres,
            @RequestParam(value = "year", defaultValue = "0") int year) {

        try {
            if (title == null || artist == null) {
                return ResponseEntity.badRequest()
                        .body(TextSearchResponse.error("title and artist are required"));
            }

            TrackMetadata metadata = new TrackMetadata(
                    trackId, title, artist, album, lyrics, genres, year);

            textSearchService.updateTrack(metadata);

            return ResponseEntity.ok(new TextSearchResponse(
                    true,
                    "Track metadata updated successfully",
                    null,
                    null
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TextSearchResponse.error("Error updating track: " + e.getMessage()));
        }
    }

    @Operation(summary = "Загрузить треки из JSON", description = "Загружает треки из файла ресурсов в индекс поиска")
    @PostMapping("/load-json")
    public ResponseEntity<TextSearchResponse> loadTracksFromJson(@RequestParam(value = "file", defaultValue = "spotify_1000_tracks_20250618_153243.json") String file) {
        try {
            textSearchService.loadTracksFromJson(file);
            return ResponseEntity.ok(new TextSearchResponse(true, "Треки успешно загружены из json", null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(TextSearchResponse.error("Ошибка загрузки треков: " + e.getMessage()));
        }
    }
} 