package org.audio.db.repo;

import org.audio.db.SongMatch;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryFingerprintRepository implements FingerprintRepository {

    private final Map<Long, List<SongMatch>> hashMap = new ConcurrentHashMap<>();

    @Override
    public void save(long hash, SongMatch match) {
        hashMap.computeIfAbsent(hash, k -> new ArrayList<>()).add(match);
    }

    @Override
    public List<SongMatch> findByHash(long hash) {
        return hashMap.get(hash);
    }

    @Override
    public List<SongMatch> findByHashes(List<Long> hashes) {
        return hashes.stream()
                .map(hashMap::get)
                .filter(list -> list != null && !list.isEmpty())
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
} 