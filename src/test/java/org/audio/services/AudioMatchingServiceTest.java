package org.audio.services;

import org.audio.db.FingerprintDatabase;
import org.audio.models.AudioProcessingResult;
import org.audio.models.TrackMatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AudioMatchingServiceTest {
    private FingerprintDatabase fingerprintDatabase;
    private FingerprintServiceBase fingerprintService;
    private AudioMatchingService service;

    @BeforeEach
    void setUp() {
        fingerprintDatabase = mock(FingerprintDatabase.class);
        fingerprintService = mock(FingerprintServiceBase.class);
        service = new AudioMatchingService(fingerprintDatabase, fingerprintService);
    }

    @Test
    void identifyTrack_returnsErrorOnEmptyAudio() {
        AudioProcessingResult result = service.identifyTrack(new byte[0]);
        assertFalse(result.isSuccess());
        assertEquals("Audio data is empty", result.getErrorMessage());
    }

    @Test
    void identifyTrack_returnsNoMatchIfNoneFound() {
        when(fingerprintService.generateFingerprints(any())).thenReturn(List.of(1L, 2L));
        when(fingerprintDatabase.findBestMatch(any())).thenReturn(Optional.empty());
        byte[] audio = new byte[4];
        AudioProcessingResult result = service.identifyTrack(audio);
        assertTrue(result.isSuccess());
        assertFalse(result.hasMatches());
    }

    @Test
    void identifyTrack_returnsMatchIfFound() {
        when(fingerprintService.generateFingerprints(any())).thenReturn(List.of(1L, 2L));
        TrackMatch match = mock(TrackMatch.class);
        when(fingerprintDatabase.findBestMatch(any())).thenReturn(Optional.of(match));
        byte[] audio = new byte[4];
        AudioProcessingResult result = service.identifyTrack(audio);
        assertTrue(result.isSuccess());
        assertTrue(result.hasMatches());
    }

    @Test
    void registerTrack_throwsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> service.registerTrack(null, "title", new byte[1]));
        assertThrows(IllegalArgumentException.class, () -> service.registerTrack("id", null, new byte[1]));
        assertThrows(IllegalArgumentException.class, () -> service.registerTrack("id", "title", null));
    }

    @Test
    void findBestMatches_returnsErrorOnEmptyAudio() {
        AudioProcessingResult result = service.findBestMatches(new byte[0], 10, 0.5f);
        assertFalse(result.isSuccess());
        assertEquals("Audio data is empty", result.getErrorMessage());
    }

    @Test
    void findBestMatches_returnsMultipleMatches() {
        when(fingerprintService.generateFingerprints(any())).thenReturn(List.of(1L, 2L, 3L));
        TrackMatch[] matches = {mock(TrackMatch.class), mock(TrackMatch.class)};
        when(fingerprintDatabase.bestMatches(any(), eq(2), eq(0.5f))).thenReturn(matches);
        byte[] audio = new byte[4];
        AudioProcessingResult result = service.findBestMatches(audio, 2, 0.5f);
        assertTrue(result.isSuccess());
        assertEquals(2, result.getMatches().length);
    }

    @Test
    void findBestMatches_returnsErrorOnException() {
        when(fingerprintService.generateFingerprints(any())).thenThrow(new RuntimeException("fail"));
        byte[] audio = new byte[4];
        AudioProcessingResult result = service.findBestMatches(audio, 2, 0.5f);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("fail"));
    }

    @Test
    void registerTrack_addsTrack() {
        when(fingerprintService.generateFingerprints(any())).thenReturn(List.of(1L, 2L));
        doNothing().when(fingerprintDatabase).addTrack(any(), any(), any());
        service.registerTrack("id", "title", new byte[4]);
        verify(fingerprintDatabase, times(1)).addTrack(eq("id"), eq("title"), any());
    }

    @Test
    void identifyTrack_returnsErrorOnException() {
        when(fingerprintService.generateFingerprints(any())).thenThrow(new RuntimeException("fail"));
        byte[] audio = new byte[4];
        AudioProcessingResult result = service.identifyTrack(audio);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("fail"));
    }
} 