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

    public static Metadata merge(Metadata m1, Metadata m2) {
        final Builder builder = newBuilder();
        m1.getTitle().ifPresent(builder::withTitle);
        m1.getArtist().ifPresent(builder::withArtist);
        m1.getYear().ifPresent(builder::withYear);
        m1.getGenre().ifPresent(builder::withGenre);
        m1.getComment().ifPresent(builder::withComment);
        m1.getDuration().ifPresent(builder::withDuration);
        m1.getAlbum().ifPresent(builder::withAlbum);
        m2.getTitle().ifPresent(builder::withTitle);
        m2.getArtist().ifPresent(builder::withArtist);
        m2.getYear().ifPresent(builder::withYear);
        m2.getGenre().ifPresent(builder::withGenre);
        m2.getComment().ifPresent(builder::withComment);
        m2.getDuration().ifPresent(builder::withDuration);
        m2.getAlbum().ifPresent(builder::withAlbum);
        return builder.build();
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
        return Optional.ofNullable(genre);
    }

    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
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

    public Builder getBuilder() {
        final Builder builder = new Builder();
        getTitle().ifPresent(builder::withTitle);
        getArtist().ifPresent(builder::withArtist);
        getAlbum().ifPresent(builder::withAlbum);
        getYear().ifPresent(builder::withYear);
        getGenre().ifPresent(builder::withGenre);
        getComment().ifPresent(builder::withComment);
        getDuration().ifPresent(builder::withDuration);
        return builder;
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

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder withAlbum(String album) {
            this.album = album;
            return this;
        }

        public Builder withYear(Integer year) {
            this.year = year;
            return this;
        }

        public Builder withGenre(String genre) {
            this.genre = genre;
            return this;
        }

        public Builder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        /**
         * Duration in seconds.
         */
        public Builder withDuration(Integer duration) {
            this.duration = duration;
            return this;
        }
    }
}
