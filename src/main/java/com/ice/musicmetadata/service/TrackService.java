package com.ice.musicmetadata.service;

import com.ice.musicmetadata.domain.Artist;
import com.ice.musicmetadata.domain.Track;
import com.ice.musicmetadata.mapper.TrackMapper;
import com.ice.musicmetadata.model.TrackPageResponse;
import com.ice.musicmetadata.model.TrackRequest;
import com.ice.musicmetadata.model.TrackResponse;
import com.ice.musicmetadata.repository.GenreRepository;
import com.ice.musicmetadata.repository.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TrackService {
    private final TrackRepository trackRepository;
    private final GenreRepository genreRepository;
    private final TrackMapper trackMapper;

    /**
     * Retrieves a paginated list of tracks for a given artist.
     *
     * @param artist The artist to retrieve tracks for.
     * @param page   The page number to retrieve.
     * @param size   The number of tracks per page.
     * @return A paginated response containing the tracks for the artist.
     * @throws EntityNotFoundException if the artist is not found.
     */
    public TrackPageResponse getArtistTracks(Artist artist, Integer page, Integer size) {
        var trackPage = trackRepository.findByArtist_ArtistId(artist.getArtistId(), PageRequest.of(page, size));
        return trackMapper.mapToTrackPageResponse(trackPage);
    }


    /**
     * Adds a new track to an artist.
     *
     * @param artist       The artist to retrieve tracks for.
     * @param trackRequest The request containing the track details.
     * @return The added track response.
     * @throws IllegalArgumentException if the title, duration, or release date is invalid, or if the title already exists for the artist.
     */
    public TrackResponse addTrack(Artist artist, TrackRequest trackRequest) throws IllegalArgumentException {
        // Validate the request
        if (trackRequest.getTitle() == null || trackRequest.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Track title cannot be null or empty");
        }
        if (trackRequest.getDurationInSeconds() <= 0) {
            throw new IllegalArgumentException("Track duration must be greater than 0");
        }
        if (trackRequest.getReleaseDate() == null) {
            throw new IllegalArgumentException("Track release date cannot be null");
        }
        if (trackRepository.existsByArtist_ArtistIdAndTitle(artist.getArtistId(), trackRequest.getTitle())) {
            throw new IllegalArgumentException("Track title already exists for this artist");
        }

        var track = Track.builder().artist(artist)
                .title(trackRequest.getTitle())
                .duration(trackRequest.getDurationInSeconds())
                .releaseDate(trackRequest.getReleaseDate())
                .build();
        if (Objects.nonNull(trackRequest.getGenre()) && !trackRequest.getGenre().isBlank()) {
            genreRepository.findByName(trackRequest.getGenre())
                    .ifPresentOrElse(
                            track::setGenre,
                            () -> track.setGenreName(trackRequest.getGenre())
                    );
        }
        var savedTrack = trackRepository.save(track);
        return trackMapper.mapToTrackResponse(savedTrack);
    }
}
