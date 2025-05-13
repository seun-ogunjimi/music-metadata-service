package com.ice.musicmetadata.service;

import com.ice.musicmetadata.domain.Artist;
import com.ice.musicmetadata.domain.Track;
import com.ice.musicmetadata.exception.NoEligibleArtistsException;
import com.ice.musicmetadata.mapper.ArtistMapper;
import com.ice.musicmetadata.mapper.TrackMapper;
import com.ice.musicmetadata.model.*;
import com.ice.musicmetadata.repository.ArtistRepository;
import com.ice.musicmetadata.repository.GenreRepository;
import com.ice.musicmetadata.repository.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistService {
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final GenreRepository genreRepository;
    private final TrackMapper trackMapper;
    private final ArtistMapper artistMapper;
    private final FeaturedArtistService featuredArtistService;

    /**
     * Retrieves the artist of the day.
     *
     * @return The artist of the day response.
     * @throws NoEligibleArtistsException if no artist of the day is found.
     */
    public ArtistResponse getArtistOfTheDay() throws NoEligibleArtistsException {
        var artist = featuredArtistService.getArtistOfTheDay();
        if (artist == null) {
            throw new NoEligibleArtistsException("No artist of the day found");
        }
        return artistMapper.mapToArtistResponse(artist);
    }

    /**
     * Retrieves a paginated list of tracks for a given artist.
     *
     * @param artistId The ID of the artist to retrieve tracks for.
     * @param page     The page number to retrieve.
     * @param size     The number of tracks per page.
     * @return A paginated response containing the tracks for the artist.
     * @throws EntityNotFoundException if the artist is not found.
     */
    public TrackPageResponse getArtistTracks(UUID artistId, Integer page, Integer size) {
        if (!artistRepository.existsByArtistId(artistId)) {
            throw new EntityNotFoundException("Artist not found");
        }
        var trackPage = trackRepository.findByArtist_ArtistId(artistId, PageRequest.of(page, size));
        return trackMapper.mapToTrackPageResponse(trackPage);
    }

    /**
     * Updates the name of an artist and their aliases.
     *
     * @param artistId                The ID of the artist to update.
     * @param updateArtistNameRequest The request containing the new name and aliases.
     * @return The updated artist response.
     * @throws IllegalArgumentException if the name is null or empty, or if the name already exists for another artist.
     */
    public ArtistResponse updateArtistName(UUID artistId, UpdateArtistNameRequest updateArtistNameRequest) throws IllegalArgumentException {
        var artist = findByArtistId(artistId);
        // Validate the request //updateArtistNameRequest.getName IS MANDATORY
        if (updateArtistNameRequest.getName().isEmpty()) {
            throw new IllegalArgumentException("Artist name cannot be null or empty");
        }
        if (artistRepository.existsByArtistIdNotAndName(artistId, updateArtistNameRequest.getName())) {
            throw new IllegalArgumentException("Artist name already exists");
        }
        // Update the artist name and aliases
        artist.setName(updateArtistNameRequest.getName());
        artist.setAliases(new ArrayList<>());
        updateArtistNameRequest.getAliases().forEach(artist::addAlias);
        artist = artistRepository.save(artist);
        // Map the updated artist to the response model
        return artistMapper.mapToArtistResponse(artist);
    }

    /**
     * Adds a new track to an artist.
     *
     * @param artistId     The ID of the artist to add the track to.
     * @param trackRequest The request containing the track details.
     * @return The added track response.
     * @throws IllegalArgumentException if the title, duration, or release date is invalid, or if the title already exists for the artist.
     */
    public TrackResponse addTrack(UUID artistId, TrackRequest trackRequest) throws IllegalArgumentException {
        var artist = findByArtistId(artistId);
        // Validate the request
        if (trackRequest.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Track title cannot be null or empty");
        }
        if (trackRequest.getDurationInSeconds() <= 0) {
            throw new IllegalArgumentException("Track duration must be greater than 0");
        }
        if (trackRequest.getReleaseDate() == null) {
            throw new IllegalArgumentException("Track release date cannot be null");
        }
        if (trackRepository.existsByArtist_ArtistIdAndTitle(artistId, trackRequest.getTitle())) {
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

    private Artist findByArtistId(UUID artistId) {
        return artistRepository.findByArtistId(artistId)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found"));
    }
}
