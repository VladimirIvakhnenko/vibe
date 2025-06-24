package org.playlists.dto;

public class PlaylistRule {
    private String source;
    private String genre;
    private double weight;
    
    // Getters and setters
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}