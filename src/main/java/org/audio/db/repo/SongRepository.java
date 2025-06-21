package org.audio.db.repo;

import org.audio.db.SongData;

import java.util.Optional;

public interface SongRepository {
    void save(SongData song);
    Optional<SongData> findById(String id);
} 