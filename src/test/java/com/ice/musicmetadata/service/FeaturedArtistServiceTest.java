package com.ice.musicmetadata.service;

import com.ice.musicmetadata.domain.Artist;
import com.ice.musicmetadata.exception.NoEligibleArtistsException;
import com.ice.musicmetadata.repository.ArtistRepository;
import com.ice.musicmetadata.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeaturedArtistServiceTest {
    @InjectMocks
    private FeaturedArtistService featuredArtistService;

    @Mock
    private ArtistRepository artistRepository;

    @Spy
    private CacheManager cacheManager = new ConcurrentMapCacheManager();

    @Test
    @DisplayName("Should successfully rotate artist of the day")
    void rotateArtistOfTheDay_Success() {
        // Arrange
        var artist = TestUtils.createArtist(TestUtils.MADONNA_ARTIST_ID);
        when(artistRepository.findNextFeaturedArtist()).thenReturn(Optional.of(artist));
        when(artistRepository.save(any(Artist.class))).thenAnswer(invocation -> {
            Artist savedArtist = invocation.getArgument(0);
            assertThat(savedArtist.getFeaturedAt()).isNotNull(); // Ensure featuredAt was set
            return savedArtist;
        });
        //when(cacheManager.getCache("featured-artist")).thenReturn(new ConcurrentMapCacheManager().getCache("featured-artist"));

        // Act
        var artistOfTheDay = featuredArtistService.rotateArtistOfTheDay();

        // Assert
        assertThat(artistOfTheDay).isNotNull();
        assertThat(artistOfTheDay.getArtistId()).isEqualTo(TestUtils.MADONNA_ARTIST_ID);
        assertThat(artistOfTheDay.getFeaturedAt()).isNotNull();

        // Verify
        verify(artistRepository).findNextFeaturedArtist();
        verify(artistRepository).save(any(Artist.class));
        //verify(cacheManager).getCache("featured-artist");

        // Verify cache was cleared
        var cache = cacheManager.getCache("featured-artist");
        assertThat(cache).isNotNull();
        assertThat(cache.get("'artist-of-the-day'")).isNull();
    }

    @Test
    @DisplayName("Should throw NoEligibleArtistsException when no eligible artists found")
    void rotateArtistOfTheDay_NoEligibleArtists() {
        // Arrange
        when(artistRepository.findNextFeaturedArtist()).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> featuredArtistService.rotateArtistOfTheDay())
                .isInstanceOf(NoEligibleArtistsException.class)
                .hasMessageContaining("No eligible artists found for rotation");

        // Verify
        verify(artistRepository).findNextFeaturedArtist();
        verify(artistRepository, never()).save(any(Artist.class));
    }


 /*   @Test
    @DisplayName("Should update cache with new artist of the day")
    void rotateArtistOfTheDay_UpdatesCache() {
        // Arrange
        var cache = cacheManager.getCache("featured-artist");
        var artist = TestUtils.createArtist(TestUtils.MADONNA_ARTIST_ID);
        when(artistRepository.findNextFeaturedArtist()).thenReturn(Optional.of(artist));
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);
        when(artistRepository.findTopByOrderByFeaturedAtDesc()).thenAnswer(
                invocation -> Optional.ofNullable(cache.get("artist-of-the-day"))
        );

        // Pre-populate cache with a different artist

        assertThat(cache).isNotNull();
        var differentArtist = TestUtils.createArtist(TestUtils.MICHAEL_JACKSON_ARTIST_ID);
        cache.clear();
        cache.put("'artist-of-the-day'", differentArtist);

        // Act
        var result = featuredArtistService.rotateArtistOfTheDay();

        // Fetch from cache to verify it was updated
        var cachedArtist = featuredArtistService.getArtistOfTheDay();
        System.out.println("========");
        System.out.println(cache.get("'artist-of-the-day'"));
        // Assert
        assertThat(result).isNotNull();
        assertThat(cachedArtist).isNotNull();
        assertThat(cachedArtist.getArtistId()).isEqualTo(TestUtils.MADONNA_ARTIST_ID);
        assertThat(cachedArtist).isEqualTo(result);

        // Verify
        verify(artistRepository).findNextFeaturedArtist();
        verify(artistRepository).save(any(Artist.class));
    }*/


    @DisplayName("Should get artist of the day when artist exists")
    @Test
    void getArtistOfTheDay_ReturnsArtist_WhenArtistExists() {
        // Arrange
        var artist = TestUtils.createArtist(TestUtils.MADONNA_ARTIST_ID);
        when(artistRepository.findTopByOrderByFeaturedAtDesc()).thenReturn(Optional.of(artist));

        // Act
        var artistOfTheDay = featuredArtistService.getArtistOfTheDay();

        // Assert
        assertThat(artistOfTheDay).isNotNull();
        assertThat(artistOfTheDay.getArtistId()).isEqualTo(TestUtils.MADONNA_ARTIST_ID);
        assertThat(artistOfTheDay.getName()).isEqualTo(artist.getName());

        // Verify
        verify(artistRepository, times(1)).findTopByOrderByFeaturedAtDesc();
        verifyNoMoreInteractions(artistRepository);
    }

    @DisplayName("Should return null when no artist is found")
    @Test
    void getArtistOfTheDay_ReturnsNull_WhenNoArtistFound() {
        // Arrange
        when(artistRepository.findTopByOrderByFeaturedAtDesc()).thenReturn(Optional.empty());

        // Act
        var artistOfTheDay = featuredArtistService.getArtistOfTheDay();

        // Assert
        assertThat(artistOfTheDay).isNull();

        // Verify
        verify(artistRepository, times(1)).findTopByOrderByFeaturedAtDesc();
    }

    //@Test
    void getArtistOfTheDay_UsesCache_WhenCalledMultipleTimes() {
        // Arrange
        var artist = TestUtils.createArtist(TestUtils.MADONNA_ARTIST_ID);
        when(artistRepository.findTopByOrderByFeaturedAtDesc()).thenReturn(Optional.of(artist));

        // Act - First call should hit the repository
        Artist firstResult = featuredArtistService.getArtistOfTheDay();

        // Second call should use the cache
        Artist secondResult = featuredArtistService.getArtistOfTheDay();

        // Assert
        assertThat(firstResult).isNotNull();
        assertThat(secondResult).isNotNull();
        assertThat(firstResult).isEqualTo(secondResult);

        // Verify repository is only called once despite two method calls
        verify(artistRepository, times(1)).findTopByOrderByFeaturedAtDesc();
    }
}