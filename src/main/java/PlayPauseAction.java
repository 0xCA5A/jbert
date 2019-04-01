import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;


class PlayPauseAction extends DebouncedGpiAction {
    private final MpdCommunicator mpdCommunicator;
    private boolean playbackActive;

    PlayPauseAction(PinEdge edge, MpdCommunicator mpdCommunicator) {
        super(edge);
        this.mpdCommunicator = mpdCommunicator;
        playbackActive = false;
    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        if (playbackActive) {
            mpdCommunicator.pause();
            playbackActive = false;
        } else {
            mpdCommunicator.play();
            playbackActive = true;
        }
    }
}
