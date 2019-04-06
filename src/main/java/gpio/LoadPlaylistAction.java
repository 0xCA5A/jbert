package gpio;

import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import mpd.MpdCommunicator;


public class LoadPlaylistAction extends DebouncedGpiAction {
    private final MpdCommunicator mpdCommunicator;

    public LoadPlaylistAction(PinEdge edge, MpdCommunicator mpdCommunicator, String playlist) {
        super(edge);
        this.mpdCommunicator = mpdCommunicator;
    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {

    }
}
