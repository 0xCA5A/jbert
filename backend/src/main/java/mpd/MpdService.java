package mpd;

public interface MpdService {
    void isConnected();

    void configure();

    void loadPlaylist(String playlist);

    void play();

    void pause();

    void playNext();

    void playPrevious();

    void increaseVolume();

    void decreaseVolume();
}
