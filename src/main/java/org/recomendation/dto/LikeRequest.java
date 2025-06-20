package org.recomendation.dto;

/**
 * DTO для передачи идентификатора трека при лайке, дизлайке или прослушивании.
 */
public class LikeRequest {
    private String trackId;

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }
} 