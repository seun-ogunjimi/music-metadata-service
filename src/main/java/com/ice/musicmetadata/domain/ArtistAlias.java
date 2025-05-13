package com.ice.musicmetadata.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@ToString()
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "artist_aliases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistAlias {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @Column(nullable = false)
    private String alias;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Version
    @Column(nullable = false)
    private Long version;

    public void setArtist(Artist artist) {
        this.artist = artist;
        if (artist != null && !artist.getAliases().contains(this)) {
            artist.getAliases().add(this);
        }
    }
}
