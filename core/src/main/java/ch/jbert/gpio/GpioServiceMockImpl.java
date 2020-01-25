package ch.jbert.gpio;

import javax.inject.Singleton;

@Singleton
public class GpioServiceMockImpl implements GpioService {

    @Override
    public void registerGpiListener(String pinName, DebouncedGpiAction debouncedGpiAction) {
    }
}
