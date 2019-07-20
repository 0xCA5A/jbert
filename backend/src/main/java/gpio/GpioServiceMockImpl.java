package gpio;

import util.LogHelper;

import java.util.logging.Logger;


public class GpioServiceMockImpl implements GpioService {
    private static final Logger logger = LogHelper.getLogger(GpioServiceMockImpl.class.getName());

    @Override
    public void registerGpiListener(String pinName, DebouncedGpiAction debouncedGpiAction) {

    }
}
