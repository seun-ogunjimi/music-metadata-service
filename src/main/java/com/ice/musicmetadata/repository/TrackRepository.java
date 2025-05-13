package com.ice.musicmetadata.repository;

import com.ice.musicmetadata.domain.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackRepository extends JpaRepository<Track, Long> {

    Page<Track> findByArtist_ArtistId(UUID artistId, Pageable pageable);

    boolean existsByArtist_ArtistIdAndTitle(UUID artistId, String title);
}