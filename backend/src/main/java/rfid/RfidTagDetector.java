package rfid;

import rfid.rc522.api.RC522SimpleAPI;
import utils.GeneralException;
import utils.LogHelper;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;


public class RfidTagDetector {
    private static final Logger logger = LogHelper.getLogger(RfidTagDetector.class.getName());

    private ExecutorService executorService;
    private Duration scanInterval = Duration.ofSeconds(1);

    private RfidTagUid rfidTagUid;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void notifyListener(RfidTagUid rfidTagUid) {
        propertyChangeSupport.firePropertyChange("rfidTagUid", this.rfidTagUid, rfidTagUid);
        this.rfidTagUid = rfidTagUid;
    }

    public void start(ExecutorService executorService, Duration scanInterval) {
        this.executorService = executorService;
        this.scanInterval = scanInterval;
        executorService.submit(this::findRfidTags);
    }

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    private void findRfidTags() {
        while (true) {
            readRfidTagUid().ifPresent(this::notifyListener);
            snooze(scanInterval);
        }
    }

    public void stop() {
        executorService.shutdown();
    }

    static void snooze(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new GeneralException(e);
        }
    }

    private Optional<RfidTagUid> readRfidTagUid() {
        try {
            byte[] uid = new byte[5];
            RC522SimpleAPI.getInstance().findCards().getUid(uid);
            RfidTagUid rfidTagUid = new RfidTagUid(uid);
            logger.fine(String.format("RFID tag detected: %s", rfidTagUid));
            return Optional.of(rfidTagUid);

        } catch (RC522SimpleAPI.SimpleAPIException e) {
            logger.fine(String.format("No RFID tag detected (read interval: %sms)", scanInterval.toMillis()));
        }
        return Optional.empty();
    }


}