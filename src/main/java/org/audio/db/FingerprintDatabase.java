package org.audio.db;

import org.audio.models.TrackMatch;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface FingerprintDatabase {
    Optional<TrackMatch> findBestMatch(List<Long> queryHashes);

    default void addFingerprints(String trackId, List<Long> fingerprints) {
        throw new UnsupportedOperationException("Add operation not supported");
    }
}