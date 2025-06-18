package org.audio.db;

import org.audio.models.TrackMatch;

import java.util.List;
import java.util.Optional;

public abstract class FingerprintDatabase {
    public abstract Optional<TrackMatch> findBestMatch(List<Long> queryHashes);

    public abstract TrackMatch[] bestMatches(List<Long> queryHashes, int limit, float minConfidence);

    public abstract void addTrack(String trackId, String trackName, List<Long> fingerprints);
}