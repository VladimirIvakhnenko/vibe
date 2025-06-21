package org.audio.db;

import org.audio.db.repo.FingerprintRepository;
import org.audio.db.repo.SongRepository;
import org.audio.models.TrackMatch;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InMemoryFingerprintDatabase extends FingerprintDatabase {

    private final SongRepository songRepository;
    private final FingerprintRepository fingerprintRepository;

    private static final int MIN_MATCHES = 5;
    private static final int HAMMING_DISTANCE_THRESHOLD = 1;

    public InMemoryFingerprintDatabase(SongRepository songRepository, FingerprintRepository fingerprintRepository) {
        this.songRepository = songRepository;
        this.fingerprintRepository = fingerprintRepository;
    }

    @Override
    public void addTrack(String trackId, String trackName, List<Long> fingerprints) {
        SongData songData = new SongData(trackId, trackName);
        songRepository.save(songData);

        for (int offset = 0; offset < fingerprints.size(); offset++) {
            long hash = fingerprints.get(offset);
            fingerprintRepository.save(hash, new SongMatch(trackId, offset));
        }
    }

    @Override
    public Optional<TrackMatch> findBestMatch(List<Long> queryHashes) {
        if (queryHashes == null || queryHashes.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Map<Integer, Integer>> candidateMatches = new HashMap<>();
        findAndProcessMatchesForBestMatch(queryHashes, candidateMatches, HAMMING_DISTANCE_THRESHOLD);

        String bestTrackId = null;
        int bestMatches = 0;
        int bestDelta = 0;

        for (Map.Entry<String, Map<Integer, Integer>> entry : candidateMatches.entrySet()) {
            String currentTrackId = entry.getKey();
            for (Map.Entry<Integer, Integer> deltaEntry : entry.getValue().entrySet()) {
                if (deltaEntry.getValue() > bestMatches && deltaEntry.getValue() >= MIN_MATCHES) {
                    bestMatches = deltaEntry.getValue();
                    bestTrackId = currentTrackId;
                    bestDelta = deltaEntry.getKey();
                }
            }
        }

        if (bestTrackId == null) {
            return Optional.empty();
        }

        final int finalBestMatches = bestMatches;
        final int finalBestDelta = bestDelta;
        return songRepository.findById(bestTrackId)
                .map(songData -> TrackMatch.create(
                        songData.id,
                        songData.name,
                        finalBestMatches,
                        queryHashes.size(),
                        finalBestDelta * 1000L / 44100
                ));
    }

    @Override
    public TrackMatch[] bestMatches(List<Long> queryHashes, int limit, float minConfidence) {
        if (queryHashes == null || queryHashes.isEmpty()) {
            return new TrackMatch[0];
        }

        Map<String, TrackMatchInfo> matchInfoMap = new HashMap<>();
        findAndProcessMatchesForBestMatches(queryHashes, matchInfoMap);

        return matchInfoMap.values().stream()
                .filter(info -> {
                    float confidence = (float) info.matchCount / queryHashes.size();
                    return confidence >= minConfidence;
                })
                .sorted(Comparator.comparingInt((TrackMatchInfo info) -> info.matchCount).reversed())
                .limit(limit)
                .map(info -> {
                    final String trackId = info.trackId;
                    final int matchCount = info.matchCount;
                    final int finalBestDelta = info.bestDelta;
                    return songRepository.findById(trackId)
                            .map(songData -> TrackMatch.create(
                                    songData.id,
                                    songData.name,
                                    matchCount,
                                    queryHashes.size(),
                                    finalBestDelta * 1000L / 44100
                            ))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toArray(TrackMatch[]::new);
    }

    private void findAndProcessMatchesForBestMatch(List<Long> queryHashes, Map<String, Map<Integer, Integer>> candidateMatches, int maxHammingDistance) {
        for (int queryOffset = 0; queryOffset < queryHashes.size(); queryOffset++) {
            long queryHash = queryHashes.get(queryOffset);
            Set<Long> hashesToSearch = getHashesWithinHammingDistance(queryHash, maxHammingDistance);

            for (long hash : hashesToSearch) {
                List<SongMatch> matches = fingerprintRepository.findByHash(hash);
                if (matches != null) {
                    for (SongMatch match : matches) {
                        int delta = match.offset - queryOffset;
                        candidateMatches
                                .computeIfAbsent(match.songId, k -> new HashMap<>())
                                .merge(delta, 1, Integer::sum);
                    }
                }
            }
        }
    }

    private void findAndProcessMatchesForBestMatches(List<Long> queryHashes, Map<String, TrackMatchInfo> matchInfoMap) {
        for (int queryOffset = 0; queryOffset < queryHashes.size(); queryOffset++) {
            long queryHash = queryHashes.get(queryOffset);
            Set<Long> hashesToSearch = getHashesWithinHammingDistance(queryHash, HAMMING_DISTANCE_THRESHOLD);

            for (long hash : hashesToSearch) {
                List<SongMatch> matches = fingerprintRepository.findByHash(hash);
                if (matches != null) {
                    for (SongMatch match : matches) {
                        int delta = match.offset - queryOffset;
                        TrackMatchInfo info = matchInfoMap.computeIfAbsent(match.songId,
                                k -> new TrackMatchInfo(match.songId));

                        info.matchCount++;
                        info.deltaCounts.merge(delta, 1, Integer::sum);
                        if (info.deltaCounts.get(delta) > info.deltaCounts.getOrDefault(info.bestDelta, 0)) {
                            info.bestDelta = delta;
                        }
                    }
                }
            }
        }
    }

    private Set<Long> getHashesWithinHammingDistance(long hash, int distance) {
        Set<Long> results = new HashSet<>();
        results.add(hash);
        if (distance <= 0) {
            return results;
        }
        findNeighbors(hash, distance, 0, results);
        return results;
    }

    private void findNeighbors(long hash, int maxDistance, int startBit, Set<Long> results) {
        if (maxDistance == 0) {
            return;
        }
        for (int i = startBit; i < 48; i++) { // Assuming 48-bit hashes
            long neighbor = hash ^ (1L << i);
            if (results.add(neighbor)) {
                findNeighbors(neighbor, maxDistance - 1, i + 1, results);
            }
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