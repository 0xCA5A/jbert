import org.bff.javampd.server.MPD;
import org.bff.javampd.server.ServerStatus;

import java.util.logging.Logger;

public class MpdCommunicator {
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

    public void configure() {
        logger.info(String.format("Configure MPD communicator [%s:%d]", server, port));
    }

    public void getStatus() {
        ServerStatus status = mpd.getServerStatus();
        logger.info(String.format("MPD status::getElapsedTime: %s", status.getElapsedTime()));
        logger.info(String.format("MPD status::getAudio: %s", status.getAudio()));
        logger.info(String.format("MPD status::getState: %s", status.getState()));
        logger.info(String.format("MPD status::getStatus: %s", status.getStatus()));
        logger.info(String.format("MPD status::getVolume: %s", status.getVolume()));
        logger.info(String.format("MPD status::isRepeat: %s", status.isRepeat()));
        logger.info(String.format("MPD status::isRandom: %s", status.isRandom()));
    }

    public static void main(String... args) {
        MpdCommunicator m = new MpdCommunicator("10.0.50.120", 6600);
        m.configure();
        m.getStatus();

    }
}
