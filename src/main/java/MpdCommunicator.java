import org.bff.javampd.player.Player;
import org.bff.javampd.server.MPD;
import org.bff.javampd.server.ServerStatus;

import java.util.logging.Logger;

class MpdCommunicator {
    private static final Logger logger = Logger.getLogger(MpdCommunicator.class.getName());

    private final String server;
    private final int port;
    private final MPD mpd;

    MpdCommunicator(String server, int port) {
        this.server = server;
        this.port = port;
        this.mpd = new MPD.Builder()
                .server(this.server)
                .port(this.port)
                .build();
    }

    void configure() {
        logger.info(String.format("Configure MPD communicator [%s:%d]", server, port));
        if (!mpd.isConnected()) {
            throw new RuntimeException(String.format("Can not establish connection to MPD server @%s:%d", server, port));
        }
    }

    void play() {
        mpd.getPlayer().play();
    }

    void pause() {
        mpd.getPlayer().pause();
    }

    void increaseVolume() {
        Player player = mpd.getPlayer();
        int newVolumeValue = player.getVolume() + 10;
        logger.info(String.format("Increased volume: %d%%", newVolumeValue));
        player.setVolume(newVolumeValue);
    }

    void decreaseVolume() {
        Player player = mpd.getPlayer();
        int newVolumeValue = player.getVolume() - 10;
        logger.info(String.format("Decreased volume: %d%%", newVolumeValue));
        player.setVolume(newVolumeValue);
    }
}
