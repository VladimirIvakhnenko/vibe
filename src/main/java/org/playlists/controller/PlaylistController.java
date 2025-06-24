package org.playlists.controller;

import org.playlists.dto.GeneratePlaylistRequest;
import org.playlists.dto.GeneratePlaylistResponse;
import org.playlists.services.PlaylistGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Playlists", description = "Генерация и работа с плейлистами")
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    private final PlaylistGeneratorService playlistGeneratorService;

    public PlaylistController(PlaylistGeneratorService playlistGeneratorService) {
        this.playlistGeneratorService = playlistGeneratorService;
    }

    @Operation(summary = "Генерация плейлиста", description = "Генерирует плейлист на основе пользовательских предпочтений и запроса.")
    @PostMapping("/generate")
    public ResponseEntity<GeneratePlaylistResponse> generatePlaylist(
            @RequestBody GeneratePlaylistRequest request) {
        GeneratePlaylistResponse response = playlistGeneratorService.generatePlaylist(request);
        return ResponseEntity.ok(response);
    }
}