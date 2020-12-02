package ch.jbert.models;

import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.Comparators;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonDeserialize(builder = Track.Builder.class)
public final class Track implements Comparable<Track> {

    private static final Logger LOG = LoggerFactory.getLogger(Track.class);

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
        return Optional.ofNullable(metadata);
    }

    /**
     * Base64 encoded data
     */
    public Optional<String> getData() {
        return Optional.ofNullable(data);
    }

    /**
     * Calculates the SHA256 hash of the Base64 encoded data
     */
    public String calculateSha256() throws IOException {
        LOG.trace("Calculating SHA256 hash from track data: {}", data);
        // final MessageDigest md = MessageDigest.getInstance("MD5");
        // String hash = (new HexBinaryAdapter()).marshal(md.digest(data.getBytes()));
        final ByteSource byteSource = getData()
                .map(data -> ByteSource.wrap(Base64.getDecoder().decode(data)))
                .orElse(ByteSource.empty());
        final String hash = byteSource.hash(Hashing.sha256()).toString();
        LOG.debug("Calculated SHA256 hash: {}", hash);
        return hash;
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

    public Builder getBuilder() {
        final Builder builder = new Builder();
        getMetadata().ifPresent(builder::withMetadata);
        getData().ifPresent(builder::withData);
        return builder;
    }

    public static final class Builder {
        private Metadata metadata;
        private String data;

        public Track build() {
            return new Track(metadata, data);
        }

        public Builder withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Base64 encoded data
         */
        public Builder withData(String data) {
            this.data = data;
            return this;
        }

        public Builder withData(byte[] data) {
            this.data = Base64.getEncoder().encodeToString(data);
            return this;
        }
    }
}