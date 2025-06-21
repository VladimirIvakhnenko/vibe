package org.playlists.controller;

import org.playlists.dto.GeneratePlaylistRequest;
import org.playlists.dto.GeneratePlaylistResponse;
import org.playlists.services.PlaylistGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    private final PlaylistGeneratorService playlistGeneratorService;

    public PlaylistController(PlaylistGeneratorService playlistGeneratorService) {
        this.playlistGeneratorService = playlistGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<GeneratePlaylistResponse> generatePlaylist(
            @RequestBody GeneratePlaylistRequest request) {
        return ResponseEntity.ok(
                playlistGeneratorService.generatePlaylist(request)
        );
    }
}