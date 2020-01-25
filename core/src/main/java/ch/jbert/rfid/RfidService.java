package ch.jbert.rfid;

import java.beans.PropertyChangeListener;

public interface RfidService {

    void addListener(PropertyChangeListener listener);

    void scan();

}
