package mpd;

public interface MpdService {
    void ensureConnection();

    void configure();

    void loadPlaylist(String playlist);

    void play();

    void play(int trackIndex);

    void pause();

    void playNext();

    void playPrevious();

    void increaseVolume();

    void decreaseVolume();
}
