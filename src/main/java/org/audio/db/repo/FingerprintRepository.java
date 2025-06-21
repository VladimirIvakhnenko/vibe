package org.audio.db.repo;

import org.audio.db.SongMatch;

import java.util.List;

public interface FingerprintRepository {
    void save(long hash, SongMatch match);
    List<SongMatch> findByHash(long hash);
    List<SongMatch> findByHashes(List<Long> hashes);
} 