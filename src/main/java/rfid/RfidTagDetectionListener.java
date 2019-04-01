package rfid;

import util.GeneralException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class RfidTagDetectionListener implements RfidTagAction, PropertyChangeListener {

    public void propertyChange(PropertyChangeEvent evt) {
        Object object = evt.getNewValue();
        try {
            rfidTagAction((RfidTagUid) object);
        } catch (ClassCastException exception) {
            throw new GeneralException(exception);
        }
    }
}
