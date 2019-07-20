package rfid;

import javax.inject.Inject;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.util.concurrent.ExecutorService;


public class RfidServiceImpl implements RfidService {

    private final RfidTagDetector rfidTagDetector = new RfidTagDetector();

    @Inject
    RfidServiceImpl(ExecutorService executorService) {
        rfidTagDetector.start(executorService, Duration.ofSeconds(2));
    }

    @Override
    public void addListener(PropertyChangeListener listener) {
        rfidTagDetector.addListener(listener);
    }
}
