package ch.jbert.gpio;

import ch.jbert.mpd.MpdService;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;


public class PlaylistLoadAction extends DebouncedGpiAction {
    private final MpdService mpdService;
    private final String playlist;

    public PlaylistLoadAction(MpdService mpdService, String playlist) {
        super(PinEdge.RISING);
        this.mpdService = mpdService;
        this.playlist = playlist;

    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        mpdService.loadPlaylist(playlist);
    }
}
