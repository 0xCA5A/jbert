package models;

import java.util.Objects;

public class Playlist implements Comparable<Playlist> {

    private PlaylistDto wrapped;

    private Playlist(PlaylistDto wrapped) {
        this.wrapped = wrapped;
    }

    public static Playlist wrap(PlaylistDto dto) {
        return new Playlist(dto);
    }

    public PlaylistDto unwrap() {
        return wrapped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(wrapped.getName(), playlist.wrapped.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(wrapped.getName());
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "name='" + wrapped.getName() + '\'' +
                '}';
    }

    @Override
    public int compareTo(Playlist o) {
        return this.wrapped.getName().compareTo(o.unwrap().getName());
    }
}
