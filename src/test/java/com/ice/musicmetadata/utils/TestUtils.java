package com.ice.musicmetadata.utils;

import com.ice.musicmetadata.domain.Artist;
import com.ice.musicmetadata.domain.ArtistAlias;
import com.ice.musicmetadata.domain.Track;
import com.ice.musicmetadata.model.ArtistResponse;
import com.ice.musicmetadata.model.TrackPageResponse;
import com.ice.musicmetadata.model.TrackRequest;
import com.ice.musicmetadata.model.TrackResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public interface TestUtils {


    UUID THE_BEATLES_ARTIST_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    String THE_BEATLES_ARTIST_NAME = "The Beatles";
    String THE_BEATLES_TRACK_TITLE = "Hey Jude";

    UUID MADONNA_ARTIST_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    String MADONNA_ARTIST_NAME = "Madonna";
    String MADONNA_TRACK_TITLE = "Like a Prayer";
    UUID MADONNA_TRACK_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");

    UUID MICHAEL_JACKSON_ARTIST_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174003");
    String MICHAEL_JACKSON_ARTIST_NAME = "Michael Jackson";
    String MICHAEL_JACKSON_TRACK_TITLE = "Billie Jean";
    UUID MICHAEL_JACKSON_TRACK_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174008");

    //
    Random RAND = new Random();
    Pattern UUID_REGEX_PATTERN =
            Pattern.compile("[\\da-f]{8}-(?:[\\da-f]{4}-){3}[\\da-f]{12}");


    static Artist createArtist() {
        return createArtist(UUID.randomUUID());
    }

    static Artist createArtist(UUID artistId) {
        return Artist.builder()
                .id(RAND.nextLong(1, 1000))
                .name("Test Artist")
                .artistId(artistId)
                .featuredAt(null)
                .bio("Test Bio")
                .build();
    }

    static Track createTrack(UUID artistId) {
        return createTrack(createArtist(artistId), null);
    }

    static Track createTrack(Artist artist, Long id) {
        return Track.builder()
                .id(id)
                .trackId(UUID.randomUUID())
                .title("Test Track")
                .artist(artist)
                .duration(RAND.nextInt(100, 800))
                .releaseDate(LocalDate.now())
                .build();
    }

    static ArtistResponse mockArtistResponse(Artist artist) {
        return new ArtistResponse()
                .artistId(artist.getArtistId())
                .name(artist.getName())
                .bio(artist.getBio())
                .aliases(artist.getAliases()
                        .stream()
                        .map(ArtistAlias::getAlias).toList(
                        ));
    }

/*    static Page<Track> createTrackPage(Artist artist) {
        return Track.builder()
                .title("Test Track")
                .artist(artist)
                .featuredAt(null)
                .build();
    }*/

   /* static Genre createGenre() {
        return Genre.builder()

                .name("Test Genre")
                .build();
    }*/

    //
    static Page<Track> createTrackPage(Artist artist, int page, int size) {
        return new PageImpl<>(generateTrackList(artist, size), PageRequest.of(page, size), size);
    }

    static Page<Track> createTrackPage(UUID artistId, int page, int size) {
        return new PageImpl<>(generateTrackList(createArtist(artistId), size), PageRequest.of(page, size), size);
    }

    static List<Track> generateTrackList(Artist artist, int size) {
        return IntStream.range(0, size).mapToObj(i -> createTrack(artist, (long) i)).toList();
    }


    //
    static TrackRequest createTrackRequest() {
        return new TrackRequest()
                .title("Test Track")
                .durationInSeconds(RAND.nextInt(100, 800))
                .releaseDate(LocalDate.now())
                .genre("Test Genre");
    }

    static TrackPageResponse mockTrackPageResponse(int page, int size,int total) {
        return new TrackPageResponse()
                .page(page)
                .pageSize(size)
                .totalItems(total)
                .totalPages(0)
                .data(generateTrackResponseList(total));
    }

    static List<TrackResponse> generateTrackResponseList(int size) {
        return IntStream.range(0, size).mapToObj(i -> mockTrackResponse(i)).toList();
    }

   /* static TrackRequest  createTrackResponse(){
        return new TrackResponse()
                .trackId(UUID.randomUUID())
                .title("Test Track")
                .durationInSeconds(RAND.nextInt(100, 800))
                .releaseDate(LocalDate.now())
                .genre("Test Genre");
    }*/



    static TrackResponse mockTrackResponse(TrackRequest trackRequest) {
        return new TrackResponse()
                .trackId(UUID.randomUUID())
                .title(trackRequest.getTitle())
                .durationInSeconds(trackRequest.getDurationInSeconds())
                .releaseDate(trackRequest.getReleaseDate())
                .genre(trackRequest.getGenre());
    }

    static TrackResponse mockTrackResponse(int i) {
        return new TrackResponse()
                .trackId(UUID.randomUUID())
                .title(MADONNA_TRACK_TITLE+" " + i)
                .durationInSeconds(RAND.nextInt(100, 800))
                .releaseDate(LocalDate.now())
                .genre("Test Genre + " + i);
    }
}
