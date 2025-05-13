package com.ice.musicmetadata.mapper;

import com.ice.musicmetadata.domain.Artist;
import com.ice.musicmetadata.domain.ArtistAlias;
import com.ice.musicmetadata.model.ArtistResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ArtistMapper {

    ArtistResponse mapToArtistResponse(Artist artist);

    default String mapToArtistAliasName(ArtistAlias artistAlias) {
        return artistAlias != null ? artistAlias.getAlias() : null;
    }
}
