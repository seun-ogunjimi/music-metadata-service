package com.ice.musicmetadata.service;

import com.ice.musicmetadata.domain.Artist;
import com.ice.musicmetadata.exception.NoEligibleArtistsException;
import com.ice.musicmetadata.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class FeaturedArtistService {
    private static final String ARTIST_OF_THE_DAY_CACHE_NAME = "featured-artist";
    private static final String ARTIST_OF_THE_DAY_CACHE_KEY = "'artist-of-the-day'";
    private final ArtistRepository artistRepository;
    private final CacheManager cacheManager;

    /**
     * Rotates the artist of the day by selecting a new artist and updating the cache.
     * This method is scheduled to run daily at midnight.
     *
     * @return The new artist of the day.
     */
    @Scheduled(cron = "${app.artist-of-the-day.cron}") // Runs daily at midnight
    @Transactional
    @CachePut(value = ARTIST_OF_THE_DAY_CACHE_NAME, key = ARTIST_OF_THE_DAY_CACHE_KEY)
    public Artist rotateArtistOfTheDay() {
        // Clear the previous day's cache
        var cache = cacheManager.getCache(ARTIST_OF_THE_DAY_CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }

        // Find the least recently featured artist
        Artist artist = artistRepository.findNextFeaturedArtist()
                .orElseThrow(() -> new NoEligibleArtistsException("No eligible artists found for rotation"));

        // Update artist's featured info
        artist.setFeaturedAt(Instant.now());
        return artistRepository.save(artist);
    }


    /**
     * Retrieves the artist of the day from the cache or database.
     *
     * @return The artist of the day, or null if not found.
     */
    @Cacheable(value = ARTIST_OF_THE_DAY_CACHE_NAME, key = ARTIST_OF_THE_DAY_CACHE_KEY, unless = "#result == null")
    public Artist getArtistOfTheDay() {
        return artistRepository.findTopByOrderByFeaturedAtDesc().orElse(null);
    }
}