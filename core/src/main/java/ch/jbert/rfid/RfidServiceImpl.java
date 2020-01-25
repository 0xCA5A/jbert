package ch.jbert.rfid;

import javax.inject.Singleton;
import java.beans.PropertyChangeListener;

@Singleton
public class RfidServiceImpl implements RfidService {
    private final RfidTagDetector rfidTagDetector = new RfidTagDetector();

    @Override
    public void addListener(PropertyChangeListener listener) {
        rfidTagDetector.addListener(listener);
    }

    @Override
    public void scan() {
        rfidTagDetector.findRfidTags();
    }

}
