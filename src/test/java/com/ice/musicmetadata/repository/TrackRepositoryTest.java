package com.ice.musicmetadata.repository;

import com.ice.musicmetadata.utils.TestUtils;
import org.assertj.core.api.Assertions;
import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit5.annotation.FlywayTestExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@FlywayTestExtension
@FlywayTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TrackRepositoryTest {

    @Autowired
    private TrackRepository trackRepository;

    @DisplayName("Given artistId When findByArtistId Then return Track")
    @Test
    void findByArtistId() {
        // given
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        // when
        var trackPage = trackRepository.findByArtist_ArtistId(artistId, Pageable.ofSize(10));
        // then
        assertThat(trackPage.getTotalElements()).isPositive();
        assertThat(trackPage.getContent()).isNotEmpty();
        assertThat(trackPage.getContent())
                .allSatisfy(
                        track -> {
                            Assertions.assertThat(track.getId()).isNotNull();
                            Assertions.assertThat(track.getTrackId()).isNotNull();
                            Assertions.assertThat(track.getTitle()).isNotNull();
                        });
    }

    @DisplayName("Given artistId and title When existsByArtist_ArtistIdAndTitle Then return true")
    @Test
    void existsByArtist_ArtistIdAndTitle() {
        // given
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var trackTitle = TestUtils.MADONNA_TRACK_TITLE;
        // when
        var trackTitleAlreadyExist = trackRepository.existsByArtist_ArtistIdAndTitle(artistId, trackTitle);
        // then
        assertThat(trackTitleAlreadyExist).isTrue();
    }

    @DisplayName("Given artistId and title When existsByArtist_ArtistIdAndTitle Then return false")
    @Test
    void existsByArtist_ArtistIdAndTitle_notExist() {
        // given
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var trackTitle = TestUtils.MICHAEL_JACKSON_TRACK_TITLE;
        // when
        var trackTitleAlreadyExist = trackRepository.existsByArtist_ArtistIdAndTitle(artistId, trackTitle);
        // then
        assertThat(trackTitleAlreadyExist).isFalse();
    }
}