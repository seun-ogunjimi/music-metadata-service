package com.ice.musicmetadata.repository;

import com.ice.musicmetadata.utils.TestUtils;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit5.annotation.FlywayTestExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@FlywayTestExtension
@FlywayTest
class ArtistRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    @DisplayName("Given artistId When findByArtistId Then return Artist")
    @Test
    void findByArtistId() {
        // given
        var artistId = TestUtils.THE_BEATLES_ARTIST_ID;
        // when
        var artist = artistRepository.findByArtistId(artistId);
        // then
        assertThat(artist).isPresent();
        assertThat(artist.get().getId()).isNotNull();
        assertThat(artist.get().getName()).isNotNull();
        assertThat(artist.get().getArtistId()).isEqualTo(artistId);
    }

    @DisplayName("Given artistId and name When existsByArtistIdNotAndName Then return false")
    @Test
    void existsByArtistIdNotAndName() {
        // given
        var artistId = TestUtils.THE_BEATLES_ARTIST_ID;
        var name = TestUtils.THE_BEATLES_ARTIST_NAME;
        // when
        var artistExist = artistRepository.existsByArtistIdNotAndName(artistId, name);
        // then
        assertThat(artistExist).isFalse();
    }

    @DisplayName("Given artistId and existing name When existsByArtistIdNotAndName Then return true")
    @Test
    void existsByArtistIdNotAndName_true() {
        // given
        var artistId = TestUtils.THE_BEATLES_ARTIST_ID;
        var name = TestUtils.MADONNA_ARTIST_NAME;
        // when
        var artistExist = artistRepository.existsByArtistIdNotAndName(artistId, name);
        // then
        assertThat(artistExist).isTrue();
    }

    @DisplayName("Given artistId When existsByArtistId Then return true")
    @Test
    void existsByArtistId_true() {
        // given
        var artistId = TestUtils.THE_BEATLES_ARTIST_ID;
        // when
        var artistExist = artistRepository.existsByArtistId(artistId);
        // then
        assertThat(artistExist).isTrue();
    }

    @DisplayName("Given artistId When existsByArtistId Then return false")
    @Test
    void existsByArtistId_false() {
        // given
        var artistId = UUID.randomUUID();
        // when
        var artistExist = artistRepository.existsByArtistId(artistId);
        // then
        assertThat(artistExist).isFalse();
    }

    @DisplayName("Given nothing When findNextFeaturedArtist Then return eligible artist")
    @Test
    void findNextFeaturedArtist() {
        // given
        var artistId = TestUtils.THE_BEATLES_ARTIST_ID;

        // when
        var artist = artistRepository.findNextFeaturedArtist();

        // then
        assertThat(artist).isPresent();
        assertThat(artist.get().getId()).isNotNull();
        assertThat(artist.get().getName()).isNotNull();
        assertThat(artist.get().getArtistId()).isEqualTo(artistId);
    }

    @DisplayName("Given nothing When findTopByOrderByFeaturedAtDesc Then return first artist")
    @Test
    void findTopByOrderByFeaturedAtDesc() {
        // given
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        // when
        var artist = artistRepository.findTopByOrderByFeaturedAtDesc();
        // then
        assertThat(artist).isPresent();
        assertThat(artist.get().getId()).isNotNull();
        assertThat(artist.get().getName()).isNotNull();
        assertThat(artist.get().getArtistId()).isEqualTo(artistId);
    }

}