package modules;

import com.google.inject.AbstractModule;
import gpio.GpioService;
import gpio.GpioServiceImpl;
import gpio.GpioServiceMockImpl;
import rfid.RfidService;
import rfid.RfidServiceImpl;
import rfid.RfidServiceMockImpl;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HalModule extends AbstractModule {

    private final boolean halMockEnabled;

    @Inject
    HalModule(boolean halMockEnabled) {
        this.halMockEnabled = halMockEnabled;
    }

    @Override
    protected void configure() {
        bind(ExecutorService.class).toInstance(Executors.newFixedThreadPool(3));

        if (halMockEnabled) {
            bind(GpioService.class).to(GpioServiceMockImpl.class).asEagerSingleton();
            bind(RfidService.class).to(RfidServiceMockImpl.class).asEagerSingleton();
        } else {
            bind(GpioService.class).to(GpioServiceImpl.class).asEagerSingleton();
            bind(RfidService.class).to(RfidServiceImpl.class).asEagerSingleton();
        }
    }
}
