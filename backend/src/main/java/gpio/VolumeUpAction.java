package gpio;

import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import util.MpcWrapper;


public class VolumeUpAction extends DebouncedGpiAction {
    private final MpcWrapper mpcWrapper;

    public VolumeUpAction(PinEdge edge, MpcWrapper mpcWrapper) {
        super(edge);
        this.mpcWrapper = mpcWrapper;
    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        mpcWrapper.volumeUp(4);
    }
}
