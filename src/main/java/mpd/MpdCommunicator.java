package mpd;

import org.bff.javampd.player.Player;
import org.bff.javampd.server.MPD;
import util.LogHelper;

import java.util.logging.Logger;


public class MpdCommunicator {
    private static final Logger logger = LogHelper.getLogger(MpdCommunicator.class.getName());

    private final String server;
    private final int port;
    private final MPD mpd;

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
    }

    public void play() {
        mpd.getPlayer().play();
    }

    public void pause() {
        mpd.getPlayer().pause();
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
