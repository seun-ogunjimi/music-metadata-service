package com.ice.musicmetadata.service;

import com.ice.musicmetadata.domain.Artist_;
import com.ice.musicmetadata.repository.ArtistRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@SpringBootTest(
        properties = {
                "app.artist-of-the-day.cron=*/4 * * * * ?",
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class FeaturedArtistServiceIntegrationTest {
    @MockitoSpyBean
    private FeaturedArtistService featuredArtistService;
    @Autowired
    private ArtistRepository artistRepository;

    @Test
    void rotateArtistOfTheDay() {
        var currentArtistOfTheDay = featuredArtistService.getArtistOfTheDay();
        // Wait for the scheduled task to run
        Awaitility.await()
                .atMost(Duration.of(4200, ChronoUnit.MILLIS))
                .pollDelay(Duration.of(2000, ChronoUnit.MILLIS))
                .untilAsserted(() -> {
                            var rotatedArtistOfTheDay = featuredArtistService.getArtistOfTheDay();
                            assertThat(rotatedArtistOfTheDay).isNotEqualTo(currentArtistOfTheDay);
                            verify(featuredArtistService, times(1)).rotateArtistOfTheDay();
                        }
                );
    }

    @DisplayName("Should practice fairness in rotating artist of the day")
    @Test
    void rotateArtistOfTheDay_fairness() {
        var artistList = artistRepository.findAll(Sort.by(Sort.Direction.ASC, Artist_.ID));
        for (int i = 0; i < artistList.size(); i++) {
            if (i == artistList.size() - 1) {
                artistList.get(i).setFeaturedAt(null);
            } else {
                artistList.get(i).setFeaturedAt(Instant.now().minus(artistList.size() - i, ChronoUnit.DAYS));
            }
        }
        artistRepository.saveAll(artistList);
        artistRepository.flush();

        var currentArtistOfTheDay = featuredArtistService.getArtistOfTheDay();
        // Verify that the current artist of the day is the one with the most current FeaturedAt Date (second to last artist)
        assertThat(currentArtistOfTheDay).isEqualTo(artistList.get(artistList.size() - 2));
        // Wait for the scheduled task to run and move to the last artist
        Awaitility.await()
                .atMost(Duration.of(4200, ChronoUnit.MILLIS))
                .pollDelay(Duration.of(2000, ChronoUnit.MILLIS))
                .untilAsserted(() -> {
                            var rotatedArtistOfTheDay = featuredArtistService.getArtistOfTheDay();
                            assertThat(rotatedArtistOfTheDay).isEqualTo(artistList.getLast());
                            verify(featuredArtistService, times(1)).rotateArtistOfTheDay();
                        }
                );
        // Wait for the scheduled task to run and move to the first artist
        Awaitility.await()
                .atMost(Duration.of(4200, ChronoUnit.MILLIS))
                .pollDelay(Duration.of(2000, ChronoUnit.MILLIS))
                .untilAsserted(() -> {
                            var rotatedArtistOfTheDay = featuredArtistService.getArtistOfTheDay();
                            assertThat(rotatedArtistOfTheDay).isEqualTo(artistList.getFirst());
                            verify(featuredArtistService, times(2)).rotateArtistOfTheDay();
                        }
                );
    }
}
