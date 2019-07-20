package gpio;

import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import mpd.MpdService;


public class VolumeUpAction extends DebouncedGpiAction {
    private final MpdService mpdService;

    public VolumeUpAction(MpdService mpdService) {
        super(PinEdge.RISING);
        this.mpdService = mpdService;
    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        mpdService.increaseVolume();
    }
}
