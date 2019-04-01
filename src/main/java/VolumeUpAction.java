import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;


class VolumeUpAction extends DebouncedGpiAction {
    private final MpdCommunicator mpdCommunicator;

    VolumeUpAction(PinEdge edge, MpdCommunicator mpdCommunicator) {
        super(edge);
        this.mpdCommunicator = mpdCommunicator;
    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        mpdCommunicator.increaseVolume();
    }
}
