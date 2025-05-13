package com.ice.musicmetadata.repository;

import org.flywaydb.test.annotation.FlywayTest;
import org.flywaydb.test.junit5.annotation.FlywayTestExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@FlywayTestExtension
@FlywayTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @DisplayName("Given genreId When findByGenreId Then return Genre")
    @Test
    void findByName() {
        // given
        var genreName = "Pop";
        // when
        var genre = genreRepository.findByName(genreName);
        // then
        assertTrue(genre.isPresent());
        assertEquals(genreName, genre.get().getName());
    }
}