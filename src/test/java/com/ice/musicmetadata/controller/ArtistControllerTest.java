package com.ice.musicmetadata.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ice.musicmetadata.model.ArtistResponse;
import com.ice.musicmetadata.model.TrackResponse;
import com.ice.musicmetadata.model.UpdateArtistNameRequest;
import com.ice.musicmetadata.service.ArtistService;
import com.ice.musicmetadata.utils.TestUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtistController.class)
class ArtistControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArtistService artistService;


    @DisplayName("Add Track - Success Case")
    @Test
    void addTrack_Success() throws Exception {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var trackRequest = TestUtils.createTrackRequest();
        var trackResponse = new TrackResponse()
                .trackId(UUID.randomUUID())
                .title(trackRequest.getTitle())
                .durationInSeconds(trackRequest.getDurationInSeconds())
                .releaseDate(trackRequest.getReleaseDate())
                .genre(trackRequest.getGenre());

        when(artistService.addTrack(artistId, trackRequest)).thenReturn(trackResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/artists/{artistId}/tracks", artistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(trackRequest)))
                //.with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                //Assert
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trackId").isNotEmpty())
                .andExpect(jsonPath("$.title").value(trackResponse.getTitle()))
                .andExpect(jsonPath("$.durationInSeconds").value(trackResponse.getDurationInSeconds()))
                .andExpect(jsonPath("$.releaseDate").value(trackResponse.getReleaseDate() != null ? trackResponse.getReleaseDate().toString() : null))
                .andExpect(jsonPath("$.genre").value(trackResponse.getGenre())).andDo(print());

        // Verify
        verify(artistService).addTrack(artistId, trackRequest);
        verifyNoMoreInteractions(artistService);
    }

    @DisplayName("Add Track - Bad Request when Track Title Empty")
    @Test
    void addTrack_BadRequest_EmptyTitle() throws Exception {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var trackRequest = TestUtils.createTrackRequest();
        trackRequest.setTitle("");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/artists/{artistId}/tracks", artistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(trackRequest)))
                //Assert
                .andExpect(status().isBadRequest())
                .andDo(print());

        // Verify
        verify(artistService, never()).addTrack(any(), any());
    }

    @DisplayName("Add Track - Not Found when Artist Service Throws EntityNotFoundException")
    @Test
    void addTrack_NotFound_ArtistNotFound() throws Exception {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var trackRequest = TestUtils.createTrackRequest();

        when(artistService.addTrack(artistId, trackRequest))
                .thenThrow(new EntityNotFoundException("Artist not found"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/artists/{artistId}/tracks", artistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(trackRequest)))
                //Assert
                .andExpect(status().isNotFound())
                .andDo(print());

        // Verify
        verify(artistService).addTrack(artistId, trackRequest);
    }

    @DisplayName("Get Artist Tracks - Success Case with Pagination")
    @Test
    void getArtistTracks_Success_Pagination() throws Exception {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var page = 0;
        var size = 10;

        var mockTrackPageResponse = TestUtils.mockTrackPageResponse(page, size, size * 2);

        when(artistService.getArtistTracks(artistId, page, size))
                .thenReturn(mockTrackPageResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/artists/{artistId}/tracks", artistId)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                //Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.pageSize").value(size))
                .andExpect(jsonPath("$.totalPages").value(mockTrackPageResponse.getTotalPages()))
                .andExpect(jsonPath("$.totalItems").value(mockTrackPageResponse.getTotalItems()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(mockTrackPageResponse.getData().size()))
                .andDo(print());

        // Verify
        verify(artistService).getArtistTracks(artistId, page, size);
    }

    @DisplayName("Get Artist Tracks - Not Found when Artist Service Throws EntityNotFoundException")
    @Test
    void getArtistTracks_NotFound_ArtistNotFound() throws Exception {
        // Arrange
        var artistId = UUID.randomUUID();
        var defaultPage = 0;
        var defaultSize = 20;

        when(artistService.getArtistTracks(artistId, defaultPage, defaultSize))
                .thenThrow(new EntityNotFoundException("Artist not found"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/artists/{artistId}/tracks", artistId)
                        .accept(MediaType.APPLICATION_JSON))
                //Assert
                .andExpect(status().isNotFound())
                .andDo(print());

        // Verify
        verify(artistService).getArtistTracks(artistId, defaultPage, defaultSize);
        verifyNoMoreInteractions(artistService);
    }

    @DisplayName("Update Artist Name - Success Case")
    @Test
    void updateArtistName_Success() throws Exception {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var newName = "Madonna (Official)";
        var aliases = List.of("Material Girl", "Queen of Pop");

        var updateRequest = new UpdateArtistNameRequest()
                .name(newName)
                .aliases(aliases);

        var artistResponse = new ArtistResponse()
                .artistId(artistId)
                .name(newName)
                .bio("Test Bio")
                .aliases(aliases);

        when(artistService.updateArtistName(artistId, updateRequest))
                .thenReturn(artistResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/artists/{artistId}/name", artistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistId").value(artistId.toString()))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.bio").value("Test Bio"))
                .andExpect(jsonPath("$.aliases").isArray())
                .andExpect(jsonPath("$.aliases.length()").value(aliases.size()))
                .andExpect(jsonPath("$.aliases[0]").value(aliases.get(0)))
                .andExpect(jsonPath("$.aliases[1]").value(aliases.get(1)))
                .andDo(print());

        // Verify
        verify(artistService).updateArtistName(artistId, updateRequest);
        verifyNoMoreInteractions(artistService);
    }


    @DisplayName("Update Artist Name - Bad Request when Name Empty")
    @Test
    void updateArtistName_BadRequest_EmptyName() throws Exception {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var updateRequest = new UpdateArtistNameRequest()
                .name("")
                .aliases(List.of("Alias1", "Alias2"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/v1/artists/{artistId}/name", artistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isBadRequest())
                .andDo(print());

        // Verify
        verify(artistService, never()).updateArtistName(any(), any());
    }


    @DisplayName("Get Artist of the Day - Success Case")
    @Test
    void getArtistOfTheDay_Success() throws Exception {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var artistName = TestUtils.MADONNA_ARTIST_NAME;
        var bio = "Grammy-winning artist with multiple hits";
        var aliases = List.of("Material Girl", "Queen of Pop");

        var artistResponse = new ArtistResponse()
                .artistId(artistId)
                .name(artistName)
                .bio(bio)
                .aliases(aliases);

        when(artistService.getArtistOfTheDay()).thenReturn(artistResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/artists/artist-of-the-day")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.artistId").value(artistId.toString()))
                .andExpect(jsonPath("$.name").value(artistName))
                .andExpect(jsonPath("$.bio").value(bio))
                .andExpect(jsonPath("$.aliases").isArray())
                .andExpect(jsonPath("$.aliases.length()").value(aliases.size()))
                .andExpect(jsonPath("$.aliases[0]").value(aliases.get(0)))
                .andExpect(jsonPath("$.aliases[1]").value(aliases.get(1)))
                .andDo(print());

        // Verify
        verify(artistService).getArtistOfTheDay();
        verifyNoMoreInteractions(artistService);
    }
}