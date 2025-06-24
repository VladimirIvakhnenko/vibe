package org.playlists.dto;

import org.playlists.models.Playlist;

public class GeneratePlaylistResponse {
    private boolean success;
    private String message;
    private Playlist playlist;
    
    // Constructors
    public GeneratePlaylistResponse(boolean success, String message, Playlist playlist) {
        this.success = success;
        this.message = message;
        this.playlist = playlist;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Playlist getPlaylist() { return playlist; }
}