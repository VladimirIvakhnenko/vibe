package org.audio.db;

import org.audio.models.TrackMatch;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class InMemoryFingerprintDatabase extends FingerprintDatabase {
    private final Map<Long, List<SongMatch>> hashMap = new ConcurrentHashMap<>();
    private final Map<String, SongData> songs = new ConcurrentHashMap<>();
    private static final int MIN_MATCHES = 5;

    @Override
    public void addTrack(String trackId, String trackName, List<Long> fingerprints) {
        SongData songData = new SongData(trackId, trackName);
        songs.put(trackId, songData);

        for (int offset = 0; offset < fingerprints.size(); offset++) {
            long hash = fingerprints.get(offset);
            hashMap.computeIfAbsent(hash, k -> new ArrayList<>())
                    .add(new SongMatch(trackId, offset));
        }
    }

    @Override
    public Optional<TrackMatch> findBestMatch(List<Long> queryHashes) {
        if (queryHashes == null || queryHashes.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Map<Integer, Integer>> candidateMatches = new HashMap<>();

        for (int queryOffset = 0; queryOffset < queryHashes.size(); queryOffset++) {
            long hash = queryHashes.get(queryOffset);
            List<SongMatch> matches = hashMap.get(hash);

            if (matches != null) {
                for (SongMatch match : matches) {
                    int delta = match.offset - queryOffset;
                    candidateMatches
                            .computeIfAbsent(match.songId, k -> new HashMap<>())
                            .merge(delta, 1, Integer::sum);
                }
            }
        }

        String bestTrackId = null;
        int bestMatches = 0;
        int bestDelta = 0;

        for (Map.Entry<String, Map<Integer, Integer>> entry : candidateMatches.entrySet()) {
            for (Map.Entry<Integer, Integer> deltaEntry : entry.getValue().entrySet()) {
                if (deltaEntry.getValue() > bestMatches && deltaEntry.getValue() >= MIN_MATCHES) {
                    bestMatches = deltaEntry.getValue();
                    bestTrackId = entry.getKey();
                    bestDelta = deltaEntry.getKey();
                }
            }
        }

        if (bestTrackId == null) {
            return Optional.empty();
        }

        SongData songData = songs.get(bestTrackId);
        float confidence = (float) bestMatches / queryHashes.size();

        return Optional.of(TrackMatch.create(
                bestTrackId,
                songData.name,
                bestMatches,
                queryHashes.size(),
                bestDelta * 1000L / 44100 // Convert to milliseconds
        ));
    }

    @Override
    public TrackMatch[] bestMatches(List<Long> queryHashes, int limit, float minConfidence) {
        if (queryHashes == null || queryHashes.isEmpty()) {
            return new TrackMatch[0];
        }

        Map<String, TrackMatchInfo> matchInfoMap = new HashMap<>();

        for (int queryOffset = 0; queryOffset < queryHashes.size(); queryOffset++) {
            long hash = queryHashes.get(queryOffset);
            List<SongMatch> matches = hashMap.get(hash);

            if (matches != null) {
                for (SongMatch match : matches) {
                    int delta = match.offset - queryOffset;
                    TrackMatchInfo info = matchInfoMap.computeIfAbsent(match.songId,
                            k -> new TrackMatchInfo(match.songId));

                    info.matchCount++;
                    // Keep the most common delta
                    info.deltaCounts.merge(delta, 1, Integer::sum);
                    if (info.deltaCounts.get(delta) > info.deltaCounts.getOrDefault(info.bestDelta, 0)) {
                        info.bestDelta = delta;
                    }
                }
            }
        }

        return matchInfoMap.values().stream()
                .filter(info -> {
                    float confidence = (float) info.matchCount / queryHashes.size();
                    return confidence >= minConfidence;
                })
                .sorted(Comparator.comparingInt((TrackMatchInfo info) -> info.matchCount).reversed())
                .limit(limit)
                .map(info -> {
                    SongData songData = songs.get(info.trackId);
                    return TrackMatch.create(
                            info.trackId,
                            songData.name,
                            info.matchCount,
                            queryHashes.size(),
                            info.bestDelta * 1000L / 44100
                    );
                })
                .toArray(TrackMatch[]::new);
    }

    private static class SongData {
        final String id;
        final String name;

        SongData(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static class SongMatch {
        final String songId;
        final int offset;

        SongMatch(String songId, int offset) {
            this.songId = songId;
            this.offset = offset;
        }
    }

    private static class TrackMatchInfo {
        final String trackId;
        int matchCount;
        int bestDelta;
        final Map<Integer, Integer> deltaCounts = new HashMap<>();

        TrackMatchInfo(String trackId) {
            this.trackId = trackId;
            this.matchCount = 0;
            this.bestDelta = 0;
        }
    }
}