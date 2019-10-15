package rfid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rfid.rc522.api.RC522SimpleAPI;
import util.ThreadHelper;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;


public class RfidTagDetector {
    private static final Logger logger = LoggerFactory.getLogger(RfidTagDetector.class);

    private ExecutorService executorService;
    private Duration scanInterval = Duration.ofSeconds(1);

    private RfidTagUid rfidTagUid;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void start(ExecutorService executorService, Duration scanInterval) {
        this.executorService = executorService;
        this.scanInterval = scanInterval;
        executorService.submit(this::findRfidTags);
    }

    public void stop() {
        executorService.shutdown();
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void findRfidTags() {
        while (!Thread.interrupted()) {
            readRfidTagUid().ifPresent(this::notifyListener);
            ThreadHelper.snooze(scanInterval);
        }
    }

    private void notifyListener(RfidTagUid rfidTagUid) {
        propertyChangeSupport.firePropertyChange("rfidTagUid", this.rfidTagUid, rfidTagUid);
        this.rfidTagUid = rfidTagUid;
    }

    private Optional<RfidTagUid> readRfidTagUid() {
        try {
            byte[] uid = new byte[5];
            RC522SimpleAPI.getInstance().findCards().getUid(uid);
            RfidTagUid rfidTagUid = new RfidTagUid(uid);
            logger.debug("RFID tag detected: {}", rfidTagUid);
            return Optional.of(rfidTagUid);

        } catch (RC522SimpleAPI.SimpleAPIException e) {
            logger.debug("No RFID tag detected (read interval: {}ms)", scanInterval.toMillis());
        }
        return Optional.empty();
    }
}
