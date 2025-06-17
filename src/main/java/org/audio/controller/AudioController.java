package org.audio.controller;

import org.audio.dto.MatchResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/audio")
public class AudioController {
    @PostMapping(path = "/top-similar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String topSimilarTracks(@RequestParam("audioFile") MultipartFile file) {

        return "Matched!";
    }
}