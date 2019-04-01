import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;


class VolumeDownAction extends DebouncedGpiAction {
    private final MpdCommunicator mpdCommunicator;

    VolumeDownAction(PinEdge edge, MpdCommunicator mpdCommunicator) {
        super(edge);
        this.mpdCommunicator = mpdCommunicator;
    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        mpdCommunicator.decreaseVolume();
    }
}
