package com.ice.musicmetadata.service;

import com.ice.musicmetadata.domain.Artist;
import com.ice.musicmetadata.domain.ArtistAlias;
import com.ice.musicmetadata.domain.Track;
import com.ice.musicmetadata.exception.NoEligibleArtistsException;
import com.ice.musicmetadata.mapper.ArtistMapper;
import com.ice.musicmetadata.mapper.TrackMapper;
import com.ice.musicmetadata.model.TrackResponse;
import com.ice.musicmetadata.model.UpdateArtistNameRequest;
import com.ice.musicmetadata.repository.ArtistRepository;
import com.ice.musicmetadata.repository.GenreRepository;
import com.ice.musicmetadata.repository.TrackRepository;
import com.ice.musicmetadata.utils.TestUtils;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @InjectMocks
    private ArtistService artistService;

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private ArtistMapper artistMapper;

    @Mock
    private TrackMapper trackMapper;

    @Mock
    private FeaturedArtistService featuredArtistService;


    @DisplayName("Should return ArtistResponse when getArtistOfTheDay is called")
    @Test
    void getArtistOfTheDay_Success() throws Exception {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var artist = TestUtils.createArtist(artistId);
        //
        var artistResponseMock = TestUtils.mockArtistResponse(artist);

        // Act
        when(featuredArtistService.getArtistOfTheDay()).thenReturn(artist);
        when(artistMapper.mapToArtistResponse(artist)).thenReturn(artistResponseMock);

        var artistResponse = artistService.getArtistOfTheDay();

        // Assert
        assertThat(artistResponse).isNotNull();
        assertThat(artistResponse.getArtistId()).isEqualTo(artistId);
        assertThat(artistResponse.getName()).isEqualTo(artist.getName());
        assertThat(artistResponse.getAliases()).containsExactlyElementsOf(artistResponseMock.getAliases());

        // Verify interactions
        var inOrder = inOrder(featuredArtistService, artistMapper);
        inOrder.verify(featuredArtistService, times(1)).getArtistOfTheDay();
        inOrder.verify(artistMapper, times(1)).mapToArtistResponse(artist);
        verifyNoMoreInteractions(featuredArtistService, artistMapper);

    }

    @DisplayName("Should throw NoEligibleArtistsException when getArtistOfTheDay is called and no artist is found")
    @Test
    void getArtistOfTheDay_NoEligibleArtists() {
        // Arrange
        // Act
        when(featuredArtistService.getArtistOfTheDay()).thenReturn(null);

        // Assert
        assertThatThrownBy(() -> artistService.getArtistOfTheDay())
                .isInstanceOf(NoEligibleArtistsException.class)
                .hasMessageContaining("No artist of the day found");

        // Verify interactions
        verify(featuredArtistService, only()).getArtistOfTheDay();
        verify(artistMapper, never()).mapToArtistResponse(any(Artist.class));
        verifyNoMoreInteractions(featuredArtistService);
    }

    @DisplayName("Given artistId, page, size When getArtistTracks is called Then return TrackPageResponse")
    @Test
    void getArtistTracks() {
        // Arrange
        // Create a mock artist ID and page request
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        int page = 0;
        int size = 10;

        // Act
        when(artistRepository.existsByArtistId(artistId)).thenReturn(true);
        when(trackRepository.findByArtist_ArtistId(artistId, PageRequest.of(page, size)))
                .thenReturn(TestUtils.createTrackPage(artistId, page, size));
        when(trackMapper.mapToTrackPageResponse(any()))
                .thenAnswer(invocation -> {
                    var trackPage = invocation.<Page<Track>>getArgument(0);
                    return Mappers.getMapper(TrackMapper.class).mapToTrackPageResponse(trackPage);
                });
        // Call the method to test
        var trackPageResponse = artistService.getArtistTracks(artistId, page, size);

        // Assert
        assertThat(trackPageResponse).isNotNull();
        assertThat(trackPageResponse.getData()).isNotEmpty();
        assertThat(trackPageResponse.getPage()).isEqualTo(page);
        assertThat(trackPageResponse.getPageSize()).isEqualTo(size);
        assertThat(trackPageResponse.getTotalPages()).isEqualTo(1);
        assertThat(trackPageResponse.getTotalItems()).isEqualTo(size);
        assertThat(trackPageResponse.getData()).allSatisfy(
                trackResponse -> {
                    assertThat(trackResponse.getTrackId()).isNotNull();
                    assertThat(trackResponse.getTitle()).isNotEmpty();
                    assertThat(trackResponse.getDurationInSeconds()).isNotNull();
                    assertThat(trackResponse.getReleaseDate()).isNotNull();
                }
        );
        // Verify that the repository, trackMapper methods were called with the correct parameters
        var inOrder = inOrder(artistRepository, trackRepository, trackMapper);
        inOrder.verify(artistRepository, times(1)).existsByArtistId(artistId);
        inOrder.verify(trackRepository, times(1)).findByArtist_ArtistId(artistId, PageRequest.of(page, size));
        inOrder.verify(trackMapper, times(1)).mapToTrackPageResponse(any());
        verifyNoMoreInteractions(trackRepository, trackMapper);
    }

    // ArtistResponse updateArtistName(UUID artistId, UpdateArtistNameRequest updateArtistNameRequest)
    @DisplayName("Given valid artistId and name When updateArtistName is called Then return updated ArtistResponse")
    @Test
    void updateArtistName_Success() throws IllegalArgumentException {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var newArtistName = "New Artist Name";
        var aliases = List.of("Alias1", "Alias2");
        var request = new UpdateArtistNameRequest()
                .name(newArtistName)
                .aliases(aliases);
        //
        var artist = TestUtils.createArtist(artistId);
        var updatedArtist = Artist.builder()
                .id(artist.getId())
                .artistId(artistId)
                .name(newArtistName)
                .aliases(aliases.stream().map(
                        alias -> ArtistAlias.builder()
                                .alias(alias)
                                .artist(artist)
                                .build()
                ).toList())
                .build();

        // Act
        when(artistRepository.findByArtistId(artistId)).thenReturn(Optional.of(artist));
        when(artistRepository.existsByArtistIdNotAndName(artistId, newArtistName)).thenReturn(false);
        when(artistRepository.save(any(Artist.class))).thenReturn(updatedArtist);
        when(artistMapper.mapToArtistResponse(updatedArtist))
                .thenAnswer(invocation -> {
                    var a = invocation.<Artist>getArgument(0);
                    return Mappers.getMapper(ArtistMapper.class).mapToArtistResponse(a);
                });

        var updateArtistName = artistService.updateArtistName(artistId, request);

        // Assert
        assertThat(updateArtistName).isNotNull();
        assertThat(updateArtistName.getName()).isEqualTo(newArtistName);

        // Verify interactions
        var inOrder = inOrder(artistRepository, artistMapper);
        inOrder.verify(artistRepository).findByArtistId(artistId);
        inOrder.verify(artistRepository).existsByArtistIdNotAndName(artistId, newArtistName);
        inOrder.verify(artistRepository).save(any(Artist.class));
        inOrder.verify(artistMapper).mapToArtistResponse(updatedArtist);
    }

    @DisplayName("Given artistId and existing name When updateArtistName is called Then throw IllegalArgumentException")
    @Test
    void updateArtistName_NameExists() {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var newArtistName = "Existing Artist Name";
        var request = new UpdateArtistNameRequest()
                .name(newArtistName)
                .aliases(List.of("Alias1", "Alias2"));

        // Create a mock artist
        var artist = TestUtils.createArtist(artistId);
        // Act
        when(artistRepository.findByArtistId(artistId)).thenReturn(Optional.of(artist));
        when(artistRepository.existsByArtistIdNotAndName(artistId, newArtistName)).thenReturn(true);

        // Assert
        assertThatThrownBy(() -> artistService.updateArtistName(artistId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Artist name already exists");

        // Verify interactions
        verify(artistRepository).findByArtistId(artistId);
        verify(artistRepository).existsByArtistIdNotAndName(artistId, newArtistName);
        verify(artistRepository, never()).save(any(Artist.class));
        verify(artistMapper, never()).mapToArtistResponse(any(Artist.class));
    }

    @DisplayName("Given nonexistent artistId When updateArtistName is called Then throw EntityNotFoundException")
    @Test
    void updateArtistName_ArtistNotFound() {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var request = new UpdateArtistNameRequest()
                .name("New Artist Name")
                .aliases(List.of("Alias1", "Alias2"));

        // Act
        when(artistRepository.findByArtistId(artistId)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> artistService.updateArtistName(artistId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Artist not found");

        // Verify interactions
        verify(artistRepository).findByArtistId(artistId);
        verify(artistRepository, never()).existsByArtistIdNotAndName(any(), any());
        verify(artistRepository, never()).save(any(Artist.class));
        verify(artistMapper, never()).mapToArtistResponse(any(Artist.class));
    }


    //TrackResponse addTrack(UUID artistId, TrackRequest trackRequest)
    @DisplayName("Given valid artistId and trackRequest When addTrack is called Then return TrackResponse")
    @Test
    void addTrack_Success() throws Exception {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var trackRequest = TestUtils.createTrackRequest();
        var artist = TestUtils.createArtist(artistId);
        var track = TestUtils.createTrack(artist, 3L);

        // Create a mock track Response
        var mockTrackResponse = new TrackResponse()
                .trackId(TestUtils.MADONNA_TRACK_ID)
                .title(trackRequest.getTitle())
                .durationInSeconds(trackRequest.getDurationInSeconds())
                .releaseDate(LocalDate.now())
                .genre(trackRequest.getGenre());
        // Act
        when(artistRepository.findByArtistId(artistId)).thenReturn(Optional.of(artist));
        when(trackRepository.existsByArtist_ArtistIdAndTitle(artistId, trackRequest.getTitle())).thenReturn(false);
        when(genreRepository.findByName(trackRequest.getGenre())).thenReturn(Optional.empty());
        when(trackRepository.save(any(Track.class))).thenReturn(track);
        when(trackMapper.mapToTrackResponse(track)).thenReturn(mockTrackResponse);

        var trackResponse = artistService.addTrack(artistId, trackRequest);

        // Assert
        assertThat(trackResponse).isNotNull();
        assertThat(trackResponse.getTrackId()).isNotNull();
        assertThat(trackResponse.getTitle()).isEqualTo(trackRequest.getTitle());
        assertThat(trackResponse.getDurationInSeconds()).isEqualTo(trackRequest.getDurationInSeconds());
        assertThat(trackResponse.getReleaseDate()).isEqualTo(trackRequest.getReleaseDate());

        // Verify interactions
        verify(artistRepository).findByArtistId(artistId);
        verify(trackRepository).existsByArtist_ArtistIdAndTitle(artistId, trackRequest.getTitle());
        verify(genreRepository).findByName(trackRequest.getGenre());
        verify(trackRepository).save(any(Track.class));
        verify(trackMapper).mapToTrackResponse(track);
    }

    @DisplayName("Given artistId and trackRequest with duplicate title When addTrack is called Then throw IllegalArgumentException")
    @Test
    void addTrack_DuplicateTitle() {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var trackRequest = TestUtils.createTrackRequest();
        var artist = TestUtils.createArtist(artistId);

        // Act
        when(artistRepository.findByArtistId(artistId)).thenReturn(Optional.of(artist));
        when(trackRepository.existsByArtist_ArtistIdAndTitle(artistId, trackRequest.getTitle())).thenReturn(true);

        // Assert
        assertThatThrownBy(() -> artistService.addTrack(artistId, trackRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Track title already exists for this artist");

        // Verify interactions
        verify(artistRepository).findByArtistId(artistId);
        verify(trackRepository).existsByArtist_ArtistIdAndTitle(artistId, trackRequest.getTitle());

        verifyNoMoreInteractions(artistRepository, trackRepository);
        verifyNoInteractions(genreRepository);
    }

    @DisplayName("Given non-existent artistId When addTrack is called Then throw EntityNotFoundException")
    @Test
    void addTrack_ArtistNotFound() {
        // Arrange
        var artistId = TestUtils.MADONNA_ARTIST_ID;
        var trackRequest = TestUtils.createTrackRequest();

        // Act
        when(artistRepository.findByArtistId(artistId)).thenReturn(Optional.empty());

        // Assert
        assertThatThrownBy(() -> artistService.addTrack(artistId, trackRequest))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Artist not found");

        // Verify interactions
        verify(artistRepository).findByArtistId(artistId);
        verifyNoMoreInteractions(artistRepository);
        verifyNoInteractions(trackRepository, genreRepository, trackMapper);
    }
}