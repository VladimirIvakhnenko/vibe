package org.textsearch.db;

import java.util.List;

import org.textsearch.indexing.InvertedIndex;
import org.textsearch.models.TextSearchResult;
import org.textsearch.models.TrackMetadata;

public abstract class TextSearchDatabase {
    public abstract void addTrack(TrackMetadata metadata);
    public abstract void removeTrack(String trackId);
    public abstract void updateTrack(TrackMetadata metadata);
    public abstract List<TextSearchResult> searchTracks(String query, int maxResults, boolean fuzzy);
    public abstract TrackMetadata getTrack(String trackId);
    public abstract List<String> getSuggestions(String prefix, int maxSuggestions);
    public abstract InvertedIndex.IndexStats getStats();
} 