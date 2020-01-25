package ch.jbert.gpio;

import ch.jbert.mpd.MpdService;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;

public class PlayTrackByIndexAction extends DebouncedGpiAction {

    private final MpdService mpdService;
    private final int trackIndex;

    public PlayTrackByIndexAction(MpdService mpdService, int trackIndex) {
        super(PinEdge.RISING);
        this.mpdService = mpdService;
        this.trackIndex = trackIndex;
    }

    @Override
    protected void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        mpdService.play(trackIndex);
    }
}
