package org.textsearch.dto;

import org.textsearch.models.TextSearchResult;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO для результата текстового поиска
 */
public class TextSearchResponse {
    private final boolean success;
    private final String message;
    private final List<TextSearchResultDto> results;
    private final SearchStatsDto stats;

    public TextSearchResponse(boolean success, String message,
                             List<TextSearchResultDto> results, SearchStatsDto stats) {
        this.success = success;
        this.message = message;
        this.results = results;
        this.stats = stats;
    }

    public static TextSearchResponse fromResults(List<TextSearchResult> results) {
        return new TextSearchResponse(
                true,
                results.isEmpty() ? "No results found" : "Search completed successfully",
                results.stream()
                        .map(TextSearchResultDto::fromTextSearchResult)
                        .collect(Collectors.toList()),
                new SearchStatsDto(results.size())
        );
    }

    public static TextSearchResponse error(String message) {
        return new TextSearchResponse(false, message, null, null);
    }

    // Геттеры
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<TextSearchResultDto> getResults() { return results; }
    public SearchStatsDto getStats() { return stats; }

    // Nested DTOs
    public static class TextSearchResultDto {
        private final String trackId;
        private final String title;
        private final String artist;
        private final String album;
        private final float relevanceScore;
        private final String matchedField;
        private final String matchedText;

        public TextSearchResultDto(String trackId, String title, String artist, String album,
                                   float relevanceScore, String matchedField, String matchedText) {
            this.trackId = trackId;
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.relevanceScore = relevanceScore;
            this.matchedField = matchedField;
            this.matchedText = matchedText;
        }

        public static TextSearchResultDto fromTextSearchResult(TextSearchResult result) {
            return new TextSearchResultDto(
                    result.getTrackId(),
                    result.getTitle(),
                    result.getArtist(),
                    result.getAlbum(),
                    result.getRelevanceScore(),
                    result.getMatchedField(),
                    result.getMatchedText()
            );
        }

        // Геттеры
        public String getTrackId() { return trackId; }
        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public String getAlbum() { return album; }
        public float getRelevanceScore() { return relevanceScore; }
        public String getMatchedField() { return matchedField; }
        public String getMatchedText() { return matchedText; }
    }

    public static class SearchStatsDto {
        private final int resultCount;

        public SearchStatsDto(int resultCount) {
            this.resultCount = resultCount;
        }

        public int getResultCount() { return resultCount; }
    }
} 