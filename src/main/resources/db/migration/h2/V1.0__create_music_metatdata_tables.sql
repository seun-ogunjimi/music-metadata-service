CREATE SCHEMA IF NOT EXISTS "MUSIC_METADATA";
SET SCHEMA "MUSIC_METADATA";

-- Create table artists
CREATE TABLE artists (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         artist_id UUID DEFAULT RANDOM_UUID() NOT NULL,
                         name VARCHAR(255) NOT NULL,
                         bio        TEXT,
                         featured_at TIMESTAMP,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         version BIGINT NOT NULL DEFAULT 0,
                         CONSTRAINT uk_artists_artist_id UNIQUE (artist_id)
);

-- Create table genres
CREATE TABLE genres (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) NOT NULL UNIQUE,
                        description TEXT,
                        parent_genre_id BIGINT,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT fk_parent_genre FOREIGN KEY (parent_genre_id) REFERENCES genres (id)
);


-- Create table tracks
CREATE TABLE tracks (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        track_id UUID DEFAULT RANDOM_UUID() NOT NULL,
                        title VARCHAR(255) NOT NULL,
                        artist_id BIGINT NOT NULL,
                        genre_id BIGINT,
                        genre VARCHAR(100),
                        duration INTEGER NOT NULL,
                        release_date DATE,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        version BIGINT NOT NULL DEFAULT 0,
                        CONSTRAINT uk_tracks_track_id UNIQUE (track_id),
                        CONSTRAINT fk_tracks_artist_id FOREIGN KEY (artist_id) REFERENCES artists(id),
                        CONSTRAINT fk_tracks_genre_id FOREIGN KEY (genre_id) REFERENCES genres (id)

);

-- Create artist_aliases table
CREATE TABLE artist_aliases (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                artist_id BIGINT NOT NULL,
                                alias VARCHAR(255) NOT NULL,
                                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                version BIGINT NOT NULL DEFAULT 0,
                                CONSTRAINT fk_artist_aliases_artist_id FOREIGN KEY (artist_id) REFERENCES artists(id),
                                CONSTRAINT uk_artist_aliases_artist_alias UNIQUE (artist_id, alias)
);

-- Create indexes for performance
CREATE INDEX idx_artists_artist_id ON artists(artist_id);
CREATE INDEX idx_tracks_track_id ON tracks(track_id);
CREATE INDEX idx_tracks_artist_id ON tracks(artist_id);
CREATE INDEX idx_artist_aliases_artist_id ON artist_aliases(artist_id);