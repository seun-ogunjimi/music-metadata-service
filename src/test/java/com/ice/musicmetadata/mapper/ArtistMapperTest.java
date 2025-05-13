package com.ice.musicmetadata.mapper;

import com.ice.musicmetadata.domain.ArtistAlias;
import com.ice.musicmetadata.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link ArtistMapper}.
 * <p>
 * This class contains unit tests for the methods in the {@link ArtistMapper} interface.
 * It uses MapStruct to generate the implementation of the mapper.
 */
class ArtistMapperTest {

    private final ArtistMapper artistMapper = Mappers.getMapper(ArtistMapper.class);

    @DisplayName("Given Artist When mapToArtistResponse is called Then return ArtistResponse")
    @Test
    void given_artist_when_mapToArtistResponse_then_artistResponse() {
        // Given
        var artist = TestUtils.createArtist();
        // When
        var artistResponse = artistMapper.mapToArtistResponse(artist);
        // Then
        assertThat(artistResponse).isNotNull();
        assertThat(artist.getName()).isEqualTo(artistResponse.getName());
        assertThat(artist.getArtistId()).isEqualTo(artistResponse.getArtistId());
        assertThat(artist.getBio()).isEqualTo(artistResponse.getBio());
        assertThat(artist.getAliases().stream().map(ArtistAlias::getAlias)).containsAll(artistResponse.getAliases());
    }

    @DisplayName("Given ArtistAlias When mapToArtistAliasName is called Then return String alias name")
    @Test
    void mapToArtistAliasName() {
        // Given
        var artistAlias = ArtistAlias.builder().alias("Test Alias").build();
        // When
        var aliasName = artistMapper.mapToArtistAliasName(artistAlias);
        // Then
        assertThat(aliasName).isNotNull()
                .isNotEmpty()
                .isEqualTo(artistAlias.getAlias());
    }
}