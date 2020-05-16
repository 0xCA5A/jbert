package ch.jbert.models;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Playlist.Builder.class)
public final class Playlist implements Comparable<Playlist> {

    private final String name;
    private final List<Track> tracks;

    public Playlist(String name, List<Track> tracks) {
        this.name = name;
        this.tracks = tracks;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(this.name);
    }

    public List<Track> getTracks() {
        return this.tracks;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Playlist playlist = (Playlist) o;
        return Objects.equals(name, playlist.name)
            && Objects.equals(tracks, playlist.tracks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, tracks);
    }

    @Override
    public String toString() {
        return String.format("Playlist[name=%s, tracks=%s]", name, tracks);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private List<Track> tracks;

        public Playlist build() {
            return new Playlist(name, tracks);
        }

        public Builder withName(String value) {
            this.name = value;
            return this;
        }

        public Builder withTracks(List<Track> value) {
            this.tracks = value;
            return this;
        }
    }

    @Override
    public int compareTo(Playlist o) {
        return name.compareTo(o.name);
    }
}
