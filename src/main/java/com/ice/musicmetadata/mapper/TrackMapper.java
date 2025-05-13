package com.ice.musicmetadata.mapper;

import com.ice.musicmetadata.domain.Track;
import com.ice.musicmetadata.model.TrackPageResponse;
import com.ice.musicmetadata.model.TrackResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.Objects;

@Mapper(componentModel = "spring",
        imports = {java.util.List.class, java.util.UUID.class}
)
public interface TrackMapper {
    @Mapping(target = "page", source = "number")
    @Mapping(target = "pageSize", source = "size")
    @Mapping(target = "totalItems", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    @Mapping(target = "data", source = "content", defaultExpression = "java(List.of())")
    TrackPageResponse mapToTrackPageResponse(Page<Track> trackPage);

    @Mapping(target = "durationInSeconds", source = "duration")
    @Mapping(target = "genre", expression = "java(mapToGenreName(track))")
    TrackResponse mapToTrackResponse(Track track);

    default String mapToGenreName(Track track) {
        return Objects.isNull(track.getGenre()) ?
                track.getGenreName() :
                track.getGenre().getName();
    }
}
