package gpio;

import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import mpd.MpdService;


public class PlayPauseAction extends DebouncedGpiAction {
    private final MpdService mpdService;
    private boolean playbackActive;

    public PlayPauseAction(MpdService mpdService) {
        super(PinEdge.RISING);
        this.mpdService = mpdService;
        playbackActive = false;
    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        if (playbackActive) {
            mpdService.pause();
            playbackActive = false;
        } else {
            mpdService.play();
            playbackActive = true;
        }
    }
}
