
-- Insert root genres
INSERT INTO genres (name, description) VALUES
                                           ('Pop', 'Popular music characterized by catchy melodies and mass appeal'),
                                           ('Rock', 'Originated as rock and roll in the 1950s, characterized by electric guitars and strong rhythms'),
                                           ('Hip Hop', 'Developed in the 1970s, featuring rhythmic speech over beats'),
                                           ('Electronic', 'Music created using electronic instruments and technology'),
                                           ('Jazz', 'Originating in African-American communities with improvisation and syncopation'),
                                           ('Classical', 'Art music rooted in Western traditions'),
                                           ('Country', 'Originated in Southern US, featuring folk and blues influences'),
                                           ('R&B', 'Rhythm and blues combining jazz, gospel, and blues influences'),
                                           ('Metal', 'Heavy metal and its subgenres characterized by amplified distortion'),
                                           ('Folk', 'Traditional and contemporary folk music');

-- Insert sub-genres with parent relationships
INSERT INTO genres (name, description, parent_genre_id) VALUES
                                                            ('K-Pop', 'Korean pop music', (SELECT id FROM genres WHERE name = 'Pop')),
                                                            ('Indie Pop', 'Independent pop music', (SELECT id FROM genres WHERE name = 'Pop')),
                                                            ('Pop Rock', 'Rock music with pop influences', (SELECT id FROM genres WHERE name = 'Rock')),
                                                            ('Alternative Rock', 'Rock music that differs from mainstream', (SELECT id FROM genres WHERE name = 'Rock')),
                                                            ('Trap', 'Subgenre of hip hop', (SELECT id FROM genres WHERE name = 'Hip Hop')),
                                                            ('Drill', 'Chicago-born hip hop subgenre', (SELECT id FROM genres WHERE name = 'Hip Hop')),
                                                            ('Techno', 'Electronic dance music', (SELECT id FROM genres WHERE name = 'Electronic')),
                                                            ('House', 'Electronic dance music with repetitive 4/4 beats', (SELECT id FROM genres WHERE name = 'Electronic')),
                                                            ('Bebop', 'Complex jazz style', (SELECT id FROM genres WHERE name = 'Jazz')),
                                                            ('Symphony', 'Extended musical composition', (SELECT id FROM genres WHERE name = 'Classical'));
