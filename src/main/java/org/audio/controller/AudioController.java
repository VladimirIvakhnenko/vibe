package org.audio.controller;

import org.audio.dto.MatchResponse;
import org.audio.services.AudioMatchingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.io.IOException;

@Tag(name = "Audio", description = "Audio fingerprinting и идентификация треков")
@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private final AudioMatchingService audioMatchingService;

    public AudioController(AudioMatchingService audioMatchingService) {
        this.audioMatchingService = audioMatchingService;
    }

    @Operation(summary = "Идентификация трека по аудиофайлу", description = "Возвращает наиболее подходящий трек по аудиофайлу.")
    @PostMapping(path = "/identify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MatchResponse> identifyTrack(
            @Parameter(description = "Аудиофайл для идентификации", required = true)
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

    @Operation(summary = "Поиск похожих треков", description = "Возвращает топ-N похожих треков по аудиофайлу.")
    @PostMapping(path = "/top-similar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MatchResponse> findSimilarTracks(
            @Parameter(description = "Аудиофайл для поиска", required = true)
            @RequestParam("audioFile") MultipartFile audioFile,
            @Parameter(description = "Максимальное количество результатов", example = "20")
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "Минимальная уверенность", example = "0.5")
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

    @Operation(summary = "Регистрация нового трека", description = "Добавляет новый трек в базу по аудиофайлу.")
    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MatchResponse> registerTrack(
            @Parameter(description = "ID трека", required = true)
            @RequestParam("trackId") String trackId,
            @Parameter(description = "Название трека", required = true)
            @RequestParam("title") String title,
            @Parameter(description = "Аудиофайл трека", required = true)
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
