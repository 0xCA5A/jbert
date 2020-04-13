package ch.jbert.models;

import com.google.common.collect.Comparators;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class Track implements Comparable<Track> {

    private TrackDto wrapped;

    private Track(TrackDto wrapped) {
        this.wrapped = wrapped;
    }

    public static Track wrap(TrackDto dto) {
        return new Track(dto);
    }

    public TrackDto unwrap() {
        return wrapped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(wrapped.getMetadata(), track.wrapped.getMetadata());
    }

    @Override
    public int hashCode() {
        return Objects.hash(wrapped.getMetadata());
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "metadata='" + wrapped.getMetadata() + '\'' +
                '}';
    }

    @Override
    public int compareTo(Track o) {

        final MetadataDto metadata = this.wrapped.getMetadata();
        final MetadataDto otherMetadata = o.unwrap().getMetadata();

        final Optional<String> artist = metadata.getArtistOptional();
        Comparator<Optional<String>> comparator = Comparators.emptiesFirst(Comparator.naturalOrder());
        final int comparingArtist = comparator.compare(metadata.getArtistOptional(), otherMetadata.getArtistOptional());
        if (comparingArtist != 0) {
            return comparingArtist;
        }

        final int comparingAlbum = comparator.compare(metadata.getAlbumOptional(), otherMetadata.getAlbumOptional());
        if (comparingAlbum != 0) {
            return comparingAlbum;
        }

        final int comparingTitle = comparator.compare(metadata.getTitleOptional(), otherMetadata.getTitleOptional());
        if (comparingTitle != 0) {
            return comparingTitle;
        }

        return 0;
    }
}
