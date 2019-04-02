package gpio;

import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import util.MpcWrapper;


public class VolumeDownAction extends DebouncedGpiAction {
    private final MpcWrapper mpcWrapper;

    public VolumeDownAction(PinEdge edge, MpcWrapper mpcWrapper) {
        super(edge);
        this.mpcWrapper = mpcWrapper;
    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        mpcWrapper.volumeDown(4);
    }
}
