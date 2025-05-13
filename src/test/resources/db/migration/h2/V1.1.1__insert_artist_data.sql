CREATE SCHEMA IF NOT EXISTS "MUSIC_METADATA";
SET SCHEMA "MUSIC_METADATA";

INSERT INTO artists (artist_id, name, bio, featured_at, created_at, updated_at, version)
VALUES
    (UUID'123e4567-e89b-12d3-a456-426614174000', 'The Beatles', 'Iconic British rock band formed in Liverpool in 1960.', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174001', 'Elvis Presley', 'American singer and actor, known as the King of Rock and Roll.', DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174002', 'Madonna', 'American singer-songwriter and actress, known as the Queen of Pop.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174003', 'Michael Jackson', 'American singer, songwriter, and dancer, known as the King of Pop.', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174004', 'Beyoncé', 'American singer-songwriter and actress, known for her powerful voice and stage presence.', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO artist_aliases (artist_id, alias, created_at, version)

VALUES
    (1, 'The Fab Four', CURRENT_TIMESTAMP, 0),
    (2, 'The King', CURRENT_TIMESTAMP, 0),
    (3, 'Madge', CURRENT_TIMESTAMP, 0),
    (4, 'MJ', CURRENT_TIMESTAMP, 0),
    (5, 'Queen Bey', CURRENT_TIMESTAMP, 0);

-- (SELECT id FROM artists WHERE name = 'The Beatles')
-- (SELECT id FROM artists WHERE name = 'Elvis Presley')
-- (SELECT id FROM artists WHERE name = 'Madonna')
-- (SELECT id FROM artists WHERE name = 'Michael Jackson')
-- (SELECT id FROM artists WHERE name = 'Beyoncé')
INSERT INTO tracks (track_id, title, artist_id, genre_id, genre, duration, release_date, created_at, updated_at, version)
VALUES
    (UUID'123e4567-e89b-12d3-a456-426614174005', 'Hey Jude', 1, NULL, 'Rock', 431, DATE'1968-08-26', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174006', 'Jailhouse Rock', 2, NULL, 'Rock', 195, DATE'1957-09-24', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174007', 'Like a Prayer', 3, NULL, 'Pop', 295, DATE'1989-03-21', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174008', 'Billie Jean', 4, NULL, 'Pop', 294, DATE'1982-01-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174009', 'Crazy in Love', 5, NULL, 'R&B', 238, DATE'2003-05-18', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174010', 'Frozen', 3, NULL, 'Pop', 300, DATE'1998-11-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174011', 'Beat It', 4, NULL, 'Pop', 258, DATE'1982-02-02', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174012', 'Single Ladies', 5, NULL, 'R&B', 235, DATE'2008-10-13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174013', 'Uptown Funk', 5, NULL, 'Funk', 269, DATE'2014-11-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174014', 'Let It Be', 1, NULL, 'Rock', 243, DATE'1970-03-06', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (UUID'123e4567-e89b-12d3-a456-426614174015', 'Hound Dog', 2, NULL, 'Rock', 122, DATE'1956-07-13', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
