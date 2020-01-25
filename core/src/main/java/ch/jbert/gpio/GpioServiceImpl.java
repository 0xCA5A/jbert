package ch.jbert.gpio;

import com.pi4j.io.gpio.RaspiPin;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class GpioServiceImpl implements GpioService {

    private List<GpiListener> gpiListenerList = new ArrayList<>();

    @Override
    public void registerGpiListener(String pinName, DebouncedGpiAction debouncedGpiAction) {
        GpiListener gpiListener = new GpiListener(RaspiPin.getPinByName(pinName));
        gpiListener.registerAction(debouncedGpiAction);
        gpiListenerList.add(gpiListener);
    }
}
