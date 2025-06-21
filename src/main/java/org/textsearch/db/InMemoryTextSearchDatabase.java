package org.textsearch.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.textsearch.indexing.InvertedIndex;
import org.textsearch.models.TextSearchResult;
import org.textsearch.models.TrackMetadata;

@Repository
public class InMemoryTextSearchDatabase extends TextSearchDatabase {
    private final InvertedIndex invertedIndex = new InvertedIndex();
    private final Map<String, TrackMetadata> tracks = new HashMap<>();

    @Override
    public void addTrack(TrackMetadata metadata) {
        if (metadata == null) return;
        tracks.put(metadata.getTrackId(), metadata);
        invertedIndex.addTrack(metadata);
    }

    @Override
    public void removeTrack(String trackId) {
        tracks.remove(trackId);
        invertedIndex.removeTrack(trackId);
    }

    @Override
    public void updateTrack(TrackMetadata metadata) {
        if (metadata == null) return;
        removeTrack(metadata.getTrackId());
        addTrack(metadata);
    }

    @Override
    public List<TextSearchResult> searchTracks(String query, int maxResults, boolean fuzzy) {
        List<TextSearchResult> results = new ArrayList<>();
        if (fuzzy) {
            List<String> trackIds = invertedIndex.fuzzyAutomatonSearch(query, 2, maxResults);
            for (String trackId : trackIds) {
                TrackMetadata meta = tracks.get(trackId);
                if (meta != null) {
                    results.add(new TextSearchResult(
                        trackId,
                        meta.getTitle(),
                        meta.getArtist(),
                        meta.getAlbum(),
                        1.0f,
                        "levenshtein_automaton",
                        query
                    ));
                }
            }
        } else {
            List<String> trackIds = invertedIndex.search(query, maxResults);
            for (String trackId : trackIds) {
                TrackMetadata meta = tracks.get(trackId);
                if (meta != null) {
                    results.add(new TextSearchResult(
                        trackId,
                        meta.getTitle(),
                        meta.getArtist(),
                        meta.getAlbum(),
                        1.0f,
                        "exact_match",
                        query
                    ));
                }
            }
        }
        return results;
    }

    @Override
    public TrackMetadata getTrack(String trackId) {
        return tracks.get(trackId);
    }

    @Override
    public List<String> getSuggestions(String prefix, int maxSuggestions) {
        if (prefix == null || prefix.trim().isEmpty()) return Collections.emptyList();
        String lowerPrefix = prefix.toLowerCase();
        Set<String> suggestions = new HashSet<>();
        for (String word : invertedIndex.getWords()) {
            if (word.startsWith(lowerPrefix)) {
                suggestions.add(word);
                if (suggestions.size() >= maxSuggestions) break;
            }
        }
        return new ArrayList<>(suggestions);
    }

    @Override
    public InvertedIndex.IndexStats getStats() {
        return invertedIndex.getStats();
    }

    public InvertedIndex getInvertedIndex() {
        return invertedIndex;
    }
} 