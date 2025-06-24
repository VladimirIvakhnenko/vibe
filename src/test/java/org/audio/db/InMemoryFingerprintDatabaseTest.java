package org.audio.db;

import org.audio.db.repo.FingerprintRepository;
import org.audio.db.repo.SongRepository;
import org.audio.models.TrackMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryFingerprintDatabaseTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private FingerprintRepository fingerprintRepository;

    @InjectMocks
    private InMemoryFingerprintDatabase db;

    @BeforeEach
    void setUp() {
        lenient().when(songRepository.findById("id1")).thenReturn(Optional.of(new SongData("id1", "track1")));
        lenient().when(songRepository.findById("id2")).thenReturn(Optional.of(new SongData("id2", "track2")));
    }

    @Test
    void addAndFindBestMatch() {
        List<Long> fingerprints = List.of(1L, 2L, 3L, 4L, 5L);
        db.addTrack("id1", "track1", fingerprints);

        List<SongMatch> matches = new ArrayList<>();
        for (int i = 0; i < fingerprints.size(); i++) {
            matches.add(new SongMatch("id1", i));
        }

        when(fingerprintRepository.findByHash(anyLong())).thenAnswer(invocation -> {
            long hash = invocation.getArgument(0);
            if (hash >= 1L && hash <= 5L) {
                return List.of(new SongMatch("id1", (int) (hash - 1)));
            }
            return null;
        });

        Optional<TrackMatch> match = db.findBestMatch(fingerprints);
        assertTrue(match.isPresent());
        assertEquals("id1", match.get().getTrackId());
        assertEquals(5, match.get().getMatchScore());
    }

    @Test
    void bestMatchesReturnsMultiple() {
        when(fingerprintRepository.findByHash(anyLong())).thenAnswer(invocation -> {
            long hash = invocation.getArgument(0);
            if (hash == 2L) {
                return List.of(new SongMatch("id1", 1), new SongMatch("id2", 0));
            }
            if (hash == 3L) {
                return List.of(new SongMatch("id1", 2), new SongMatch("id2", 1));
            }
            if (hash == 4L) {
                return List.of(new SongMatch("id2", 2));
            }
            return null;
        });


        TrackMatch[] matches = db.bestMatches(List.of(2L, 3L, 4L), 2, 0.1f);
        assertEquals(2, matches.length);
        assertEquals("id2", matches[0].getTrackId());
        assertEquals("id1", matches[1].getTrackId());
    }

    @Test
    void findBestMatchReturnsEmptyIfNoMatch() {
        when(fingerprintRepository.findByHash(anyLong())).thenReturn(null);
        Optional<TrackMatch> match = db.findBestMatch(List.of(10L, 20L));
        assertTrue(match.isEmpty());
    }
} 