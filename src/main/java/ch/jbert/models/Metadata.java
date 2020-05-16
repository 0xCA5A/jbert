package ch.jbert.models;

import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Metadata.Builder.class)
public final class Metadata {

    private final String title;
    private final String artist;
    private final String album;
    private final Integer year;
    private final String genre;
    private final String comment;
    private final Integer duration;

    public Metadata(String title, String artist, String album, Integer year, String genre, String comment,
            Integer duration) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.year = year;
        this.genre = genre;
        this.comment = comment;
        this.duration = duration;
    }

    public Optional<String> getTitle() {
        return Optional.ofNullable(this.title);
    }

    public Optional<String> getArtist() {
        return Optional.ofNullable(this.artist);
    }

    public Optional<String> getAlbum() {
        return Optional.ofNullable(this.album);
    }

    public Optional<Integer> getYear() {
        return Optional.ofNullable(this.year);
    }

    public Optional<String> getGenre() {
        return Optional.ofNullable(this.genre);
    }

    public Optional<String> getComment() {
        return Optional.ofNullable(this.comment);
    }

    public Optional<Integer> getDuration() {
        return Optional.ofNullable(this.duration);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Metadata v = (Metadata) other;
        return Objects.equals(title, v.title)
            && Objects.equals(artist, v.artist)
            && Objects.equals(album, v.album)
            && Objects.equals(year, v.year)
            && Objects.equals(genre, v.genre)
            && Objects.equals(comment, v.comment)
            && Objects.equals(duration, v.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist, album, year, genre, comment, duration);
    }

    @Override
    public String toString() {
        return String.format("Metadata[title=%s, artist=%s, album=%s, year=%s, genre=%s, comment=%s, duration=%s]",
                title, artist, album, year, genre, comment, duration);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String title;
        private String artist;
        private String album;
        private Integer year;
        private String genre;
        private String comment;
        private Integer duration;

        public Metadata build() {
            return new Metadata(title, artist, album, year, genre, comment, duration);
        }

        public Builder withTitle(String value) {
            this.title = value;
            return this;
        }

        public Builder withArtist(String value) {
            this.artist = value;
            return this;
        }

        public Builder withAlbum(String value) {
            this.album = value;
            return this;
        }

        public Builder withYear(Integer value) {
            this.year = value;
            return this;
        }

        public Builder withGenre(String value) {
            this.genre = value;
            return this;
        }

        public Builder withComment(String value) {
            this.comment = value;
            return this;
        }

        public Builder withDuration(Integer value) {
            this.duration = value;
            return this;
        }
    }
}
