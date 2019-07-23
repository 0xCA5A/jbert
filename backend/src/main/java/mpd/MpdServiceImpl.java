package mpd;

import org.bff.javampd.player.Player;
import org.bff.javampd.server.MPD;
import util.LogHelper;
import util.MpcWrapper;

import java.time.Duration;
import java.util.logging.Logger;

public class MpdServiceImpl implements MpdService {
    private static final Logger logger = LogHelper.getLogger(MpdServiceImpl.class.getName());
    private static final int VOLUME_CHANGE_STEP = 5; // value in percent
    private final static int DEFAULT_VOLUME = 20; // value in percent

    private final String server;
    private final int port;
    private final MPD mpd;

    private final boolean playerRepeat = true;
    private final boolean xFade = true;
    private final Duration xFadeDuration = Duration.ofSeconds(3);

    // Workaround strange behaving MPD library
    private final MpcWrapper mpcWrapper;

    public MpdServiceImpl(String server, int port) {
        this.server = server;
        this.port = port;
        this.mpd = new MPD.Builder()
                .server(this.server)
                .port(this.port)
                .build();

        this.mpcWrapper = new MpcWrapper(server, port);
    }

    @Override
    public void isConnected() {
        logger.info(String.format("Configure MPD communicator [%s:%d]", server, port));
        if (!mpd.isConnected()) {
            throw new RuntimeException(String.format("Can not establish connection to MPD server %s:%d", server, port));
        }
    }

    @Override
    public void configure() {
        if (playerRepeat) {
            logger.config("Enable repeat playing");
            mpd.getPlayer().setRepeat(true);
        }
        if (xFade) {
            mpd.getPlayer().setXFade((int) xFadeDuration.getSeconds());
        }

        mpd.getPlayer().setVolume(DEFAULT_VOLUME);
    }

    @Override
    public void loadPlaylist(String playlist) {
        logger.info(String.format("Loading playlist '%s'", playlist));

        mpd.getPlaylist().clearPlaylist();
        sleep(300);
        mpd.getPlaylist().loadPlaylist(playlist);
        sleep(300);
        mpd.getPlayer().play();
    }

    @Override
    public void play() {
        mpd.getPlayer().play();
    }

    @Override
    public void pause() {
        mpd.getPlayer().pause();
    }

    @Override
    public void playNext() {
        mpd.getPlayer().playNext();
        logger.info(String.format("Next track in playlist selected, playing: %s", mpd.getPlayer().getCurrentSong()));
    }

    @Override
    public void playPrevious() {
        mpd.getPlayer().playPrevious();
        logger.info(String.format("Previous track in playlist selected, playing: %s", mpd.getPlayer().getCurrentSong()));
    }

    @Override
    public void increaseVolume() {
        Player player = mpd.getPlayer();
        final int newVolumeValue = player.getVolume() + VOLUME_CHANGE_STEP;
        logger.info(String.format("Increased volume: %d%%", newVolumeValue));

        mpcWrapper.volumeUp(VOLUME_CHANGE_STEP);
    }

    @Override
    public void decreaseVolume() {
        Player player = mpd.getPlayer();
        final int newVolumeValue = player.getVolume() - VOLUME_CHANGE_STEP;
        logger.info(String.format("Decreased volume: %d%%", newVolumeValue));

        mpcWrapper.volumeDown(VOLUME_CHANGE_STEP);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
