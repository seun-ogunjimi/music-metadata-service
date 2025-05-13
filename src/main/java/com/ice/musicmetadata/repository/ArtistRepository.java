package com.ice.musicmetadata.repository;

import com.ice.musicmetadata.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByArtistId(UUID artistId);

    boolean existsByArtistId(UUID artistId);

    boolean existsByArtistIdNotAndName(UUID artistId, String name);

    @Query("SELECT a FROM Artist a " +
           "WHERE a.featuredAt IS NULL OR " +
           "a.featuredAt < CURRENT_DATE " +
           "ORDER BY a.featuredAt ASC NULLS FIRST, a.id ASC " +
           "LIMIT 1")
    Optional<Artist> findNextFeaturedArtist();

    @Query("SELECT a FROM Artist a " +
           "LEFT JOIN FETCH a.aliases aa " +
           "WHERE aa.artist.id = a.id " +
           "ORDER BY a.featuredAt DESC NULLS LAST, a.id ASC " +
           "LIMIT 1")
    Optional<Artist> findTopByOrderByFeaturedAtDesc();
}
