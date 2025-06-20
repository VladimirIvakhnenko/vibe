package org.recomendation.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recomendation.db.TrackGraph;
import org.recomendation.db.UserPreferenceRepository;
import org.recomendation.models.Track;
import org.recomendation.models.UserPreference;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    private RecommendationService recommendationService;

    private TrackGraph trackGraph;

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    private Track t1, t2, t3, t4, t5;
    private UserPreference user;

    @BeforeEach
    void setUp() {
        trackGraph = new TrackGraph();
        recommendationService = new RecommendationService(trackGraph, userPreferenceRepository);

        t1 = new Track("t1", "Title 1", "Rock", "Artist A");
        t2 = new Track("t2", "Title 2", "Rock", "Artist A");
        t3 = new Track("t3", "Title 3", "Pop", "Artist B");
        t4 = new Track("t4", "Title 4", "Pop", "Artist B");
        t5 = new Track("t5", "Title 5", "Jazz", "Artist C");

        trackGraph.addTrack(t1);
        trackGraph.addTrack(t2);
        trackGraph.addTrack(t3);
        trackGraph.addTrack(t4);
        trackGraph.addTrack(t5);

        trackGraph.connectTracks(t1, t2, 5.0); // Связь по артисту/жанру
        trackGraph.connectTracks(t1, t3, 2.0);
        trackGraph.connectTracks(t2, t4, 1.0);
        trackGraph.connectTracks(t3, t4, 5.0);

        user = new UserPreference("u1");
        when(userPreferenceRepository.findByUserId("u1")).thenReturn(Optional.of(user));
    }

    @Test
    void recommendTracks_shouldReturnWeightedAndSortedTracks() {
        user.getLikedTrackIds().add("t1");

        List<Track> recommendations = recommendationService.recommendTracks("u1", 10);

        assertEquals(2, recommendations.size());
        assertEquals(t2, recommendations.get(0)); // t2 имеет больший вес (5.0)
        assertEquals(t3, recommendations.get(1)); // t3 имеет меньший вес (2.0)
    }

    @Test
    void recommendTracks_shouldExcludeDislikedTracks() {
        user.getLikedTrackIds().add("t1");
        user.getDislikedTrackIds().add("t2");

        List<Track> recommendations = recommendationService.recommendTracks("u1", 10);

        assertEquals(1, recommendations.size());
        assertEquals(t3, recommendations.get(0));
    }

    @Test
    void recommendTracks_shouldReturnEmptyList_whenUserHasNoLikes() {
        List<Track> recommendations = recommendationService.recommendTracks("u1", 10);
        assertTrue(recommendations.isEmpty());
    }
} 