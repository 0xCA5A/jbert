package ch.jbert.mpd;

import ch.jbert.util.MpcWrapper;
import ch.jbert.util.ThreadHelper;
import org.bff.javampd.player.Player;
import org.bff.javampd.server.MPD;
import org.bff.javampd.song.MPDSong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

public class MpdServiceImpl implements MpdService {
    private static final Logger logger = LoggerFactory.getLogger(MpdServiceImpl.class);
    private static final int VOLUME_CHANGE_STEP_IN_PERCENT = 5;
    private final static int DEFAULT_VOLUME_IN_PERCENT = 25;
    private final static int MAX_VOLUME_IN_PERCENT = 75;
    private final static int MIN_VOLUME_IN_PERCENT = 15;
    private final static boolean PLAYER_REPEAT = true;
    private final static boolean X_FADE = true;
    private static final Duration X_FADE_DURATION = Duration.ofSeconds(3);

    private final String server;
    private final int port;
    private final MPD mpd;

    // Workaround for strange behaving MPD library
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
    public void ensureConnection() {
        if (!mpd.isConnected()) {
            throw new IllegalStateException(String.format("Can not establish connection to MPD server %s:%d", server, port));
        }
    }

    @Override
    public void configure() {
        if (PLAYER_REPEAT) {
            logger.info("Enable repeat playing");
            mpd.getPlayer().setRepeat(true);
        }
        if (X_FADE) {
            mpd.getPlayer().setXFade((int) X_FADE_DURATION.getSeconds());
        }

        mpd.getPlayer().setVolume(DEFAULT_VOLUME_IN_PERCENT);
    }

    @Override
    public void loadPlaylist(String playlist) {
        logger.info("Loading playlist '{}'", playlist);

        mpd.getPlaylist().clearPlaylist();
        ThreadHelper.snooze(Duration.ofMillis(300));
        mpd.getPlaylist().loadPlaylist(playlist);
        ThreadHelper.snooze(Duration.ofMillis(300));
    }

    @Override
    public void play() {
        mpd.getPlayer().play();
    }

    @Override
    public void play(int trackIndex) {
        final List<MPDSong> songList = mpd.getPlaylist().getSongList();

        if (trackIndex >= songList.size()) {
            final String message = String.format("No track #%d found in currant playlist (%d songs)", trackIndex, songList.size());
            throw new IllegalArgumentException(message);
        } else {
            mpd.getPlayer().playSong(songList.get(trackIndex));
        }
    }

    @Override
    public void pause() {
        mpd.getPlayer().pause();
    }

    @Override
    public void playNext() {
        final Player player = mpd.getPlayer();

        player.playNext();
        logger.info("Next track in playlist selected, playing: {}", player.getCurrentSong());
    }

    @Override
    public void playPrevious() {
        mpd.getPlayer().playPrevious();
        logger.info("Previous track in playlist selected, playing: {}", mpd.getPlayer().getCurrentSong());
    }

    @Override
    public void increaseVolume() {
        mpcWrapper.getVolume()
                .flatMap(this::checkVolumeIncrease)
                .flatMap(mpcWrapper::setVolume);
    }

    @Override
    public void decreaseVolume() {
        mpcWrapper.getVolume()
                .flatMap(this::checkVolumeDecrease)
                .flatMap(mpcWrapper::setVolume);
    }

    private Optional<Integer> checkVolumeIncrease(int volume) {
        final int targetValue = volume + VOLUME_CHANGE_STEP_IN_PERCENT;
        return targetValue > MAX_VOLUME_IN_PERCENT
                ? Optional.empty()
                : Optional.of(targetValue);
    }

    private Optional<Integer> checkVolumeDecrease(int volume) {
        final int targetValue = volume - VOLUME_CHANGE_STEP_IN_PERCENT;
        return targetValue < MIN_VOLUME_IN_PERCENT
                ? Optional.empty()
                : Optional.of(targetValue);
    }
}
