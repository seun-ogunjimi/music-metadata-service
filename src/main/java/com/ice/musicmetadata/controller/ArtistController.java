package com.ice.musicmetadata.controller;

import com.ice.musicmetadata.api.ArtistApi;
import com.ice.musicmetadata.model.*;
import com.ice.musicmetadata.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/v1"})
public class ArtistController implements ArtistApi {

    private final ArtistService artistService;

    @Override
    public ResponseEntity<TrackResponse> addTrack(UUID artistId, TrackRequest trackRequest) throws Exception {
        var trackResponse = artistService.addTrack(artistId, trackRequest);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path(String.format("/%s", trackResponse.getTrackId()))
                .build().toUri()).body(trackResponse);
    }

    @Override
    public ResponseEntity<ArtistResponse> getArtistOfTheDay() throws Exception {
        var artistResponse = artistService.getArtistOfTheDay();
        return ResponseEntity.ok(artistResponse);
    }

    @Override
    public ResponseEntity<TrackPageResponse> getArtistTracks(UUID artistId, Integer page, Integer size) {
        var trackPageResponse = artistService.getArtistTracks(artistId, page, size);
        return ResponseEntity.ok(trackPageResponse);
    }

    @Override
    public ResponseEntity<ArtistResponse> updateArtistName(UUID artistId, UpdateArtistNameRequest updateArtistNameRequest) {
        var artistResponse = artistService.updateArtistName(artistId, updateArtistNameRequest);
        return ResponseEntity.ok(artistResponse);
    }
}
