package org.recomendation.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.recomendation.db.TrackGraph;
import org.recomendation.db.UserPreferenceRepository;
import org.recomendation.models.Track;
import org.recomendation.models.UserPreference;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private TrackGraph trackGraph;

    @Mock
    private UserPreferenceRepository userPreferenceRepository;

    @InjectMocks
    private LikeService likeService;

    private Track track1, track2;
    private UserPreference user;

    @BeforeEach
    void setUp() {
        track1 = new Track("t1", "Title 1", "Genre", "Artist");
        track2 = new Track("t2", "Title 2", "Genre", "Artist");
        user = new UserPreference("u1");
    }

    @Test
    void likeTrack_shouldIncreaseWeight_whenOtherTracksAreLiked() {
        // Arrange
        user.getLikedTrackIds().add("t2");
        when(userPreferenceRepository.findByUserId("u1")).thenReturn(Optional.of(user));
        when(trackGraph.getTrackById("t1")).thenReturn(Optional.of(track1));
        when(trackGraph.getTrackById("t2")).thenReturn(Optional.of(track2));

        // Act
        likeService.likeTrack("u1", "t1");

        // Assert
        verify(trackGraph).updateEdgeWeight(track1, track2, 1.0);
        verify(userPreferenceRepository).save(user);
    }

    @Test
    void likeTrack_shouldNotUpdateWeight_whenNoOtherTracksAreLiked() {
        // Arrange
        when(userPreferenceRepository.findByUserId("u1")).thenReturn(Optional.of(user));
        when(trackGraph.getTrackById("t1")).thenReturn(Optional.of(track1));

        // Act
        likeService.likeTrack("u1", "t1");

        // Assert
        verify(trackGraph, never()).updateEdgeWeight(any(), any(), anyDouble());
        verify(userPreferenceRepository).save(user);
    }

    @Test
    void dislikeTrack_shouldDecreaseWeight() {
        // Arrange
        user.getLikedTrackIds().add("t2");
        when(userPreferenceRepository.findByUserId("u1")).thenReturn(Optional.of(user));
        when(trackGraph.getTrackById("t1")).thenReturn(Optional.of(track1));
        when(trackGraph.getTrackById("t2")).thenReturn(Optional.of(track2));

        // Act
        likeService.dislikeTrack("u1", "t1");

        // Assert
        verify(trackGraph).updateEdgeWeight(track1, track2, -1.0);
        verify(userPreferenceRepository).save(user);
    }
} 