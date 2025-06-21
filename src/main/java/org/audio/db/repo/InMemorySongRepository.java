package org.audio.db.repo;

import org.audio.db.SongData;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySongRepository implements SongRepository {

    private final Map<String, SongData> songs = new ConcurrentHashMap<>();

    @Override
    public void save(SongData song) {
        songs.put(song.id, song);
    }

    @Override
    public Optional<SongData> findById(String id) {
        return Optional.ofNullable(songs.get(id));
    }
} 