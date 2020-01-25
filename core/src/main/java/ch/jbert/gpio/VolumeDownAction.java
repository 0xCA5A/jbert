package ch.jbert.gpio;

import ch.jbert.mpd.MpdService;
import com.pi4j.io.gpio.PinEdge;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;


public class VolumeDownAction extends DebouncedGpiAction {
    private final MpdService mpdService;

    public VolumeDownAction(MpdService mpdService) {
        super(PinEdge.RISING);
        this.mpdService = mpdService;
    }

    @Override
    public void gpiEventAction(GpioPinDigitalStateChangeEvent event) {
        mpdService.decreaseVolume();
    }
}
