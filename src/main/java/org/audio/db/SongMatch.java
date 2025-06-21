package org.audio.db;

public class SongMatch {
    public final String songId;
    public final int offset;

    public SongMatch(String songId, int offset) {
        this.songId = songId;
        this.offset = offset;
    }
} 