package ch.jbert.rfid;

import ch.jbert.rfid.rc522.api.RC522SimpleAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.Optional;

@Singleton
public class RfidTagDetector {
    private static final Logger logger = LoggerFactory.getLogger(RfidTagDetector.class);

    private Duration scanInterval = Duration.ofSeconds(1);
    private RfidTagUid rfidTagUid;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    void findRfidTags() {
        readRfidTagUid().ifPresent(this::notifyListener);
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
