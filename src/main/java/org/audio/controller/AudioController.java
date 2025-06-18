package org.audio.controller;

import org.audio.dto.MatchResponse;
import org.audio.services.AudioMatchingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private final AudioMatchingService audioMatchingService;

    public AudioController(AudioMatchingService audioMatchingService) {
        this.audioMatchingService = audioMatchingService;
    }

    @PostMapping(path = "/identify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MatchResponse> identifyTrack(
            @RequestParam("audioFile") MultipartFile audioFile) {

        try {
            byte[] audioData = audioFile.getBytes();
            var result = audioMatchingService.identifyTrack(audioData);

            if (!result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MatchResponse(false, result.getErrorMessage(), null, null));
            }

            if (!result.hasMatches()) {
                return ResponseEntity.ok(new MatchResponse(true, "No matches found", null, null));
            }

            return ResponseEntity.ok(MatchResponse.fromProcessingResult(result));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MatchResponse(false, "Error reading audio file", null, null));
        }
    }

    @PostMapping(path = "/top-similar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MatchResponse> findSimilarTracks(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0.5") float minConfidence) {

        try {
            byte[] audioData = audioFile.getBytes();
            var result = audioMatchingService.findBestMatches(audioData, limit, minConfidence);

            if (!result.isSuccess()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new MatchResponse(false, result.getErrorMessage(), null, null));
            }

            return ResponseEntity.ok(MatchResponse.fromProcessingResult(result));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MatchResponse(false, "Error reading audio file", null, null));
        }
    }

    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MatchResponse> registerTrack(
            @RequestParam("trackId") String trackId,
            @RequestParam("title") String title,
            @RequestParam("audioFile") MultipartFile audioFile) {

        try {
            byte[] audioData = audioFile.getBytes();
            audioMatchingService.registerTrack(trackId, title, audioData);

            return ResponseEntity.ok(new MatchResponse(
                    true,
                    "Track registered successfully",
                    null,
                    null
            ));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MatchResponse(false, "Error reading audio file", null, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MatchResponse(false, e.getMessage(), null, null));
        }
    }
}
