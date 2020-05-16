package ch.jbert.models;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Comparators;

@JsonDeserialize(builder = Track.Builder.class)
public final class Track implements Comparable<Track> {

    private final Metadata metadata;
    private final String data;

    public Track(Metadata metadata, String data) {
        this.metadata = metadata;
        this.data = data;
    }

    public Track(Metadata metadata) {
        this(metadata, null);
    }

    public Optional<Metadata> getMetadata() {
        return Optional.ofNullable(this.metadata);
    }

    /**
     * Base64 encoded data
     */
    public Optional<String> getData() {
        return Optional.ofNullable(this.data);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Track track = (Track) o;
        return Objects.equals(metadata, track.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata);
    }

    @Override
    public String toString() {
        return String.format("Track[metadata=%s]", metadata);
    }

    @Override
    public int compareTo(Track o) {

        final Comparator<Optional<String>> comparator = Comparators.emptiesFirst(Comparator.naturalOrder());
        final int comparingArtist = comparator.compare(metadata.getArtist(), o.metadata.getArtist());
        if (comparingArtist != 0) {
            return comparingArtist;
        }

        final int comparingAlbum = comparator.compare(metadata.getAlbum(), o.metadata.getAlbum());
        if (comparingAlbum != 0) {
            return comparingAlbum;
        }

        final int comparingTitle = comparator.compare(metadata.getTitle(), o.metadata.getTitle());
        if (comparingTitle != 0) {
            return comparingTitle;
        }

        return 0;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private Metadata metadata;
        private String data;

        public Track build() {
            return new Track(metadata, data);
        }

        public Builder withMetadata(Metadata value) {
            this.metadata = value;
            return this;
        }

        /**
         * Base64 encoded data
         */
        public Builder withData(String value) {
            this.data = value;
            return this;
        }
    }
}