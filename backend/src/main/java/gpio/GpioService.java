package gpio;

public interface GpioService {

    void registerGpiListener(String pinName, DebouncedGpiAction debouncedGpiAction);

}
