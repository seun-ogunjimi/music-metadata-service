package com.ice.musicmetadata.controller;

import com.ice.musicmetadata.model.UpdateArtistNameRequest;
import com.ice.musicmetadata.utils.TestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.matchesPattern;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ArtistControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @DisplayName("Add Track - Success Case")
    @Test
    void addTrack_Success() {
        // Arrange
        var artistId = TestUtils.MICHAEL_JACKSON_ARTIST_ID;
        var trackRequest = TestUtils.createTrackRequest();

        // Act
        webTestClient.post()
                .uri("/v1/artists/{artistId}/tracks", artistId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(trackRequest))
                //.bodyValue(objectMapper.writeValueAsBytes(trackRequest))
                .exchange()
                // Assert
                .expectStatus().isCreated()
                .expectHeader().value("Location", matchesPattern(".*/v1/artists/" + artistId + "/tracks/.*"))
                .expectBody()
                .jsonPath("$.title").isEqualTo(trackRequest.getTitle())
                .jsonPath("$.durationInSeconds").isEqualTo(trackRequest.getDurationInSeconds())
                .jsonPath("$.trackId").value(matchesPattern(TestUtils.UUID_REGEX_PATTERN))
                .jsonPath("$.releaseDate").isEqualTo(trackRequest.getReleaseDate() != null ? trackRequest.getReleaseDate().toString() : "");

    }

    @DisplayName("Add Track - Artist Not Found")
    @Test
    void addTrack_ArtistNotFound() {
        // Arrange
        var artistId = UUID.randomUUID();
        var trackRequest = TestUtils.createTrackRequest();

        // Act
        webTestClient.post()
                .uri("/v1/artists/{artistId}/tracks", artistId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(trackRequest))
                .exchange()
                // Assert
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Artist not found");
    }


    @DisplayName("Add Track - Bad Request when Track Title Empty")
    @Test
    void addTrack_BadRequest_EmptyTitle() {
        // Arrange
        var artistId = TestUtils.MICHAEL_JACKSON_ARTIST_ID;
        var trackRequest = TestUtils.createTrackRequest();
        trackRequest.setTitle("");

        // Act
        webTestClient.post()
                .uri("/v1/artists/{artistId}/tracks", artistId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(trackRequest))
                .exchange()
                // Assert
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }


    @DisplayName("Get Artist Tracks - Success Case with Pagination")
    @Test
    void getArtistTracks_Success_Pagination() {
        // Arrange
        var artistId = TestUtils.MICHAEL_JACKSON_ARTIST_ID;
        var page = 0;
        var size = 5;

        // Act
        webTestClient.get()
                .uri("/v1/artists/{artistId}/tracks?page={page}&size={size}", artistId, page, size)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Assert
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.page").isEqualTo(page)
                .jsonPath("$.pageSize").isEqualTo(size)
                .jsonPath("$.totalItems").isNumber()
                .jsonPath("$.totalPages").isNumber()
                .jsonPath("$.data").isArray()
                .jsonPath("$.data[*].title").isNotEmpty();
    }

    @DisplayName("Get Artist Tracks - Not Found when Artist Service Throws EntityNotFoundException")
    @Test
    void getArtistTracks_NotFound_ArtistNotFound() {
        // Arrange
        var artistId = UUID.randomUUID();
        var page = 0;
        var size = 5;

        // Act
        webTestClient.get()
                .uri("/v1/artists/{artistId}/tracks?page={page}&size={size}", artistId, page, size)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Assert
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Artist not found");
    }

    @DisplayName("Update Artist Name - Success Case")
    @Test
    void updateArtistName_Success() {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var newName = "Madonna (Official)";
        var aliases = List.of("Material Girl", "Queen of Pop");
        var updateArtistNameRequest = new UpdateArtistNameRequest()
                .name(newName)
                .aliases(aliases);

        // Act
        webTestClient.patch()
                .uri("/v1/artists/{artistId}/name", artistId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updateArtistNameRequest))
                .exchange()
                // Assert
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(updateArtistNameRequest.getName())
                .jsonPath("$.aliases").isEqualTo(updateArtistNameRequest.getAliases());
    }

    @DisplayName("Update Artist Name - Bad Request when Name Empty")
    @Test
    void updateArtistName_BadRequest_EmptyName() {
        // Arrange
        var artistId = TestUtils.MICHAEL_JACKSON_ARTIST_ID;
        var updateArtistNameRequest = new UpdateArtistNameRequest()
                .name("")
                .aliases(List.of("Alias1", "Alias2"));

        // Act
        webTestClient.patch()
                .uri("/v1/artists/{artistId}/name", artistId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(updateArtistNameRequest))
                .exchange()
                // Assert
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isNotEmpty();
    }

    @DisplayName("Get Artist of the Day - Success Case")
    @Test
    void getArtistOfTheDay_Success() {
        // Arrange
        // Act
        webTestClient.get()
                .uri("/v1/artists/artist-of-the-day")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                // Assert
                .expectStatus().isOk()
                .expectBody()
                //.jsonPath("$.artistId").isEqualTo(artistId.toString())
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.aliases").isArray();
    }
}
