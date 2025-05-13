CREATE SCHEMA IF NOT EXISTS music_metadata;
-- Set the search path to the new schema
SET
search_path TO music_metadata;
-- Enable UUID extension if not already enabled
CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create table artists
CREATE TABLE artists
(
    id         BIGSERIAL PRIMARY KEY,
    artist_id  UUID                     NOT NULL DEFAULT uuid_generate_v4(),
    name       VARCHAR(255)             NOT NULL,
    bio        TEXT,
    featured_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version    BIGINT                   NOT NULL DEFAULT 0,
    CONSTRAINT uk_artists_artist_id UNIQUE (artist_id)
);

-- Create table genres
CREATE TABLE genres
(
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(50)              NOT NULL UNIQUE,
    description     TEXT,
    parent_genre_id BIGINT,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_parent_genre FOREIGN KEY (parent_genre_id) REFERENCES genres (id)
);

-- Create table tracks
CREATE TABLE tracks
(
    id           BIGSERIAL PRIMARY KEY,
    track_id     UUID                     NOT NULL DEFAULT uuid_generate_v4(),
    title        VARCHAR(255)             NOT NULL,
    artist_id    BIGINT                   NOT NULL,

    genre_id     BIGINT,
    genre        VARCHAR(100),
    duration     INTEGER                  NOT NULL,
    release_date DATE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_tracks_track_id UNIQUE (track_id),
    CONSTRAINT fk_tracks_artist_id FOREIGN KEY (artist_id) REFERENCES artists (id),
    CONSTRAINT fk_tracks_genre_id FOREIGN KEY (genre_id) REFERENCES genres (id)
);

-- Create artist_aliases table
CREATE TABLE artist_aliases
(
    id         BIGSERIAL PRIMARY KEY,
    artist_id  BIGINT                   NOT NULL,
    alias      VARCHAR(255)             NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_artist_aliases_artist_id FOREIGN KEY (artist_id) REFERENCES artists (id),
    CONSTRAINT uk_artist_aliases_artist_alias UNIQUE (artist_id, alias)
);

-- Create indexes for performance
CREATE INDEX idx_artists_artist_id ON artists (artist_id);
CREATE INDEX idx_tracks_track_id ON tracks (track_id);
CREATE INDEX idx_tracks_artist_id ON tracks (artist_id);
CREATE INDEX idx_artist_aliases_artist_id ON artist_aliases (artist_id);