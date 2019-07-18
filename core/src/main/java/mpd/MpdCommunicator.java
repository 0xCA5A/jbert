package mpd;

import org.bff.javampd.player.Player;
import org.bff.javampd.server.MPD;
import util.LogHelper;

import java.time.Duration;
import java.util.logging.Logger;


public class MpdCommunicator {
    private static final Logger logger = LogHelper.getLogger(MpdCommunicator.class.getName());

    private final String server;
    private final int port;
    private final MPD mpd;

    private final boolean playerRepeat = true;
    private final Duration crossfadeDuration = Duration.ofSeconds(3);

    public MpdCommunicator(String server, int port) {
        this.server = server;
        this.port = port;
        this.mpd = new MPD.Builder()
                .server(this.server)
                .port(this.port)
                .build();
    }

    public void configure() {
        logger.info(String.format("Configure MPD communicator [%s:%d]", server, port));
        if (!mpd.isConnected()) {
            throw new RuntimeException(String.format("Can not establish connection to MPD server @%s:%d", server, port));
        }

        logger.config("Enable repeat playing");
        mpd.getPlayer().setRepeat(true);
        mpd.getPlayer().setXFade((int) crossfadeDuration.getSeconds());
    }

    public void loadPlaylist(String playlist) {
        logger.info(String.format("Loading playlist '%s'", playlist));
        mpd.getPlaylist().clearPlaylist();
        sleep(300);
        mpd.getPlaylist().loadPlaylist(playlist);
        sleep(300);
        mpd.getPlayer().play();
        sleep(300);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        mpd.getPlayer().play();
    }

    public void pause() {
        mpd.getPlayer().pause();
    }

    public void playNext() {
        mpd.getPlayer().playNext();
        logger.info(String.format("Next track in playlist selected, playing: %s", mpd.getPlayer().getCurrentSong()));
    }

    public void playPrevious() {
        mpd.getPlayer().playPrevious();
        logger.info(String.format("Previous track in playlist selected, playing: %s", mpd.getPlayer().getCurrentSong()));
    }

    public void increaseVolume() {
        Player player = mpd.getPlayer();
        int newVolumeValue = player.getVolume() + 10;
        logger.info(String.format("Increased volume: %d%%", newVolumeValue));
        player.setVolume(newVolumeValue);
    }

    public void decreaseVolume() {
        Player player = mpd.getPlayer();
        int newVolumeValue = player.getVolume() - 10;
        logger.info(String.format("Decreased volume: %d%%", newVolumeValue));
        player.setVolume(newVolumeValue);
    }
}