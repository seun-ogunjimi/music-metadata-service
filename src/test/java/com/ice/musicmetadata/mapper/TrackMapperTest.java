package com.ice.musicmetadata.mapper;

import com.ice.musicmetadata.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link TrackMapper}.
 * <p>
 * This class contains unit tests for the methods in the {@link TrackMapper} interface.
 * It uses MapStruct to generate the implementation of the mapper.
 */
class TrackMapperTest {
    private final TrackMapper trackMapper = Mappers.getMapper(TrackMapper.class);

    @DisplayName("Given Paginated Tracks When mapToTrackPageResponse is called Then return TrackPageResponse")
    @Test
    void mapToTrackPageResponse() {
        // Given
        var artistId = TestUtils.MICHAEL_JACKSON_ARTIST_ID;
        var trackPage = TestUtils.createTrackPage(artistId, 0, 10);
        // When
        var trackPageResponse = trackMapper.mapToTrackPageResponse(trackPage);
        // Then
        assertThat(trackPageResponse).isNotNull();
        assertThat(trackPageResponse.getData()).isNotEmpty();
        assertThat(trackPageResponse.getPage()).isEqualTo(trackPage.getNumber());
        assertThat(trackPageResponse.getPageSize()).isEqualTo(trackPage.getSize());
        assertThat(trackPageResponse.getTotalPages()).isEqualTo(trackPage.getTotalPages());
        assertThat(trackPageResponse.getTotalItems()).isEqualTo((int) trackPage.getTotalElements());
        assertThat(trackPageResponse.getData()).zipSatisfy(
                trackPage.getContent(),
                (trackResponse, track) -> {
                    assertThat(trackResponse.getTrackId()).isEqualTo(track.getTrackId());
                    assertThat(trackResponse.getTitle()).isEqualTo(track.getTitle());
                    assertThat(trackResponse.getDurationInSeconds()).isEqualTo(track.getDuration());
                    assertThat(trackResponse.getReleaseDate()).isEqualTo(track.getReleaseDate());
                    assertThat(trackResponse.getGenre()).isEqualTo(track.getGenre() != null ? track.getGenre().getName() : track.getGenreName());
                }
        );
    }

    @DisplayName("Given Track When mapToTrackResponse is called Then return TrackResponse")
    @Test
    void mapToTrackResponse() {
        // Given
        var artistId = TestUtils.MICHAEL_JACKSON_ARTIST_ID;
        var track = TestUtils.createTrack(artistId);
        // When
        var trackResponse = trackMapper.mapToTrackResponse(track);
        // Then
        assertThat(trackResponse).isNotNull();
        assertThat(trackResponse.getTrackId()).isEqualTo(track.getTrackId());
        assertThat(trackResponse.getTitle()).isEqualTo(track.getTitle());
        assertThat(trackResponse.getDurationInSeconds()).isEqualTo(track.getDuration());
        assertThat(trackResponse.getReleaseDate()).isEqualTo(track.getReleaseDate());
        assertThat(trackResponse.getGenre()).isEqualTo(track.getGenre() != null ? track.getGenre().getName() : track.getGenreName());
    }

    @DisplayName("Given Track When mapToGenreName is called Then return Genre Name")
    @Test
    void mapToGenreName() {
        // Given
        var artistId = TestUtils.MICHAEL_JACKSON_ARTIST_ID;
        var track = TestUtils.createTrack(artistId);
        // When
        var genreName = trackMapper.mapToGenreName(track);
        // Then
        assertThat(genreName).isEqualTo(track.getGenre() != null ? track.getGenre().getName() : track.getGenreName());
    }
}