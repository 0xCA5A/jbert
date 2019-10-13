package rfid;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class RfidTagDetectionListener implements RfidTagAction, PropertyChangeListener {

    public void propertyChange(PropertyChangeEvent event) {
        final RfidTagUid rfidTagUid = (RfidTagUid) event.getNewValue();
        rfidTagAction(rfidTagUid);
    }
}
