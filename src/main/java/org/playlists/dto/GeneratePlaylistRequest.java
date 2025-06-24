package org.playlists.dto;

import java.util.List;

public class GeneratePlaylistRequest {
    private String userId;
    private List<PlaylistRule> rules;
    
    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<PlaylistRule> getRules() { return rules; }
    public void setRules(List<PlaylistRule> rules) { this.rules = rules; }
}