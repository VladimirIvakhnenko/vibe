package org.playlists.controller;

import org.junit.jupiter.api.Test;
import org.playlists.dto.GeneratePlaylistRequest;
import org.playlists.dto.GeneratePlaylistResponse;
import org.springframework.http.ResponseEntity;
import org.playlists.services.PlaylistGeneratorService;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlaylistControllerTest {
    private final PlaylistGeneratorService mockService = mock(PlaylistGeneratorService.class);
    private final PlaylistController controller = new PlaylistController(mockService);

    @Test
    void generatePlaylist_returnsOkResponse() {
        GeneratePlaylistRequest request = new GeneratePlaylistRequest();
        when(mockService.generatePlaylist(request)).thenReturn(new GeneratePlaylistResponse(true, "Success", null));

        ResponseEntity<GeneratePlaylistResponse> response = controller.generatePlaylist(request);
        assertEquals(200, response.getStatusCodeValue());
    }
}