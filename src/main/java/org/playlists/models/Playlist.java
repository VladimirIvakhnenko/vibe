package org.playlists.models;

import java.util.List;

public class Playlist {
    private String id;
    private String name;
    private List<Track> tracks;
    
    // Constructors, getters and setters
    public Playlist(String id, String name, List<Track> tracks) {
        this.id = id;
        this.name = name;
        this.tracks = tracks;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public List<Track> getTracks() { return tracks; }
}