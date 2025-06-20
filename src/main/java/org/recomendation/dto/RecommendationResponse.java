package org.recomendation.dto;

import org.recomendation.models.Track;
import java.util.List;

/**
 * DTO (Data Transfer Object) для ответа на запрос о рекомендациях.
 * Содержит список рекомендованных треков.
 */
public class RecommendationResponse {
    private final List<Track> tracks;

    public RecommendationResponse(List<Track> tracks) {
        this.tracks = tracks;
    }

    public List<Track> getTracks() {
        return tracks;
    }
} 